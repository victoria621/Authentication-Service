package com.innowise.authenticationservice.client;

import com.innowise.authenticationservice.dto.CreateUserProfileRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Component
public class UserServiceClient {

    private static final Logger log = LoggerFactory.getLogger(UserServiceClient.class);
    private final WebClient webClient;

    public UserServiceClient(@Value("${user.service.url:http://localhost:8080}") String userServiceUrl) {
        this.webClient = WebClient.builder()
                .baseUrl(userServiceUrl)
                .build();
    }

    public void createUserProfile(CreateUserProfileRequest request) {
        try {
            webClient.post()
                    .uri("/api/users")
                    .bodyValue(request)
                    .retrieve()
                    .toBodilessEntity()
                    .block();

            log.info("User profile created successfully for userId: {}", request.userId());
        } catch (WebClientResponseException e) {
            log.error("User Service error: status={}, body={}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new RuntimeException("User Service error: " + e.getStatusCode());
        } catch (Exception e) {
            log.error("Error calling User Service: {}", e.getMessage());
            throw new RuntimeException("User Service unavailable: " + e.getMessage());
        }
    }
}