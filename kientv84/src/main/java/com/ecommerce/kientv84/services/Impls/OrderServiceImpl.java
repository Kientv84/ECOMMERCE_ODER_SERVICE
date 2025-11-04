package com.ecommerce.kientv84.services.Impls;

import com.ecommerce.kientv84.Intergration.PaymentClient;
import com.ecommerce.kientv84.Intergration.ProductClient;
import com.ecommerce.kientv84.dtos.requests.ItemRequest;
import com.ecommerce.kientv84.dtos.requests.OrderRequest;
import com.ecommerce.kientv84.dtos.requests.OrderUpdateRequest;
import com.ecommerce.kientv84.dtos.responses.OrderResponse;
import com.ecommerce.kientv84.dtos.responses.clients.PaymentMethodResponse;
import com.ecommerce.kientv84.dtos.responses.clients.ProductClientResponse;
import com.ecommerce.kientv84.dtos.responses.kafka.KafkaPaymentResponse;
import com.ecommerce.kientv84.entities.OrderEntity;
import com.ecommerce.kientv84.entities.OrderItemEntity;
import com.ecommerce.kientv84.enums.OrderStatus;
import com.ecommerce.kientv84.enums.PaymentStatus;
import com.ecommerce.kientv84.exceptions.EnumError;
import com.ecommerce.kientv84.exceptions.ServiceException;
import com.ecommerce.kientv84.mappers.OrderItemMapper;
import com.ecommerce.kientv84.messaging.producer.OrderProducer;
import com.ecommerce.kientv84.repositories.OrderItemRepository;
import com.ecommerce.kientv84.mappers.OrderMapper;
import com.ecommerce.kientv84.repositories.OrderRepository;
import com.ecommerce.kientv84.services.OrderService;
import com.ecommerce.kientv84.utils.KafkaObjectError;
import feign.FeignException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    private final static String timestamp = "timestamp";

    @Override
    public List<OrderResponse> getAllOrder() {
        try {
            List<OrderResponse> responses = orderRepository.findAll().stream().map(or -> orderMapper.mapToOrderResponse(or)).toList();

            return responses;

        } catch (Exception e) {
            throw new ServiceException(EnumError.ORDER_GET_ERROR, "order.get.error");
        }
    }

    @Transactional
    @Override
    public OrderResponse createOrder(OrderRequest request) {
        log.info("[createOrder] start create order ....");
        try {
            // Init order
            OrderEntity orderEntity = OrderEntity.builder()
                    .userId(request.getUserId())
                    .orderCode(UUID.randomUUID().toString().substring(0, 8))
                    .status(OrderStatus.CREATED)
                    .paymentStatus(PaymentStatus.PENDING)
                    .paymentMethod(request.getPaymentMethod())
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
        try {
            OrderEntity order = orderRepository.findById(id).orElseThrow(() -> new ServiceException(EnumError.ORDER_GET_ERROR, "order.get.error"));

            return  orderMapper.mapToOrderResponse(order);
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

            orderRepository.save(order);

            return  orderMapper.mapToOrderResponse(order);
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

            List<OrderEntity> foundIds = orderRepository.findAllById(ids);

            System.out.println("Find collection:" + foundIds.toString());

            if ( foundIds.isEmpty()) {
                throw new ServiceException(EnumError.ORDER_ERR_NOT_FOUND, "order.delete.notfound");
            }

            orderRepository.deleteAllById(ids);

            return "Deleted collections successfully: {}" + ids;

        } catch (Exception e) {
            throw new ServiceException(EnumError.INTERNAL_ERROR, "sys.internal.error");
        }
    }

    @Override
    @Transactional
    public void listenPaymentService(KafkaPaymentResponse kafkaPaymentResponse) {
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

            order.setPaymentStatus(newStatus);

            // Nếu thanh toán thành công (hoặc COD_PENDING), cập nhật order status
            if (newStatus == PaymentStatus.PAID || newStatus == PaymentStatus.COD_PENDING) {
                //TODO:Set staus order để tracking,  gọi shipping service
            }
            // Nếu thanh toán thất bại → hủy đơn
            else if (newStatus == PaymentStatus.FAILED) {
                //TODO:Set staus order để FAILED,  gọi noti service
            }

        } catch (Exception e) {
            log.error("[listenPaymentService] Error: {}", e.getMessage(), e);
            KafkaObjectError kafkaError = new KafkaObjectError("OS-002", null, e.getMessage());
            orderProducer.produceMessageError(kafkaError);
            throw new ServiceException(EnumError.INTERNAL_ERROR, "sys.internal.error");
        }
    }
}
