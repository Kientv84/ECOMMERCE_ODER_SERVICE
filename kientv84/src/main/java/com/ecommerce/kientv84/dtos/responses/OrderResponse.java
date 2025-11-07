package com.ecommerce.kientv84.dtos.responses;

import com.ecommerce.kientv84.dtos.requests.ItemRequest;
import com.ecommerce.kientv84.entities.ShippingMethodEntity;
import com.ecommerce.kientv84.enums.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderResponse {
    private UUID id;
    private UUID userId;
    private String orderCode;
    private UUID paymentMethod;
    private String shippingAddress;
    private String paymentStatus;
    private String status;
    private String phone;
    private String email;
    private ShippingMethodResponse shippingMethod;
    private BigDecimal totalPrice;
    private List<OrderItemResponse> items;
}

