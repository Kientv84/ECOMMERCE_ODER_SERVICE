package com.ecommerce.kientv84.messaging.producer;

import com.ecommerce.kientv84.commons.EventType;
import com.ecommerce.kientv84.dtos.responses.kafka.*;
import com.ecommerce.kientv84.properties.KafkaTopicProperties;
import com.ecommerce.kientv84.services.KafkaService;
import com.ecommerce.kientv84.utils.KafkaObjectError;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderProducerImpl implements OrderProducer {
    private final KafkaTopicProperties kafkaTopicProperties;
    private final KafkaService kafkaService;

    @Override
    public void produceOrderEventSuccess(KafkaOrderResponse kafkaOrderResponse) {
        var topic = kafkaTopicProperties.getOrderCreated();
        log.info("[produceOrderEventSuccess] producing order to topic {}", topic);

        KafkaEvent<KafkaOrderResponse> message = KafkaEvent.<KafkaOrderResponse>builder()
                .metadata(EventMetadata.builder()
                        .eventId(UUID.randomUUID())
                        .eventType(EventType.ORDER_CREATED.name())
                        .source("order-service")
                        .version(1)
                        .build())
                .payload(kafkaOrderResponse)
                .build();
        kafkaService.send(topic, message);
    }

    @Override
    public void produceMessageError(KafkaObjectError kafkaObject) {
        var topic = kafkaTopicProperties.getErrorOrder();
        log.info("[produceMessageError] producing error to topic {}", topic);
        kafkaService.send(topic, kafkaObject);
    }

    @Override
    public void produceOrderEventShipping(KafkaOrderShippingResponse kafkaOrderShippingResponse) {
        var topic = kafkaTopicProperties.getOrderReadyForShipping();
        log.info("[produceOrderEventShipping] producing order ready for shipping to topic {}", topic);

        KafkaEvent<KafkaOrderShippingResponse> message = KafkaEvent.<KafkaOrderShippingResponse>builder()
                .metadata(EventMetadata.builder()
                        .eventId(UUID.randomUUID())
                        .eventType(EventType.ORDER_READY_FOR_SHIPPING.name())
                        .source("order-service")
                        .version(1)
                        .build())
                .payload(kafkaOrderShippingResponse)
                .build();

        kafkaService.send(topic, message);
    }

    @Override
    public void produceMessageOrderEventUpdatePayment(KafkaPaymentUpdated kafkaPaymentUpdated) {
        var topic = kafkaTopicProperties.getOrderEventPaymentUpdated();
        log.info("[produceMessageOrderEventUpdatePayment] producing order event to update status payment {}", topic);

        KafkaEvent<KafkaPaymentUpdated> message = KafkaEvent.<KafkaPaymentUpdated>builder()
                .metadata(EventMetadata.builder()
                        .eventId(UUID.randomUUID())
                        .eventType(EventType.ORDER_PAYMENT_UPDATED.name())
                        .source("order-service")
                        .version(1)
                        .build())
                .payload(kafkaPaymentUpdated)
                .build();

        kafkaService.send(topic, message);
    }
}
