package com.innowise.authenticationservice.dto;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ItemResponse(
        Long id,
        String name,
        String description,
        @NotNull
        BigDecimal price,
        LocalDateTime createdAt
) {
}
