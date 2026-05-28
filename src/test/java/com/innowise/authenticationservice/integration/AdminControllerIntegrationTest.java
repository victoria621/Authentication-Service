package com.innowise.authenticationservice.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.innowise.authenticationservice.dto.AuthResponse;
import com.innowise.authenticationservice.dto.UserRequest;
import com.innowise.authenticationservice.dto.UserResponse;
import com.innowise.authenticationservice.entity.Role;
import com.innowise.authenticationservice.entity.User;
import com.innowise.authenticationservice.repository.RefreshTokenRepository;
import com.innowise.authenticationservice.repository.UserRepository;
import com.innowise.authenticationservice.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class AdminControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    private String adminAccessToken;

    @BeforeEach
    void setUp() throws Exception {
        refreshTokenRepository.deleteAll();
        userRepository.deleteAll();

        UserRequest registerRequest = new UserRequest("adminuser", "adminpass");
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk());

        User adminUser = userRepository.findByLogin("adminuser").orElseThrow();
        adminUser.setRole(Role.ADMIN);
        userRepository.save(adminUser);

        MvcResult loginResult = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk())
                .andReturn();

        AuthResponse authResponse = objectMapper.readValue(
                loginResult.getResponse().getContentAsString(),
                AuthResponse.class);

        adminAccessToken = authResponse.accessToken();

    }

    @Test
    void getAllUsers_ShouldReturnUserList() throws Exception {

        UserRequest userRequest = new UserRequest("regular", "pass123");
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequest)))
                .andExpect(status().isOk());

        MvcResult result = mockMvc.perform(get("/admin/users")
                        .header("Authorization", "Bearer " + adminAccessToken))
                .andExpect(status().isOk())
                .andReturn();

        UserResponse[] users = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                UserResponse[].class);

        assertThat(users).hasSize(2);
        assertThat(users).extracting(UserResponse::login).contains("adminuser", "regular");
    }

    @Test
    void activateUser_ShouldActivateUser() throws Exception {
        UserRequest userRequest = new UserRequest("inactiveuser", "pass123");
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequest)))
                .andExpect(status().isOk());

        User user = userRepository.findByLogin("inactiveuser").orElseThrow();
        user.setActive(false);
        userRepository.save(user);

        mockMvc.perform(patch("/admin/users/{id}/activate", user.getId())
                        .header("Authorization", "Bearer " + adminAccessToken))
                .andExpect(status().isNoContent());

        User updatedUser = userRepository.findById(user.getId()).orElseThrow();
        assertThat(updatedUser.isActive()).isTrue();
    }

    @Test
    void deactivateUser_ShouldDeactivateUser() throws Exception {
        UserRequest userRequest = new UserRequest("activeuser", "pass123");
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequest)))
                .andExpect(status().isOk());

        User user = userRepository.findByLogin("activeuser").orElseThrow();
        assertThat(user.isActive()).isTrue();

        mockMvc.perform(patch("/admin/users/{id}/deactivate", user.getId())
                        .header("Authorization", "Bearer " + adminAccessToken))
                .andExpect(status().isNoContent());

        User updatedUser = userRepository.findById(user.getId()).orElseThrow();
        assertThat(updatedUser.isActive()).isFalse();
    }

    @ParameterizedTest
    @ValueSource(strings = {"/cards", "/orders", "/payments"})
    void getAllResources_ShouldReturnEmptyList(String endpoint) throws Exception {
        MvcResult result = mockMvc.perform(get("/admin" + endpoint)
                        .header("Authorization", "Bearer " + adminAccessToken))
                .andExpect(status().isOk())
                .andReturn();

        String response = result.getResponse().getContentAsString();
        assertThat(response).isEqualTo("[]");
    }

    @Test
    void getAllUsers_WithoutToken_ShouldReturnUnauthorized() throws Exception {
        mockMvc.perform(get("/admin/users"))
                .andExpect(status().isForbidden());
    }

    @Test
    void getAllUsers_WithUserToken_ShouldReturnForbidden() throws Exception {
        UserRequest userRequest = new UserRequest("normaluser", "pass123");
        MvcResult registerResult = mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequest)))
                .andExpect(status().isOk())
                .andReturn();

        AuthResponse authResponse = objectMapper.readValue(
                registerResult.getResponse().getContentAsString(),
                AuthResponse.class);

        String userToken = authResponse.accessToken();

        mockMvc.perform(get("/admin/users")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isForbidden());
    }
}