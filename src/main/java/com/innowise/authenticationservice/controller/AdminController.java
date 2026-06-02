package com.innowise.authenticationservice.controller;

import com.innowise.authenticationservice.dto.UserResponse;
import com.innowise.authenticationservice.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.innowise.authenticationservice.dto.*;

import java.util.List;

/**
 * REST controller for administrative operations.
 * Provides endpoints for managing users, orders, cards, payments and items.
 * Accessible only to users with ADMIN role.
 */
@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;
    private static final Logger log = LoggerFactory.getLogger(AdminController.class);

    /**
     * Retrieves all registered users.
     *
     * @return list of all users
     */
    @GetMapping("/users")
    public List<UserResponse> getAllUsers(){
        log.info("getAllUsers");
        return adminService.getAllUsers();
    }

    /**
     * Activates a user account by ID.
     *
     * @param id the ID of the user to activate
     * @return HTTP 204 No Content response
     */
    @PatchMapping("/users/{id}/activate")
    public ResponseEntity<Void> activateUser(@PathVariable("id") Long id) {
        log.info("Activate User");
        adminService.activateUser(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Deactivates a user account by ID.
     *
     * @param id the ID of the user to deactivate
     * @return HTTP 204 No Content response
     */
    @PatchMapping("/users/{id}/deactivate")
    public ResponseEntity<Void> deactivateUser(@PathVariable("id") Long id) {
        log.info("Deactivate User");
        adminService.deactivateUser(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Retrieves all orders.
     *
     * @return list of all orders
     */
    @GetMapping("/orders")
    public List<OrderResponse> getAllOrders() {
        log.info("Getting all orders");
        return adminService.getAllOrders();
    }

    /**
     * Retrieves all cards.
     *
     * @return list of all cards
     */
    @GetMapping("/cards")
    public List<CardResponse> getAllCards() {
        log.info("Getting all cards");
        return adminService.getAllCards();
    }

    /**
     * Retrieves all payments.
     *
     * @return list of all payments
     */
    @GetMapping("/payments")
    public List<PaymentResponse> getAllPayments() {
        log.info("Getting all payments");
        return adminService.getAllPayments();
    }

    /**
     * Creates a new item.
     *
     * @param request the item creation request
     * @return the created item
     */
    @PostMapping("/items")
    public ItemResponse createItem(@RequestBody ItemRequest request) {
        log.info("Creating new item");
        return adminService.createItem(request);
    }

    /**
     * Updates an existing item by ID.
     *
     * @param id the ID of the item to update
     * @param request the item update request
     * @return the updated item
     */
    @PutMapping("/items/{id}")
    public ItemResponse updateItem(@PathVariable Long id, @RequestBody ItemRequest request) {
        log.info("Updating item with id: {}", id);
        return adminService.updateItem(id, request);
    }

    /**
     * Deletes an item by ID.
     *
     * @param id the ID of the item to delete
     * @return HTTP 204 No Content response
     */
    @DeleteMapping("/items/{id}")
    public ResponseEntity<Void> deleteItem(@PathVariable Long id) {
        log.info("Deleting item with id: {}", id);
        adminService.deleteItem(id);
        return ResponseEntity.noContent().build();
    }
}