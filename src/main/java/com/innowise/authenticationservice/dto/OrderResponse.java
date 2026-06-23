package com.innowise.authenticationservice.dto;

import com.innowise.authenticationservice.entity.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record OrderResponse(
        Long id,
        Long userId,
        BigDecimal totalPrice,
        OrderStatus status,
        LocalDateTime createdAt
) {
}
