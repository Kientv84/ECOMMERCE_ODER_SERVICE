package com.ecommerce.kientv84.dtos.responses.clients;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Setter
@Getter
@AllArgsConstructor
public class PaymentMethodResponse {
    private UUID id;
    private String code; // "COD", "MOMO", "VNPAY", ...
    private String name; // Ví dụ: "Thanh toán khi nhận hàng"
    private String status;
    private String description;
}

