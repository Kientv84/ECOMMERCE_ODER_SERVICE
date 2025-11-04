package com.ecommerce.kientv84.messaging.producer;

import com.ecommerce.kientv84.dtos.responses.kafka.KafkaOrderResponse;
import com.ecommerce.kientv84.dtos.responses.kafka.KafkaOrderShippingResponse;
import com.ecommerce.kientv84.properties.KafkaTopicProperties;
import com.ecommerce.kientv84.services.KafkaService;
import com.ecommerce.kientv84.utils.KafkaObjectError;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderProducerImpl implements OrderProducer {
    private final KafkaTopicProperties kafkaTopicProperties;
    private final KafkaService kafkaService;

    @Override
    public void produceOrderEventSuccess(KafkaOrderResponse message) {
        var topic = kafkaTopicProperties.getOrderCreated();
        log.info("[produceOrderEventSuccess] producing order to topic {}", topic);
        kafkaService.send(topic, message);
    }

    @Override
    public void produceMessageError(KafkaObjectError kafkaObject) {
        var topic = kafkaTopicProperties.getErrorOrder();
        log.info("[produceMessageError] producing error to topic {}", topic);
        kafkaService.send(topic, kafkaObject);
    }

    @Override
    public void produceOrderEventShipping(KafkaOrderShippingResponse message) {
        var topic = kafkaTopicProperties.getOrderReadyForShipping();
        log.info("[produceOrderEventShipping] producing order ready for shipping to topic {}", topic);
        kafkaService.send(topic, message);
    }
}
