package com.ecommerce.kientv84.Intergration;

import com.ecommerce.kientv84.dtos.responses.clients.PaymentMethodResponse;
import com.ecommerce.kientv84.dtos.responses.clients.PaymentResponse;
import com.ecommerce.kientv84.dtos.responses.clients.requests.PaymentUpdateRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.UUID;

@FeignClient(url = "${spring.cloud.paymentClient.client.config.paymentClient.url}", name = "${openfeign.paymentClient.name}")
public interface PaymentClient {
    @PostMapping(value = "${openfeign.paymentClient.url.update-payment-by-id}", consumes = "application/json")
    PaymentResponse updatePaymentById(@PathVariable UUID orderId, @RequestBody PaymentUpdateRequest request);
}
