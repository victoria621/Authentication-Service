package com.innowise.authenticationservice.controller;

import com.innowise.authenticationservice.dto.CardResponse;
import com.innowise.authenticationservice.dto.OrderResponse;
import com.innowise.authenticationservice.dto.PaymentResponse;
import com.innowise.authenticationservice.dto.UserResponse;
import com.innowise.authenticationservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST controller for user operations.
 * Provides endpoints for authenticated users to access their own data.
 */
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    /**
     * Retrieves the currently authenticated user's profile.
     *
     * @return the current user's information
     */
    @GetMapping
    public UserResponse getCurrentUser() {
        Long userId = getCurrentUserId();
        log.info("Getting current user with id: {}", userId);
        return userService.getUserById(userId);
    }

    /**
     * Retrieves all cards belonging to the currently authenticated user.
     *
     * @return list of the user's cards
     */
    @GetMapping("/cards")
    public List<CardResponse> getMyCards() {
        return userService.getMyCards(getCurrentUserId());
    }

    /**
     * Retrieves all orders belonging to the currently authenticated user.
     *
     * @return list of the user's orders
     */
    @GetMapping("/orders")
    public List<OrderResponse> getMyOrders() {
        return userService.getMyOrders(getCurrentUserId());
    }

    /**
     * Retrieves all payments belonging to the currently authenticated user.
     *
     * @return list of the user's payments
     */
    @GetMapping("/payments")
    public List<PaymentResponse> getMyPayments() {
        return userService.getMyPayments(getCurrentUserId());
    }

    /**
     * Extracts the current authenticated user ID from the security context.
     *
     * @return the current user ID
     */
    private Long getCurrentUserId() {
        return (Long) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();
    }
}