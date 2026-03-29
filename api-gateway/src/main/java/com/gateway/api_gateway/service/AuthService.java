package com.gateway.api_gateway.service;

import com.gateway.api_gateway.config.AuthProperties;
import com.gateway.api_gateway.dto.AuthRequest;
import com.gateway.api_gateway.dto.AuthResponse;
import com.gateway.api_gateway.exception.InvalidCredentialsException;
import com.gateway.api_gateway.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthProperties authProperties;
    private final JwtService jwtService;

    public AuthResponse login(AuthRequest authRequest) {
        if (!authProperties.username().equals(authRequest.username())
                || !authProperties.password().equals(authRequest.password())) {
            throw new InvalidCredentialsException("Invalid username or password.");
        }

        return new AuthResponse(
                jwtService.generateToken(authRequest.username()),
                jwtService.getExpirationMs()
        );
    }
}
