package com.ecommerce.kientv84.config;

import com.ecommerce.kientv84.components.FeignJwtForwardInterceptor;
import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignConfig {

    @Bean
    public RequestInterceptor jwtForwardInterceptor() {
        return new FeignJwtForwardInterceptor();
    }
}
