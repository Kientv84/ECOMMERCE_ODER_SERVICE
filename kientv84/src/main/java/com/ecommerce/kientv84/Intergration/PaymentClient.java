package com.ecommerce.kientv84.Intergration;

import com.ecommerce.kientv84.dtos.responses.clients.requests.PaymentUpdateRequest;
import com.ecommerce.kientv84.dtos.responses.kafka.KafkaPaymentResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.UUID;

@FeignClient(url = "${spring.cloud.paymentClient.client.config.paymentClient.url}", name = "${openfeign.paymentClient.name}")
public interface PaymentClient {
    @PostMapping(value = "${openfeign.paymentClient.url.update-payment-by-id}", consumes = "application/json")
    KafkaPaymentResponse updatePaymentById(@PathVariable UUID orderId, @RequestBody PaymentUpdateRequest request);
}
