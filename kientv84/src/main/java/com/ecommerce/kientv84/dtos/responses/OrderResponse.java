package com.ecommerce.kientv84.dtos.responses;

import com.ecommerce.kientv84.dtos.requests.ItemRequest;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
@Setter
@Getter
@AllArgsConstructor
public class OrderResponse {
    private UUID userId;
    private String orderCode;
    private UUID paymentMethod;
    private String shippingAddress;
    private BigDecimal totalPrice;
    private List<OrderItemResponse> items;
}

