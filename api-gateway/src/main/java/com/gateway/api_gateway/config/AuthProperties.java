package com.gateway.api_gateway.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.auth")
public record AuthProperties(
        String username,
        String password,
        String secret,
        long expirationMs
) {
}
