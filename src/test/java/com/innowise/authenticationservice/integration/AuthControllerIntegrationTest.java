package com.innowise.authenticationservice.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.innowise.authenticationservice.dto.AuthResponse;
import com.innowise.authenticationservice.dto.UserRequest;
import com.innowise.authenticationservice.dto.ValidateTokenRequest;
import com.innowise.authenticationservice.dto.ValidateTokenResponse;
import com.innowise.authenticationservice.repository.RefreshTokenRepository;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RefreshTokenRepository  refreshTokenRepository;

    @BeforeEach
    void setUp() {
        refreshTokenRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void register_ShouldCreateUserAndReturnTokens() throws Exception {
        UserRequest request = new UserRequest("testuser", "password123");

        MvcResult result = mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn();

        String responseJson = result.getResponse().getContentAsString();
        AuthResponse response = objectMapper.readValue(responseJson, AuthResponse.class);

        assertThat(response.accessToken()).isNotNull();
        assertThat(response.refreshToken()).isNotNull();

        assertThat(userRepository.findByLogin("testuser")).isPresent();
    }

    @Test
    void register_ShouldReturnConflict_WhenLoginExists() throws Exception {
        UserRequest request = new UserRequest("existing", "password123");

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }

    @Test
    void login_ShouldReturnTokens_WhenCredentialsValid() throws Exception {
        UserRequest registerRequest = new UserRequest("loginuser", "mypass");
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk());

        MvcResult result = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk())
                .andReturn();

        AuthResponse response = objectMapper.readValue(result.getResponse().getContentAsString(), AuthResponse.class);
        assertThat(response.accessToken()).isNotNull();
        assertThat(response.refreshToken()).isNotNull();
    }

    @Test
    void login_ShouldReturnUnauthorized_WhenWrongPassword() throws Exception {
        UserRequest registerRequest = new UserRequest("wrongpassuser", "correct");
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk());

        UserRequest wrongRequest = new UserRequest("wrongpassuser", "wrong");

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(wrongRequest)))
                .andExpect(status().isConflict());
    }


    @Test
    void refresh_ShouldReturnNewTokens_WhenRefreshTokenValid() throws Exception {
        UserRequest request = new UserRequest("refreshuser", "pass");
        MvcResult registerResult = mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn();

        AuthResponse registerResponse = objectMapper.readValue(
                registerResult.getResponse().getContentAsString(),
                AuthResponse.class);


        String refreshToken = registerResponse.refreshToken();

        MvcResult refreshResult = mockMvc.perform(post("/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"refreshToken\": \"" + refreshToken + "\"}"))
                .andExpect(status().isOk())
                .andReturn();

        AuthResponse refreshResponse = objectMapper.readValue(
                refreshResult.getResponse().getContentAsString(),
                AuthResponse.class);

        assertThat(refreshResponse.accessToken()).isNotNull();
        assertThat(refreshResponse.refreshToken()).isNotNull();
        assertThat(refreshResponse.refreshToken()).isNotEqualTo(refreshToken);
    }

    @Test
    void validate_ShouldReturnTrue_WhenTokenIsValid() throws Exception {
        UserRequest request = new UserRequest("validateuser", "password123");
        MvcResult registerResult = mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn();

        AuthResponse registerResponse = objectMapper.readValue(
                registerResult.getResponse().getContentAsString(),
                AuthResponse.class);

        String accessToken = registerResponse.accessToken();
        ValidateTokenRequest validateRequest = new ValidateTokenRequest(accessToken);

        MvcResult validateResult = mockMvc.perform(post("/auth/validate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validateRequest)))
                .andExpect(status().isOk())
                .andReturn();

        ValidateTokenResponse validateResponse = objectMapper.readValue(
                validateResult.getResponse().getContentAsString(),
                ValidateTokenResponse.class);

        assertThat(validateResponse.valid()).isTrue();
    }

    @Test
    void validate_ShouldReturnFalse_WhenTokenIsInvalid() throws Exception {
        ValidateTokenRequest validateRequest = new ValidateTokenRequest("invalid.token.string");

        MvcResult validateResult = mockMvc.perform(post("/auth/validate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validateRequest)))
                .andExpect(status().isOk())
                .andReturn();

        ValidateTokenResponse validateResponse = objectMapper.readValue(
                validateResult.getResponse().getContentAsString(),
                ValidateTokenResponse.class);

        assertThat(validateResponse.valid()).isFalse();
    }
}