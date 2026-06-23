package com.innowise.authenticationservice.dto;

import com.innowise.authenticationservice.entity.Role;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UserResponse(
        @NotNull
        Long id,
        @NotBlank
        String login,
        Role role,
        boolean active
) {

}
