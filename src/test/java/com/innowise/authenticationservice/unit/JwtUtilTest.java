package com.innowise.authenticationservice.unit;

import com.innowise.authenticationservice.config.JwtConfig;
import com.innowise.authenticationservice.entity.Role;
import com.innowise.authenticationservice.entity.User;
import com.innowise.authenticationservice.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.test.context.ActiveProfiles;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
class JwtUtilTest {

    private JwtUtil jwtUtil;
    private User testUser;

    @BeforeEach
    void setUp() {
        JwtConfig jwtConfig = new JwtConfig();
        jwtConfig.setSecret("testSuperSecretKeyForTestingOnly1234567890");
        jwtConfig.setAccessExpiration(900000L);
        jwtConfig.setRefreshExpiration(604800000L);

        jwtUtil = new JwtUtil(jwtConfig);

        testUser = new User();
        testUser.setId(1L);
        testUser.setLogin("testuser");
        testUser.setRole(Role.USER);
        testUser.setActive(true);
    }

    @Test
    void generateAccessToken_ShouldReturnValidToken() {
        String token = jwtUtil.generateAccessToken(testUser);
        assertNotNull(token);
        assertEquals(3, token.split("\\.").length);
    }

    @Test
    void generateAccessToken_ShouldHaveCorrectSubject() {
        String token = jwtUtil.generateAccessToken(testUser);
        Long userId = jwtUtil.extractUserId(token);
        assertEquals(1L, userId);
    }

    @Test
    void generateAccessToken_ShouldHaveCorrectRole() {
        String token = jwtUtil.generateAccessToken(testUser);
        String role = jwtUtil.extractRole(token);
        assertEquals("USER", role);
    }

    @Test
    void generateAccessToken_ShouldHaveFutureExpiration() {
        String token = jwtUtil.generateAccessToken(testUser);
        Date expiration = jwtUtil.extractExpiration(token);
        assertTrue(expiration.after(new Date()));
    }

    @Test
    void generateRefreshToken_ShouldReturnValidToken() {
        String token = jwtUtil.generateRefreshToken(testUser);
        assertNotNull(token);
        assertEquals(3, token.split("\\.").length);
    }

    @Test
    void refreshToken_ShouldHaveLongerExpirationThanAccessToken() {
        String accessToken = jwtUtil.generateAccessToken(testUser);
        String refreshToken = jwtUtil.generateRefreshToken(testUser);
        Date accessExp = jwtUtil.extractExpiration(accessToken);
        Date refreshExp = jwtUtil.extractExpiration(refreshToken);
        assertTrue(refreshExp.after(accessExp));
    }

    @Test
    void extractRole_FromRefreshToken_ShouldReturnNull() {
        String token = jwtUtil.generateRefreshToken(testUser);
        String role = jwtUtil.extractRole(token);
        assertNull(role);
    }

    @Test
    void validateToken_ValidToken_ShouldReturnTrue() {
        String token = jwtUtil.generateAccessToken(testUser);
        assertTrue(jwtUtil.validateToken(token));
    }

    @ParameterizedTest
    @ValueSource(strings = {"invalid.token.string", "eyJhbGciOiJIUzI1NiJ9.malformed", "", "null"})
    void validateToken_InvalidTokens_ShouldReturnFalse(String token) {
        assertFalse(jwtUtil.validateToken(token));
    }
}