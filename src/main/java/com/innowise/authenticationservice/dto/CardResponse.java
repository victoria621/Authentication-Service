package com.innowise.authenticationservice.dto;

import jakarta.persistence.Column;

public record CardResponse(
        Long id,
        Long userId,
        String cardNumber,
        String expiryDate,
        String cardHolderName
) {
}
