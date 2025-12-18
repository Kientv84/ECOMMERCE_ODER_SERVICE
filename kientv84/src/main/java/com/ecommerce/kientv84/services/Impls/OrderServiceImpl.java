package com.ecommerce.kientv84.services.Impls;

import com.ecommerce.kientv84.Intergration.PaymentClient;
import com.ecommerce.kientv84.Intergration.ProductClient;
import com.ecommerce.kientv84.commons.Constant;
import com.ecommerce.kientv84.dtos.requests.ItemRequest;
import com.ecommerce.kientv84.dtos.requests.OrderRequest;
import com.ecommerce.kientv84.dtos.requests.OrderUpdateRequest;
import com.ecommerce.kientv84.dtos.requests.search.order.OrderSearchModel;
import com.ecommerce.kientv84.dtos.requests.search.order.OrderSearchOption;
import com.ecommerce.kientv84.dtos.requests.search.order.OrderSearchRequest;
import com.ecommerce.kientv84.dtos.responses.OrderResponse;
import com.ecommerce.kientv84.dtos.responses.PagedResponse;
import com.ecommerce.kientv84.dtos.responses.clients.ProductClientResponse;
import com.ecommerce.kientv84.dtos.responses.kafka.KafkaEvent;
import com.ecommerce.kientv84.dtos.responses.kafka.KafkaOrderResponse;
import com.ecommerce.kientv84.dtos.responses.kafka.KafkaPaymentResponse;
import com.ecommerce.kientv84.entities.OrderEntity;
import com.ecommerce.kientv84.entities.OrderItemEntity;
import com.ecommerce.kientv84.entities.ShippingMethodEntity;
import com.ecommerce.kientv84.enums.OrderStatus;
import com.ecommerce.kientv84.enums.PaymentStatus;
import com.ecommerce.kientv84.exceptions.EnumError;
import com.ecommerce.kientv84.exceptions.ServiceException;
import com.ecommerce.kientv84.messaging.producer.OrderProducer;
import com.ecommerce.kientv84.mappers.OrderMapper;
import com.ecommerce.kientv84.repositories.OrderRepository;
import com.ecommerce.kientv84.repositories.ShippingMethodRepository;
import com.ecommerce.kientv84.services.OrderService;
import com.ecommerce.kientv84.services.RedisService;
import com.ecommerce.kientv84.utils.KafkaObjectError;
import com.ecommerce.kientv84.utils.PageableUtils;
import com.ecommerce.kientv84.utils.SpecificationBuilder;
import com.fasterxml.jackson.core.type.TypeReference;
import feign.FeignException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderMapper orderMapper;
    private final OrderRepository orderRepository;
    private final ProductClient productClient;
    private final OrderProducer orderProducer;
    private final ShippingMethodRepository shippingMethodRepository;
    private final PaymentClient paymentClient;
    private final RedisService redisService;

    private final static String timestamp = "timestamp";

    @Override
    public PagedResponse<OrderResponse> getAllOrder(OrderSearchRequest request) {
        log.info("Get all order api calling...");
        String key = "orders:list:" + request.hashKey();

        try {
            PagedResponse<OrderResponse> cached = redisService.getValue(key, new TypeReference<PagedResponse<OrderResponse>>() {
            });

            if (cached != null) {
                log.info("Redis read for key {}", key);
                return cached;
            }

            OrderSearchOption option = request.getOrderSearchOption();
            OrderSearchModel model = request.getOrderSearchModel();

            List<String> allowedFields = List.of("orderCode", "createdDate");

            PageRequest pageRequest = PageableUtils.buildPageRequest(
                    option.getPage(),
                    option.getSize(),
                    option.getSort(),
                    allowedFields,
                    "createdDate",
                    Sort.Direction.DESC
            );

            Specification<OrderEntity> spec = new SpecificationBuilder<OrderEntity>()
                    .equal("status", model.getStatus())
                    .likeAnyFieldIgnoreCase(model.getQ(), "orderCode")
                    .build();

            Page<OrderResponse> result = orderRepository.findAll(spec, pageRequest)
                    .map(orderMapper::mapToOrderResponse);

            PagedResponse<OrderResponse> response = new PagedResponse<>(
                    result.getNumber(),
                    result.getSize(),
                    result.getTotalElements(),
                    result.getTotalPages(),
                    result.getContent()
            );

            redisService.setValue(key, response, Constant.SEARCH_CACHE_TTL);

            log.info("Redis MISS, caching search result for key {}", key);

            return response;

        } catch (Exception e) {
            log.error("Error get all orders", e);
            throw new ServiceException(EnumError.ORDER_GET_ERROR, "order.get.error");
        }
    }

    @Override
    public List<OrderResponse> searchOrderSuggestion(String q, int limit) {
        List<OrderEntity> orders = orderRepository.searchOrderSuggestion(q, limit);
        return orders.stream().map(or -> orderMapper.mapToOrderResponse(or)).toList();
    }

    @Transactional
    @Override
    public OrderResponse createOrder(OrderRequest request) {
        log.info("[createOrder] start create order ....");
        try {
            ShippingMethodEntity shippingMethod = shippingMethodRepository.findById(request.getShippingMethod()).orElseThrow(() -> new ServiceException(EnumError.SHIPPING_METHOD_GET_ERROR, "shipping.method.get.err"));


            // Init order
            OrderEntity orderEntity = OrderEntity.builder()
                    .userId(request.getUserId())
                    .orderCode(UUID.randomUUID().toString().substring(0, 8))
                    .status(OrderStatus.PROCESSING)
                    .paymentStatus(PaymentStatus.PENDING)
                    .paymentMethod(request.getPaymentMethod())
                    .email(request.getEmail())
                    .phone(request.getPhone())
                    .shippingMethod(shippingMethod)
                    .shippingAddress(request.getShippingAddress())
                    .build();

            // Handling items
            ArrayList<OrderItemEntity> orderItems = new ArrayList<>();
            BigDecimal totalPrice = BigDecimal.ZERO;

            for (ItemRequest itemRequest : request.getItems()) {
                BigDecimal lineTotal = itemRequest.getBasePrice()
                        .multiply(BigDecimal.valueOf(itemRequest.getQuantity()));

                // --- Calling Product Service internal api ---
                ProductClientResponse product;
                try {
                    product = productClient.getProductById(itemRequest.getProductId());
                } catch (FeignException.NotFound e) {
                    log.error("Product not found: {}", itemRequest.getProductId(), e);
                    throw new ServiceException(EnumError.PRODUCT_NOT_FOUND, "product.not.found");
                } catch (FeignException e) {
                    log.error("Error calling Product Service", e);
                    throw new ServiceException(EnumError.PRODUCT_SERVICE_UNAVAILABLE, "product.service.unavailable");
                }

                totalPrice = totalPrice.add(lineTotal);

                // Tạo order entity
                OrderItemEntity item = OrderItemEntity.builder()
                        .order(orderEntity)
                        .productId(itemRequest.getProductId())
                        .productName(product.getProductName())
                        .quantity(itemRequest.getQuantity())
                        .lineTotal(lineTotal)
                        .productPrice(product.getBasePrice())
                        .build();

                orderItems.add(item);
            }

            orderEntity.setItems(orderItems);
            orderEntity.setTotalPrice(totalPrice);

            OrderEntity savedOrder =  orderRepository.save(orderEntity);

            OrderResponse response = orderMapper.mapToOrderResponse(savedOrder);

            // redis handle
            redisService.deleteByKey("orders:list:*");

            // producer message lên kafka

            orderProducer.produceOrderEventSuccess(orderMapper.mapToKafkaOrderResponse(response));

            return response;
        } catch (ServiceException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error creating order", e);

            // Produce error message
            log.error("[createOrder] Error: {}", e.getMessage(), e);
            KafkaObjectError kafkaObjectError = new KafkaObjectError("OS-001", null, e.getMessage());
            orderProducer.produceMessageError(kafkaObjectError);

            throw new ServiceException(EnumError.INTERNAL_ERROR, "sys.internal.error");
        }
    }

    @Override
    public OrderResponse getOrderById(UUID id) {
        log.info("Calling get by id api with order {}", id);

        String key = "order:"+id;

        try {

            // get from cache
            OrderResponse cached = redisService.getValue(key, OrderResponse.class);

            if (cached != null) {
                log.info("Redis get for key: {}", key);
                return cached;
            }
            OrderEntity order = orderRepository.findById(id).orElseThrow(() -> new ServiceException(EnumError.ORDER_GET_ERROR, "order.get.error"));

            OrderResponse response = orderMapper.mapToOrderResponse(order);

            // storge redis
            redisService.setValue(key, response, Constant.CACHE_TTL);

            return  response;
        } catch (ServiceException e) {
            throw e;
        }
        catch (Exception e) {
            throw new ServiceException(EnumError.INTERNAL_ERROR, "sys.internal.error");
        }
    }

    @Override
    public OrderResponse updateOrderById(UUID id, OrderUpdateRequest updateRequest) {
        try {
            OrderEntity order = orderRepository.findById(id).orElseThrow(() -> new ServiceException(EnumError.ORDER_GET_ERROR, "order.get.error"));

            if ( updateRequest.getShippingAddress() != null) {
                order.setShippingAddress(updateRequest.getShippingAddress());
            }
            if(updateRequest.getStatus() != null) {
                order.setStatus(updateRequest.getStatus());
            }

            OrderEntity saved =  orderRepository.save(order);

            // Invalidate cache
            String key = "order:" + id;
            redisService.deleteByKey(key);

            redisService.deleteByKeys("order:" + id, "orders:list:*");

            log.info("Cache invalidated for key {}", key);

            return  orderMapper.mapToOrderResponse(saved);
        } catch (ServiceException e) {
            throw e;
        }
        catch (Exception e) {
            throw new ServiceException(EnumError.INTERNAL_ERROR, "sys.internal.error");
        }
    }

    @Override
    public String deleteOrder(List<UUID> ids) {
        try {
            if ( ids == null || ids.isEmpty()) {
                throw new ServiceException(EnumError.ORDER_ERR_DEL_EM, "order.delete.empty");
            }

            // Lấy tất cả brand tồn tại
            List<OrderEntity> orders = orderRepository.findAllById(ids);

            if (orders.isEmpty()) {
                throw new ServiceException(EnumError.ORDER_ERR_NOT_FOUND, "order.delete.notfound");
            }

            // Soft delete:  update status
            orders.forEach(br -> br.setStatus(OrderStatus.CANCELED));
            orderRepository.saveAll(orders);

            //dete cache
            ids.forEach(uuid -> redisService.deleteByKey("order:"+uuid));
            redisService.deleteByKeys("orders:list:*");

            log.info("Deleted orders successfully and cache invalidated: {}", ids);
            return "Deleted orders successfully: " + ids;

        } catch (Exception e) {
            throw new ServiceException(EnumError.INTERNAL_ERROR, "sys.internal.error");
        }
    }

    @Override
    @Transactional
    public void listenPaymentShipCode(KafkaPaymentResponse kafkaPaymentResponse) {
        if (kafkaPaymentResponse == null) {
            log.error("[listenPaymentShipCode] Missing KafkaPaymentResponse data");
            return;
        }

        log.info("[listenPaymentShipCode] Processing payment event: {}", kafkaPaymentResponse);

        try {
            OrderEntity order = orderRepository.findById(kafkaPaymentResponse.getOrderId())
                    .orElseThrow(() -> new ServiceException(EnumError.ORDER_ERR_NOT_FOUND, "order.not.found"));

            // Update status payment từ message
            PaymentStatus newStatus = PaymentStatus.valueOf(kafkaPaymentResponse.getStatus());

            if (newStatus == PaymentStatus.COD_PENDING) {
                //TODO: Update status
                order.setPaymentStatus(newStatus);

                //TODO: Gọi shipping Service
                orderProducer.produceOrderEventShipping(orderMapper.mapToKafkaOrderShippingResponse(order));
            }


        } catch (Exception e) {
            log.error("[listenPaymentService] Error: {}", e.getMessage(), e);
            KafkaObjectError kafkaError = new KafkaObjectError("OS-002", null, e.getMessage());
            orderProducer.produceMessageError(kafkaError);
            throw new ServiceException(EnumError.INTERNAL_ERROR, "sys.internal.error");
        }
    }
    @Override
    @Transactional
    public void listenPaymentFailed(KafkaPaymentResponse kafkaPaymentResponse) {
        if (kafkaPaymentResponse == null) {
            log.error("[listenPaymentService] Missing KafkaPaymentResponse data");
            return;
        }

        log.info("[listenPaymentService] Processing payment event: {}", kafkaPaymentResponse);

        try {
            OrderEntity order = orderRepository.findById(kafkaPaymentResponse.getOrderId())
                    .orElseThrow(() -> new ServiceException(EnumError.ORDER_ERR_NOT_FOUND, "order.not.found"));

            // Update status payment từ message
            PaymentStatus newStatus = PaymentStatus.valueOf(kafkaPaymentResponse.getStatus());

            if (newStatus == PaymentStatus.FAILED) {
                //TODO:Set staus order để FAILED,  gọi noti service, gọi inventory để hồi số lượng
                order.setPaymentStatus(newStatus);
            }

        } catch (Exception e) {
            log.error("[listenPaymentService] Error: {}", e.getMessage(), e);
            KafkaObjectError kafkaError = new KafkaObjectError("OS-002", null, e.getMessage());
            orderProducer.produceMessageError(kafkaError);
            throw new ServiceException(EnumError.INTERNAL_ERROR, "sys.internal.error");
        }
    }

    @Override
    @Transactional
    public void listenPaymentSuccess(KafkaPaymentResponse kafkaPaymentResponse) {
        if (kafkaPaymentResponse == null) {
            log.error("[listenPaymentSuccess] Missing KafkaPaymentResponse data");
            return;
        }

        log.info("[listenPaymentSuccess] Processing payment event: {}", kafkaPaymentResponse);

        try {
            OrderEntity order = orderRepository.findById(kafkaPaymentResponse.getOrderId())
                    .orElseThrow(() -> new ServiceException(EnumError.ORDER_ERR_NOT_FOUND, "order.not.found"));

            // Update status payment từ message
            PaymentStatus newStatus = PaymentStatus.valueOf(kafkaPaymentResponse.getStatus());

            if (newStatus == PaymentStatus.PAID) {
                //TODO: Update status
                order.setPaymentStatus(newStatus);

                //TODO: Gọi shipping Service
                orderProducer.produceOrderEventShipping(orderMapper.mapToKafkaOrderShippingResponse(order));
            }


        } catch (Exception e) {
            log.error("[listenPaymentService] Error: {}", e.getMessage(), e);
            KafkaObjectError kafkaError = new KafkaObjectError("OS-002", null, e.getMessage());
            orderProducer.produceMessageError(kafkaError);
            throw new ServiceException(EnumError.INTERNAL_ERROR, "sys.internal.error");
        }
    }

    @Transactional
    @Override
    public void updateOrderStatusFromShipping(UUID orderId, String status) {
       try {
           log.info("Tiến hành update status");
           OrderEntity order = orderRepository.findById(orderId)
                   .orElseThrow(() -> new ServiceException(EnumError.ORDER_ERR_NOT_FOUND, "order.not.found"));

           OrderStatus newStatus = OrderStatus.valueOf(status.trim().toUpperCase());
           log.info("Parsed status successfully: {}", newStatus);

           order.setStatus(newStatus);

           orderRepository.save(order);

           // Nếu giao hàng thành công => phát sự kiện để Payment service cập nhật trạng thái "PAID"
           if (newStatus == OrderStatus.DELIVERED) {
               log.info("[updateOrderStatusFromShipping] Order delivered -> Producing received event...");
               orderProducer.produceMessageOrderEventUpdatePayment(orderMapper.mapToKafkaPaymentUpdated(order));
           }

           log.info("[OrderService] Updated order {} to status {}", orderId, newStatus);
       } catch (ServiceException e) {
           throw e;
       } catch (Exception e) {
           throw new ServiceException(EnumError.INTERNAL_ERROR, "sys.internal.error");
       }
    }
}
