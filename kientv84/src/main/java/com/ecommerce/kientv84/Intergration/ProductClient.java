package com.ecommerce.kientv84.Intergration;

import com.ecommerce.kientv84.dtos.responses.clients.ProductClientResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "${openfeign.productClient.name}",  url = "${spring.cloud.productClient.client.config.productClient.url}")
public interface ProductClient {
    @GetMapping(
            value = "${openfeign.productClient.url.get-product}",
            consumes = "application/json")
    ProductClientResponse getProductById(UUID productId);

}
