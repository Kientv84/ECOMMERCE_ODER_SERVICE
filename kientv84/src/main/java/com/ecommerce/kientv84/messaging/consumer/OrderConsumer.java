package com.ecommerce.kientv84.messaging.consumer;

import com.ecommerce.kientv84.dtos.responses.kafka.KafkaPaymentResponse;
import com.ecommerce.kientv84.services.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Slf4j
public class OrderConsumer {
    private final OrderService orderService;

    @KafkaListener(topics = "${spring.kafka.payment.topic.payment-checked}", groupId = "spring.kafka.payment.group", containerFactory = "kafkaListenerContainerFactory")
    public void onMessageHandler(@Payload KafkaPaymentResponse message) {
        try {
            log.info("[onMessageHandler] Start consuming message ...");
            log.info("[onMessageHandler] Received message payload: {}", message);
            orderService.listenPaymentService(message);
        } catch (Exception e) {
            log.error("[onMessageHandler] Error. Err {}", e.getMessage());
        }
    }
}
