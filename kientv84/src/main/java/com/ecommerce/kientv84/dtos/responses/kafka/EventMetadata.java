package com.ecommerce.kientv84.dtos.responses.kafka;

import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EventMetadata {
    private UUID eventId;            // duy nhất toàn hệ thống
    private String eventType;         // ORDER_CREATED, ORDER_PAID...
    private String source;            // order-service
    private int version;              // version của event schema
}

