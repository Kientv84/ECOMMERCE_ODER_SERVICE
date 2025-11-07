package com.ecommerce.kientv84.messaging.consumer;

import com.ecommerce.kientv84.dtos.responses.kafka.KafkaOrderShippingResponse;
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
public class OrderUpdatedConsumer {
    private final OrderService orderService;

    @KafkaListener(topics = "${spring.kafka.order.topic.order-updated}", groupId = "spring.kafka.shipping.group", containerFactory = "kafkaListenerContainerFactory")
    public void onMessageHandler(@Payload String message) {
        try {
            log.info("[onMessageHandler] Start consuming message ...");
            log.info("[onMessageHandler] Received message payload: {}", message);

            KafkaOrderShippingResponse response = new ObjectMapper().readValue(message, KafkaOrderShippingResponse.class);

            orderService.updateOrderStatusFromShipping(response.getId(), response.getStatus());
        } catch (Exception e) {
            log.error("[onMessageHandler] Error. Err {}", e.getMessage());
        }
    }
}

