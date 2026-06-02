package com.innowise.authenticationservice.dto;

import jakarta.validation.constraints.NotBlank;

public record ValidateTokenRequest(
        @NotBlank
        String token
) {
}