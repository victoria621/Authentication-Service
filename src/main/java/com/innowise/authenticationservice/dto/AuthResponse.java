package com.innowise.authenticationservice.dto;

public record AuthResponse(
        String accessToken,
        String refreshToken
) {
}
