package com.ecommerce.kientv84.dtos.responses;

import com.ecommerce.kientv84.dtos.requests.ItemRequest;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;
@Setter
@Getter
@AllArgsConstructor
public class OrderResponse {
    private UUID userId;
    private ItemRequest item;
    private UUID paymentMethod;
    private String shippingAddress;
}

