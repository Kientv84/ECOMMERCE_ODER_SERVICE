package com.ecommerce.kientv84.dtos.responses;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ShippingMethodResponse {
    private UUID id;
    private String shippingCode;
    private String shippingName;
    private BigDecimal baseFee;
    private String description;
    private Boolean status;
}

