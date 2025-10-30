package com.ecommerce.kientv84.dtos.requests;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Setter
@Getter
@AllArgsConstructor
public class ItemRequest {
    @NotNull(message = "{item.productid.notnull}")
    private UUID productId;
    @NotNull(message = "{item.quantity.notnull}")
    private Integer quantity;
    @NotNull(message = "{item.basePrice.notnull}")
    private BigDecimal basePrice;
}
