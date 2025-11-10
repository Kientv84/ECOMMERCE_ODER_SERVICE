package com.ecommerce.kientv84.dtos.responses.kafka;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class KafkaPaymentMethodResponse {
    private UUID id;
    private String code; // "COD", "MOMO", "VNPAY", ...
    private String name; // Ví dụ: "Thanh toán khi nhận hàng"
    private String status;
    private String description;
}

