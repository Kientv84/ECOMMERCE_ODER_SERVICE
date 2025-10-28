package com.ecommerce.kientv84.config.JWT;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtConfig {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long expiration;

    @Value("${jwt.issuer}")
    private String issuer;

    public String getSecret() {
        return secret;
    }

    public long getExpiration() { // Thời gian hết hạn
        return expiration;
    }

    public String getIssuer() {
        return issuer;
    }
}

