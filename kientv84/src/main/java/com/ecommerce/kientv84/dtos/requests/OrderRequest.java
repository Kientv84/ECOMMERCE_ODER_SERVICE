package com.ecommerce.kientv84.dtos.requests;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;
@Setter
@Getter
@AllArgsConstructor
public class OrderRequest {
    @NotNull(message = "{order.userid.notnull}")
    private UUID userId;
    @NotNull(message = "{order.items.notnull}")
    private List<ItemRequest> items;
    @NotNull(message = "{order.payment.notnull}")
    private UUID paymentMethod;
    @NotNull(message = "{order.address.notnull}")
    private String shippingAddress;
}
