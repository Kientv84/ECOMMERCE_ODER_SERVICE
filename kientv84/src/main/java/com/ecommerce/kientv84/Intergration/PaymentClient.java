package com.ecommerce.kientv84.Intergration;

import com.ecommerce.kientv84.dtos.responses.clients.PaymentMethodResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(url = "${spring.cloud.paymentClient.client.config.paymentClient.url}", name = "${openfeign.paymentClient.name}")
public interface PaymentClient {
    @GetMapping(value = "${openfeign.paymentClient.url.get-payment-method}", consumes = "${openfeign.content-type}")
    PaymentMethodResponse getPaymentMethodById(@PathVariable UUID paymentMethodId);
}
