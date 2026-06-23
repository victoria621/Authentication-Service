package com.innowise.authenticationservice.dto;

import jakarta.validation.constraints.NotBlank;

public record UserRequest(
        @NotBlank
        String login,
        @NotBlank
        String password
) {
}
