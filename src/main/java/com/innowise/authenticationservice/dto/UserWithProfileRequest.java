package com.innowise.authenticationservice.dto;

public record UserWithProfileRequest(
        String login,
        String password,
        String email,
        String firstName,
        String lastName
) {}