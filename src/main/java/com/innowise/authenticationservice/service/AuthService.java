package com.innowise.authenticationservice.service;

import com.innowise.authenticationservice.dto.AuthResponse;
import com.innowise.authenticationservice.dto.RefreshTokenRequest;
import com.innowise.authenticationservice.dto.UserRequest;
import com.innowise.authenticationservice.dto.UserWithProfileRequest;

/**
 * Service interface for authentication operations.
 * Handles user registration, login, token refresh and token validation.
 */
public interface AuthService {

    /**
     * Registers a new user in the system.
     *
     * @param userRequest the user registration request containing login and password
     * @return authentication response with access and refresh tokens
     * @throws com.innowise.authenticationservice.exception.BusinessException if login already exists
     */
    AuthResponse register(UserRequest userRequest);

    /**
     * Authenticates a user with login and password.
     *
     * @param userRequest the login request containing login and password
     * @return authentication response with access and refresh tokens
     * @throws com.innowise.authenticationservice.exception.BusinessException if user not found or password is wrong
     */
    AuthResponse login(UserRequest userRequest);

    /**
     * Refreshes an expired access token using a valid refresh token.
     *
     * @param request the refresh token request containing the refresh token
     * @return authentication response with new access and refresh tokens
     * @throws com.innowise.authenticationservice.exception.ResourceNotFoundException if refresh token not found
     * @throws com.innowise.authenticationservice.exception.BusinessException if refresh token is expired
     */
    AuthResponse refresh(RefreshTokenRequest request);

    /**
     * Validates an access token.
     *
     * @param token the token to validate
     * @return true if token is valid, false otherwise
     */
    boolean validateToken(String token);

    /**
     * Registers a new user with automatic profile creation in User Service.
     * This method performs a transactional registration:
     * <ol>
     *   <li>Saves user credentials in Authentication Service database</li>
     *   <li>Calls User Service to create user profile (email, firstName, lastName)</li>
     *   <li>If User Service call fails, rolls back the user deletion from Authentication Service</li>
     * </ol>
     *
     * @param request the registration request containing login, password and profile data
     * @return authentication response with access and refresh tokens
     * @throws com.innowise.authenticationservice.exception.BusinessException if login already exists
     * @throws RuntimeException if User Service call fails (triggers rollback)
     */
    AuthResponse registerWithRollback(UserWithProfileRequest request);
}