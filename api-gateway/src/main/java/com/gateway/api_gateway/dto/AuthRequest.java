package com.gateway.api_gateway.dto;

public record AuthRequest(
        String username,
        String password
) {
}
