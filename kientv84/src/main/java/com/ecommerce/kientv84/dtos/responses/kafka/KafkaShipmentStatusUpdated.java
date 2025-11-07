package com.ecommerce.kientv84.dtos.responses.kafka;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class KafkaShipmentStatusUpdated {
    private UUID orderId;
    private UUID shipmentId;
    private String newStatus;
    private String trackingCode;
}
