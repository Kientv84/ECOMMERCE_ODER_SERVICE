package com.ecommerce.kientv84.dtos.responses.clients;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Setter
@Getter
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProductClientResponse {
    private UUID id;
    private String productName;
    private BigDecimal basePrice;
    private Float discountPercent;
    private Integer stock;
}
