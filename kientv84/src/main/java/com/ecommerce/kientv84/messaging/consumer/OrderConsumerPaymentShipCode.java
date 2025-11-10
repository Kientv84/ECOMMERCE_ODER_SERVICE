package com.ecommerce.kientv84.messaging.consumer;

import com.ecommerce.kientv84.dtos.responses.kafka.KafkaPaymentResponse;
import com.ecommerce.kientv84.services.OrderService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Slf4j
public class OrderConsumerPaymentShipCode {
    private final OrderService orderService;

    @KafkaListener(topics = "${spring.kafka.payment.topic.payment-cod-pending}", groupId = "${spring.kafka.payment.group}", containerFactory = "kafkaListenerContainerFactory")
    public void onMessageHandlerPaymentShipCode(@Payload String message) {
        try {
            log.info("[onMessageHandlerPaymentShipCode] Start consuming message ...");
            log.info("[onMessageHandlerPaymentShipCode] Received message payload: {}", message);

            KafkaPaymentResponse response = new ObjectMapper().readValue(message, KafkaPaymentResponse.class);
            orderService.listenPaymentShipCode(response);
        } catch (Exception e) {
            log.error("[onMessageHandlerPaymentShipCode] Error. Err {}", e.getMessage());
        }
    }
}
