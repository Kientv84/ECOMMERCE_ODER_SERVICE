package com.ecommerce.kientv84.dtos.responses.kafka;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class KafkaEvent<T> {
    private EventMetadata metadata;
    private T payload;
}

