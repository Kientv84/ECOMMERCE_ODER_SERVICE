package com.ecommerce.kientv84.dtos.requests;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Setter
@Getter
@AllArgsConstructor
public class ItemRequest {
    private UUID productId;
    private Integer quantity;
    private BigDecimal basePrice;
}
