package com.innowise.authenticationservice.dto;


public record CardResponse(
        Long id,
        Long userId,
        String cardNumber,
        String expiryDate,
        String cardHolderName
) {

}
