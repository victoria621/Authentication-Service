package com.innowise.authenticationservice.controller;

import com.innowise.authenticationservice.dto.UserResponse;
import com.innowise.authenticationservice.entity.User;
import com.innowise.authenticationservice.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.innowise.authenticationservice.dto.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;
    private static final Logger log = LoggerFactory.getLogger(AdminController.class);

    @GetMapping("/users")
    public List<UserResponse> getAllUsers(){
        log.info("getAllUsers");
        return adminService.getAllUsers();
    }

    @PatchMapping("/users/{id}/activate")
    public ResponseEntity<Void> activateUser(
            @PathVariable("id") Long id
    ) {
        log.info("Activate User");
        adminService.activateUser(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/users/{id}/deactivate")
    public ResponseEntity<Void> deactivateUser(
            @PathVariable("id") Long id
    ) {
        log.info("Deactivate User");
        adminService.deactivateUser(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/orders")
    public List<OrderResponse> getAllOrders() {
        log.info("Getting all orders");
        return adminService.getAllOrders();
    }

    @GetMapping("/cards")
    public List<CardResponse> getAllCards() {
        log.info("Getting all cards");
        return adminService.getAllCards();
    }

    @GetMapping("/payments")
    public List<PaymentResponse> getAllPayments() {
        log.info("Getting all payments");
        return adminService.getAllPayments();
    }

    @PostMapping("/items")
    public ItemResponse createItem(@RequestBody ItemRequest request) {
        log.info("Creating new item");
        return adminService.createItem(request);
    }

    @PutMapping("/items/{id}")
    public ItemResponse updateItem(@PathVariable Long id, @RequestBody ItemRequest request) {
        log.info("Updating item with id: {}", id);
        return adminService.updateItem(id, request);
    }

    @DeleteMapping("/items/{id}")
    public ResponseEntity<Void> deleteItem(@PathVariable Long id) {
        log.info("Deleting item with id: {}", id);
        adminService.deleteItem(id);
        return ResponseEntity.noContent().build();
    }
}
