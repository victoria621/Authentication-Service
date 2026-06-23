package com.innowise.authenticationservice.dto;

import com.innowise.authenticationservice.entity.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PaymentResponse(
        Long id,
        Long orderId,
        BigDecimal amount,
        PaymentStatus status,
        String paymentMethod,
        LocalDateTime createdAt
) {
}
