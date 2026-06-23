package com.innowise.authenticationservice.dto;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record ItemRequest(
        String name,
        String description,
        @NotNull
        BigDecimal price
) {
}