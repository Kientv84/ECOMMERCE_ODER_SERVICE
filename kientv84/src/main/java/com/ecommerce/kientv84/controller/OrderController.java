package com.ecommerce.kientv84.controller;

import com.ecommerce.kientv84.dtos.requests.OrderRequest;
import com.ecommerce.kientv84.dtos.requests.OrderUpdateRequest;
import com.ecommerce.kientv84.dtos.responses.OrderResponse;
import com.ecommerce.kientv84.services.OrderService;
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

    @GetMapping("/ordes")
    public ResponseEntity<List<OrderResponse>> getAllOrder() {
        return ResponseEntity.ok(orderService.getAllOrder());
    }

    @PostMapping("/order")
    public ResponseEntity<OrderResponse> createOrder(@RequestBody OrderRequest request) {
        return ResponseEntity.ok(orderService.createOrder(request));
    }

    @GetMapping("/order/{id}")
    public ResponseEntity<OrderResponse> getOrderById(@PathVariable UUID id) {
        return ResponseEntity.ok(orderService.getOrderById(id));
    }

    @PostMapping("/order/{id}")
    public ResponseEntity<OrderResponse> updateOrderById(@PathVariable UUID id,@RequestBody OrderUpdateRequest updateRequest) {
        return ResponseEntity.ok(orderService.updateOrderById(id, updateRequest));
    }

    @PostMapping("orders")
    public String deleteOrder(@RequestBody List<UUID> uuids) {
        return orderService.deleteOrder(uuids);
    }

}
