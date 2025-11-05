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
public class ShippingMethodRequest {
    @NotNull(message = "{shipping.method.code.notnull}")
    private String shippingCode;
    @NotNull(message = "{shipping.method.name.notnull}")
    private String shippingName;
    @NotNull(message = "{shipping.method.baseFee.notnull}")
    private BigDecimal baseFee;
    private String description;
    @NotNull(message = "{shipping.method.status.notnull}")
    private Boolean status;
}

