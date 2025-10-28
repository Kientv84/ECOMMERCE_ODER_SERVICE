package com.ecommerce.kientv84.dtos.requests;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;
@Setter
@Getter
@AllArgsConstructor
public class OrderRequest {
    private UUID userId;
    private List<ItemRequest> items;
    private UUID paymentMethod;
    private String shippingAddress;
}
