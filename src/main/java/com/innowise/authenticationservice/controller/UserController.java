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

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    @GetMapping
    public UserResponse getCurrentUser() {
        Long userId = (Long) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        log.info("Getting current user with id: {}", userId);
        return userService.getUserById(userId);
    }

    @GetMapping("/cards")
    public List<CardResponse> getMyCards() {
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userService.getMyCards(userId);
    }

    @GetMapping("/orders")
    public List<OrderResponse> getMyOrders() {
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userService.getMyOrders(userId);
    }

    @GetMapping("/payments")
    public List<PaymentResponse> getMyPayments() {
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userService.getMyPayments(userId);
    }
}
