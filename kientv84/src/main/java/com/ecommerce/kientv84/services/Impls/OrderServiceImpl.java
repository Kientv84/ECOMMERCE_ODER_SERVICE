package com.ecommerce.kientv84.services.Impls;

import com.ecommerce.kientv84.Intergration.ProductClient;
import com.ecommerce.kientv84.dtos.requests.ItemRequest;
import com.ecommerce.kientv84.dtos.requests.OrderRequest;
import com.ecommerce.kientv84.dtos.requests.OrderUpdateRequest;
import com.ecommerce.kientv84.dtos.responses.OrderResponse;
import com.ecommerce.kientv84.dtos.responses.clients.ProductClientResponse;
import com.ecommerce.kientv84.entities.OrderEntity;
import com.ecommerce.kientv84.entities.OrderItemEntity;
import com.ecommerce.kientv84.enums.OrderStatus;
import com.ecommerce.kientv84.exceptions.EnumError;
import com.ecommerce.kientv84.exceptions.ServiceException;
import com.ecommerce.kientv84.mappers.OrderItemMapper;
import com.ecommerce.kientv84.repositories.OrderItemRepository;
import com.ecommerce.kientv84.mappers.OrderMapper;
import com.ecommerce.kientv84.repositories.OrderRepository;
import com.ecommerce.kientv84.services.OrderService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderMapper orderMapper;
    private final OrderRepository orderRepository;
    private final OrderItemMapper orderItemMapper;
    private final ProductClient productClient;
    private final OrderItemRepository orderItemRepository;

    @Override
    public List<OrderResponse> getAllOrder() {
        try {
            List<OrderResponse> responses = orderRepository.findAll().stream().map(or -> orderMapper.mapToOrderResponse(or)).toList();

            return responses;

        } catch (Exception e) {
            throw new ServiceException(EnumError.ORDER_GET_ERROR, "order.get.error");
        }
    }

    @Transactional // Đánh dấu đây kà 1 transaction chạy logic để thao tác với dữ liệu,  đảm bảo tính ACID, nếu có @transactional thì khi 1 chuỗi thao tác đó có vấn đề thì sẽ rollback toàn bộ, đảm bảo tính atomicity
    @Override
    public OrderResponse createOrder(OrderRequest request) {
        try {

            // Init order
            OrderEntity orderEntity = OrderEntity.builder()
                    .userId(request.getUserId())
                    .orderCode(UUID.randomUUID().toString().substring(0, 8))
                    .status(OrderStatus.PENDING)
                    .shippingAddress(request.getShippingAddress())
                    .build();

            // Handling items
            ArrayList<OrderItemEntity> orderItems = new ArrayList<>();
            BigDecimal totalPrice = BigDecimal.ZERO;

            for (ItemRequest itemRequest : request.getItems()) {
                BigDecimal lineTotal = itemRequest.getBasePrice()
                        .multiply(BigDecimal.valueOf(itemRequest.getQuantity()));

                // Gọi api get product By Id từ product service để get dữ liệu

                ProductClientResponse product = productClient.getProductById(itemRequest.getProductId());

                totalPrice = totalPrice.add(lineTotal);

                // Tạo order entity
                OrderItemEntity item = OrderItemEntity.builder()
                        .order(orderEntity)
                        .productId(itemRequest.getProductId())
                        .productName(product.getProductName())
                        .quantity(itemRequest.getQuantity())
                        .lineTotal(lineTotal)
                        .build();

                orderItems.add(item);
            }

            orderEntity.setOrderItems(orderItems);
            orderEntity.setTotalPrice(totalPrice);

            OrderEntity savedOrder =  orderRepository.save(orderEntity);

            // producer message lên kfka

            return orderMapper.mapToOrderResponse(savedOrder);
        } catch (ServiceException e) {
            throw e;
        } catch (Exception e) {
            throw new ServiceException(EnumError.INTERNAL_ERROR, "sys.internal.error");
        }
    }

    @Override
    public OrderResponse getOrderById(UUID id) {
        return null;
    }

    @Override
    public OrderResponse updateOrderById(UUID id, OrderUpdateRequest updateRequest) {
        return null;
    }

    @Override
    public String deleteOrder(List<UUID> ids) {
        return "";
    }
}
