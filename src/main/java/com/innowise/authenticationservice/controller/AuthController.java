package com.innowise.authenticationservice.controller;

import com.innowise.authenticationservice.dto.*;
import com.innowise.authenticationservice.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for authentication operations.
 * Provides endpoints for user registration, login, token refresh and token validation.
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    /**
     * Registers a new user.
     *
     * @param request the user registration request containing login and password
     * @return authentication response with access and refresh tokens
     */
    @PostMapping("/register")
    public AuthResponse register(@RequestBody UserRequest request) {
        log.info("Registering user with login: {}", request.login());
        return authService.register(request);
    }

    /**
     * Authenticates a user and returns tokens.
     *
     * @param request the login request containing login and password
     * @return authentication response with access and refresh tokens
     */
    @PostMapping("/login")
    public AuthResponse login(@RequestBody UserRequest request) {
        log.info("User logged in: {}", request.login());
        return authService.login(request);
    }

    /**
     * Refreshes an expired access token using a valid refresh token.
     *
     * @param request the refresh token request containing the refresh token
     * @return authentication response with new access and refresh tokens
     */
    @PostMapping("/refresh")
    public AuthResponse refresh(@RequestBody RefreshTokenRequest request) {
        log.info("Refreshing token for request: {}", request.refreshToken());
        return authService.refresh(request);
    }

    /**
     * Validates an access token.
     *
     * @param request the validation request containing the token to validate
     * @return validation response indicating whether the token is valid
     */
    @PostMapping("/validate")
    public ValidateTokenResponse validate(@RequestBody ValidateTokenRequest request) {
        log.info("Validating token");
        boolean isValid = authService.validateToken(request.token());
        return new ValidateTokenResponse(isValid);
    }

    /**
     * Registers a new user with automatic profile creation in User Service.
     * @param request the registration request containing login, password and profile data
     * @return authentication response with access and refresh tokens
     * @throws com.innowise.authenticationservice.exception.BusinessException if login already exists or User Service call fails
     */
    @PostMapping("/register-with-rollback")
    public AuthResponse registerWithRollback(@RequestBody UserWithProfileRequest request) {
        log.info("Registering user with rollback: {}", request.login());
        return authService.registerWithRollback(request);
    }
}