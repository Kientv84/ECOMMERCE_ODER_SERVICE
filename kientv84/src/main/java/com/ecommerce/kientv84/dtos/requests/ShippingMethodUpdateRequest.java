package com.ecommerce.kientv84.dtos.requests;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
@Setter
@Getter
@AllArgsConstructor
public class ShippingMethodUpdateRequest {
    private String shippingCode;
    private String shippingName;
    private BigDecimal baseFee;
    private String description;
    private Boolean status;
}
