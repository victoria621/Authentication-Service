package com.innowise.authenticationservice.controller;

import com.innowise.authenticationservice.dto.AuthResponse;
import com.innowise.authenticationservice.dto.RefreshTokenRequest;
import com.innowise.authenticationservice.dto.UserRequest;
import com.innowise.authenticationservice.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    @PostMapping("/register")
    public AuthResponse register(@RequestBody UserRequest request) {
        log.info("Registering user with login: {}", request.login());
        return authService.register(request);
    }

    @PostMapping("/login")
    public AuthResponse login(@RequestBody UserRequest request) {
        log.info("User logged in: {}", request.login());
        return authService.login(request);
    }

    @PostMapping("/refresh")
    public AuthResponse refresh(@RequestBody RefreshTokenRequest request) {
        log.info("Refreshing token for request: {}", request.refreshToken());
        return authService.refresh(request);
    }


}
