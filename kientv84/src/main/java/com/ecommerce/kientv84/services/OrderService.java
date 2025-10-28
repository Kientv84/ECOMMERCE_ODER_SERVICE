package com.ecommerce.kientv84.services;

import com.ecommerce.kientv84.dtos.requests.OrderRequest;
import com.ecommerce.kientv84.dtos.requests.OrderUpdateRequest;
import com.ecommerce.kientv84.dtos.responses.OrderResponse;

import java.util.List;
import java.util.UUID;

public interface OrderService {
    List<OrderResponse> getAllOrder();

    OrderResponse createOrder(OrderRequest request);

    OrderResponse getOrderById(UUID id);

    OrderResponse updateOrderById(UUID id, OrderUpdateRequest updateRequest);

    String deleteOrder(List<UUID> ids);

}
