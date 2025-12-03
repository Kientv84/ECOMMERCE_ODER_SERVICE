package com.ecommerce.kientv84.controller;

import com.ecommerce.kientv84.dtos.requests.OrderRequest;
import com.ecommerce.kientv84.dtos.requests.OrderUpdateRequest;
import com.ecommerce.kientv84.dtos.requests.search.order.OrderSearchRequest;
import com.ecommerce.kientv84.dtos.responses.OrderResponse;
import com.ecommerce.kientv84.dtos.responses.PagedResponse;
import com.ecommerce.kientv84.services.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v1/api")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @PostMapping("/orders/filter")
    public ResponseEntity<PagedResponse<OrderResponse>> getAllOrder(OrderSearchRequest orderSearchRequest) {
        return ResponseEntity.ok(orderService.getAllOrder(orderSearchRequest));
    }

    @GetMapping("/orders/suggestion")
    public ResponseEntity<List<OrderResponse>> getOrderSuggestions(@RequestParam String q,
                                                                   @RequestParam(defaultValue = "5") int limit) {
        return ResponseEntity.ok(orderService.searchOrderSuggestion(q, limit));
    }

    @PostMapping("/order")
    public ResponseEntity<OrderResponse> createOrder(@Valid @RequestBody OrderRequest request) {
        return ResponseEntity.ok(orderService.createOrder(request));
    }

    @GetMapping("/order/{id}")
    public ResponseEntity<OrderResponse> getOrderById(@PathVariable UUID id) {
        return ResponseEntity.ok(orderService.getOrderById(id));
    }

    @PostMapping("/order/{id}")
    public ResponseEntity<OrderResponse> updateOrderById(@PathVariable UUID id, @RequestBody OrderUpdateRequest updateRequest) {
        return ResponseEntity.ok(orderService.updateOrderById(id, updateRequest));
    }

    @PostMapping("orders")
    public String deleteOrder(@RequestBody List<UUID> uuids) {
        return orderService.deleteOrder(uuids);
    }

}
