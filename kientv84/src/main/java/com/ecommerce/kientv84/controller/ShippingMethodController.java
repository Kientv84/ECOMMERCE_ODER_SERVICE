package com.ecommerce.kientv84.controller;

import com.ecommerce.kientv84.dtos.requests.OrderRequest;
import com.ecommerce.kientv84.dtos.requests.OrderUpdateRequest;
import com.ecommerce.kientv84.dtos.requests.ShippingMethodRequest;
import com.ecommerce.kientv84.dtos.requests.ShippingMethodUpdateRequest;
import com.ecommerce.kientv84.dtos.requests.search.shippingMethod.ShippingMethodSearchRequest;
import com.ecommerce.kientv84.dtos.responses.OrderResponse;
import com.ecommerce.kientv84.dtos.responses.PagedResponse;
import com.ecommerce.kientv84.dtos.responses.ShippingMethodResponse;
import com.ecommerce.kientv84.services.OrderService;
import com.ecommerce.kientv84.services.ShippingMethodService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v1/api")
@RequiredArgsConstructor
public class ShippingMethodController {
    private final ShippingMethodService shippingMethodService;

    @PostMapping("/shipping-methods/filter")
    public ResponseEntity<PagedResponse<ShippingMethodResponse>> getAllShippingMethod(ShippingMethodSearchRequest request) {
        return ResponseEntity.ok(shippingMethodService.getAllShippingMethod(request));
    }

    @GetMapping("/shipping-methods/suggestion")
    public ResponseEntity<List<ShippingMethodResponse>> getShippingMethodSuggestions(@RequestParam String q,
                                                                   @RequestParam(defaultValue = "5") int limit) {
        return ResponseEntity.ok(shippingMethodService.searchShippingMethodSuggestion(q, limit));
    }

    @PostMapping("/shipping-method")
    public ResponseEntity<ShippingMethodResponse> createShippingMethod(@Valid @RequestBody ShippingMethodRequest request) {
        return ResponseEntity.ok(shippingMethodService.createShippingMethod(request));
    }

    @GetMapping("/shipping-method/{id}")
    public ResponseEntity<ShippingMethodResponse> getShippingMethodById(@PathVariable UUID id) {
        return ResponseEntity.ok(shippingMethodService.getShippingMethodById(id));
    }

    @PostMapping("/shipping-method/{id}")
    public ResponseEntity<ShippingMethodResponse> updateShippingMethodById(@PathVariable UUID id, @RequestBody ShippingMethodUpdateRequest updateRequest) {
        return ResponseEntity.ok(shippingMethodService.updateShippingMethod(id, updateRequest));
    }

    @PostMapping("shipping-methods")
    public String deleteShippingMethod(@RequestBody List<UUID> uuids) {
        return shippingMethodService.deleteShippingMethod(uuids);
    }

}
