package com.ecommerce.kientv84.services;

import com.ecommerce.kientv84.dtos.requests.OrderRequest;
import com.ecommerce.kientv84.dtos.requests.OrderUpdateRequest;
import com.ecommerce.kientv84.dtos.requests.search.order.OrderSearchRequest;
import com.ecommerce.kientv84.dtos.responses.OrderResponse;
import com.ecommerce.kientv84.dtos.responses.PagedResponse;
import com.ecommerce.kientv84.dtos.responses.kafka.KafkaOrderResponse;
import com.ecommerce.kientv84.dtos.responses.kafka.KafkaPaymentResponse;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface OrderService {
    PagedResponse<OrderResponse> getAllOrder(OrderSearchRequest request);

    List<OrderResponse> searchOrderSuggestion(String q, int limit);

    OrderResponse createOrder(OrderRequest request);

    OrderResponse getOrderById(UUID id);

    OrderResponse updateOrderById(UUID id, OrderUpdateRequest updateRequest);

    String deleteOrder(List<UUID> ids);

    void listenPaymentShipCode(KafkaPaymentResponse kafkaPaymentResponse);

    void listenPaymentSuccess(KafkaPaymentResponse kafkaPaymentResponse);

    void listenPaymentFailed(KafkaPaymentResponse kafkaPaymentResponse);

    void updateOrderStatusFromShipping(UUID orderId, String status);

}
