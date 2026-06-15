package com.innowise.authenticationservice.dto;

public record CreateUserProfileRequest(
        Long userId,
        String login,
        String email,
        String firstName,
        String lastName
) {}