package com.ecommerce.kientv84.dtos.requests;

import com.ecommerce.kientv84.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Setter
@Getter
@AllArgsConstructor
public class OrderUpdateRequest {
    private UUID userId;
    private String orderCode;
    private ItemRequest item;
    private OrderStatus status;
    private UUID paymentMethod;
    private String shippingAddress;
}

