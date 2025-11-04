package com.ecommerce.kientv84.messaging.producer;

import com.ecommerce.kientv84.dtos.responses.kafka.KafkaOrderResponse;
import com.ecommerce.kientv84.dtos.responses.kafka.KafkaOrderShippingResponse;
import com.ecommerce.kientv84.utils.KafkaObjectError;

public interface OrderProducer {
    void produceOrderEventSuccess(KafkaOrderResponse message);

    void produceOrderEventShipping(KafkaOrderShippingResponse message);

    void produceMessageError(KafkaObjectError kafkaObject);
}
