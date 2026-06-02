package com.innowise.authenticationservice.service;

import com.innowise.authenticationservice.dto.*;

import java.util.List;

/**
 * Service interface for administrative operations.
 * Handles management of users, orders, cards, payments and items.
 * Accessible only to users with ADMIN role.
 */
public interface AdminService {

    /**
     * Retrieves all registered users.
     *
     * @return list of all user responses
     */
    List<UserResponse> getAllUsers();

    /**
     * Activates a user account by ID.
     *
     * @param id the user ID
     * @throws com.innowise.authenticationservice.exception.ResourceNotFoundException if user not found
     */
    void activateUser(Long id);

    /**
     * Deactivates a user account by ID.
     *
     * @param id the user ID
     * @throws com.innowise.authenticationservice.exception.ResourceNotFoundException if user not found
     */
    void deactivateUser(Long id);

    /**
     * Retrieves all orders.
     *
     * @return list of all order responses
     */
    List<OrderResponse> getAllOrders();

    /**
     * Retrieves all payments.
     *
     * @return list of all payment responses
     */
    List<PaymentResponse> getAllPayments();

    /**
     * Retrieves all cards.
     *
     * @return list of all card responses
     */
    List<CardResponse> getAllCards();

    /**
     * Creates a new item.
     *
     * @param request the item creation request
     * @return the created item response
     */
    ItemResponse createItem(ItemRequest request);

    /**
     * Updates an existing item by ID.
     *
     * @param id the item ID
     * @param request the item update request
     * @return the updated item response
     * @throws com.innowise.authenticationservice.exception.ResourceNotFoundException if item not found
     */
    ItemResponse updateItem(Long id, ItemRequest request);

    /**
     * Deletes an item by ID.
     *
     * @param id the item ID
     * @throws com.innowise.authenticationservice.exception.ResourceNotFoundException if item not found
     */
    void deleteItem(Long id);
}