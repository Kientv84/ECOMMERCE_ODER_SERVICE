package com.ecommerce.kientv84.dtos.responses.kafka;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
public class KafkaOrderResponse {
    private UUID id;
    private UUID userId;
    private String orderCode;
    private UUID paymentMethod;
    private String shippingAddress;
    private String phone;
    private String email;
    private String shippingMethodCode;
    private BigDecimal totalPrice;
}
