package com.ecommerce.kientv84.dtos.responses.clients;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentResponse {
    private UUID id;
    private UUID orderId;
    private UUID userId;
    private String orderCode;
    private BigDecimal amount;
    private String status;
    private String transactionCode;
    private Date createdDate;
    private Date updateDate;
}
