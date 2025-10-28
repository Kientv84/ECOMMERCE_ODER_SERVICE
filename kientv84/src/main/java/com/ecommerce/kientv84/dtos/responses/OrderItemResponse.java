package com.ecommerce.kientv84.dtos.responses;

import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderItemResponse {
    private UUID productId;
    private String productName;
    private BigDecimal productPrice;
    private Integer quantity;
    private BigDecimal lineTotal;
}

