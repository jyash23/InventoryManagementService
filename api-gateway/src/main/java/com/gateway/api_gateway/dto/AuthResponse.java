package com.gateway.api_gateway.dto;

public record AuthResponse(
        String token,
        long expiresInMs
) {
}
