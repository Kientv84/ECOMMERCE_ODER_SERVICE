package com.ecommerce.kientv84.messagsing.producer;

import com.ecommerce.kientv84.dtos.responses.OrderResponse;
import com.ecommerce.kientv84.utils.KafkaObjectError;

public interface OrderProducer {
    void produceOrderEventSuccess(OrderResponse message);

    void produceMessageError(KafkaObjectError kafkaObject);
}
