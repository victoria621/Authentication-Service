package com.innowise.authenticationservice.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.innowise.authenticationservice.dto.AuthResponse;
import com.innowise.authenticationservice.dto.UserRequest;
import com.innowise.authenticationservice.dto.UserResponse;
import com.innowise.authenticationservice.entity.Role;
import com.innowise.authenticationservice.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class UserControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    private String accessToken;
    private Long userId;

    @BeforeEach
    void setUp() throws Exception {
        userRepository.deleteAll();

        UserRequest registerRequest = new UserRequest("testuser", "password123");

        MvcResult registerResult = mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk())
                .andReturn();

        AuthResponse authResponse = objectMapper.readValue(
                registerResult.getResponse().getContentAsString(),
                AuthResponse.class);

        accessToken = authResponse.accessToken();

        // Получаем userId через отдельный запрос к /users
        MvcResult userResult = mockMvc.perform(get("/users")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andReturn();

        UserResponse userResponse = objectMapper.readValue(
                userResult.getResponse().getContentAsString(),
                UserResponse.class);
        userId = userResponse.id();
    }

    @Test
    void getCurrentUser_ShouldReturnUserInfo() throws Exception {
        MvcResult result = mockMvc.perform(get("/users")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andReturn();

        UserResponse userResponse = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                UserResponse.class);

        assertThat(userResponse.id()).isEqualTo(userId);
        assertThat(userResponse.login()).isEqualTo("testuser");
        assertThat(userResponse.role()).isEqualTo(Role.USER);
        assertThat(userResponse.active()).isTrue();
    }

    @Test
    void getCurrentUser_WithoutToken_ShouldReturnUnauthorized() throws Exception {
        mockMvc.perform(get("/users"))
                .andExpect(status().isForbidden());
    }

    @Test
    void getMyCards_ShouldReturnEmptyList() throws Exception {
        MvcResult result = mockMvc.perform(get("/users/cards")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andReturn();

        String response = result.getResponse().getContentAsString();
        assertThat(response).isEqualTo("[]");
    }

    @Test
    void getMyOrders_ShouldReturnEmptyList() throws Exception {
        MvcResult result = mockMvc.perform(get("/users/orders")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andReturn();

        String response = result.getResponse().getContentAsString();
        assertThat(response).isEqualTo("[]");
    }

    @Test
    void getMyPayments_ShouldReturnEmptyList() throws Exception {
        MvcResult result = mockMvc.perform(get("/users/payments")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andReturn();

        String response = result.getResponse().getContentAsString();
        assertThat(response).isEqualTo("[]");
    }
}