package com.ecommerce.kientv84.messaging.consumer;

import com.ecommerce.kientv84.dtos.responses.kafka.KafkaEvent;
import com.ecommerce.kientv84.dtos.responses.kafka.KafkaPaymentResponse;
import com.ecommerce.kientv84.services.OrderService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Slf4j
public class OrderConsumerPaymentSuccess {
    private final OrderService orderService;

    @KafkaListener(topics = "${spring.kafka.payment.topic.payment-success}", groupId = "${spring.kafka.payment.group}", containerFactory = "kafkaListenerContainerFactory")
    public void onMessageHandlerPaymentSuccess(@Payload String message) {
        try {
            log.info("[onMessageHandlerPaymentSuccess] Start consuming message ...");
            log.info("[onMessageHandlerPaymentSuccess] Received message payload: {}", message);

            ObjectMapper objectMapper = new ObjectMapper();

            KafkaEvent<KafkaPaymentResponse> event =
                    objectMapper.readValue(
                            message,
                            new TypeReference<KafkaEvent<KafkaPaymentResponse>>() {}
                    );

            KafkaPaymentResponse payload = event.getPayload();
            orderService.listenPaymentSuccess(payload);
        } catch (Exception e) {
            log.error("[onMessageHandlerPaymentShipCode] Error. Err {}", e.getMessage());
        }
    }
}