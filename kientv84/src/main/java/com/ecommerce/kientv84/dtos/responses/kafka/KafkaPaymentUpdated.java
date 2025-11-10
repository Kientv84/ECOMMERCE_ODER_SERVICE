package com.ecommerce.kientv84.dtos.responses.kafka;

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
public class KafkaPaymentUpdated {
    private UUID id;
    private String orderCode;
    private UUID paymentMethod;
    private String shippingMethodCode;
    private String Status;
}
