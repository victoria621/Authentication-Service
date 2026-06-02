package com.innowise.authenticationservice.service;

import com.innowise.authenticationservice.dto.CardResponse;
import com.innowise.authenticationservice.dto.OrderResponse;
import com.innowise.authenticationservice.dto.PaymentResponse;
import com.innowise.authenticationservice.dto.UserResponse;

import java.util.List;

/**
 * Service interface for user operations.
 * Handles retrieving user data and related entities.
 */
public interface UserService {

    /**
     * Retrieves a user by their ID.
     *
     * @param id the user ID
     * @return the user response DTO
     * @throws com.innowise.authenticationservice.exception.ResourceNotFoundException if user not found
     */
    UserResponse getUserById(Long id);

    /**
     * Retrieves all cards belonging to a user.
     *
     * @param userId the user ID
     * @return list of card responses
     */
    List<CardResponse> getMyCards(Long userId);

    /**
     * Retrieves all orders belonging to a user.
     *
     * @param userId the user ID
     * @return list of order responses
     */
    List<OrderResponse> getMyOrders(Long userId);

    /**
     * Retrieves all payments belonging to a user.
     *
     * @param userId the user ID
     * @return list of payment responses
     */
    List<PaymentResponse> getMyPayments(Long userId);
}