package com.innowise.authenticationservice.unit;

import com.innowise.authenticationservice.dto.AuthResponse;
import com.innowise.authenticationservice.dto.RefreshTokenRequest;
import com.innowise.authenticationservice.dto.UserRequest;
import com.innowise.authenticationservice.entity.RefreshToken;
import com.innowise.authenticationservice.entity.Role;
import com.innowise.authenticationservice.entity.User;
import com.innowise.authenticationservice.exception.BusinessException;
import com.innowise.authenticationservice.exception.ResourceNotFoundException;
import com.innowise.authenticationservice.repository.RefreshTokenRepository;
import com.innowise.authenticationservice.repository.UserRepository;
import com.innowise.authenticationservice.security.JwtUtil;
import com.innowise.authenticationservice.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCrypt;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthService authService;

    private UserRequest userRequest;
    private User user;
    private String accessToken;
    private String refreshToken;

    @BeforeEach
    void setUp() {
        userRequest = new UserRequest("testuser", "password123");

        user = new User();
        user.setId(1L);
        user.setLogin("testuser");
        user.setPasswordHash(BCrypt.hashpw("password123" + "test-salt", BCrypt.gensalt()));
        user.setSalt("test-salt");
        user.setRole(Role.USER);
        user.setActive(true);

        accessToken = "test.access.token";
        refreshToken = "test.refresh.token";
    }

    @Test
    void register_ShouldCreateUserAndReturnTokens() {
        when(userRepository.findByLogin(userRequest.login())).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(jwtUtil.generateAccessToken(any(User.class))).thenReturn(accessToken);
        when(jwtUtil.generateRefreshToken(any(User.class))).thenReturn(refreshToken);
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenReturn(new RefreshToken());

        AuthResponse response = authService.register(userRequest);

        assertNotNull(response);
        assertEquals(accessToken, response.accessToken());
        assertEquals(refreshToken, response.refreshToken());
        verify(userRepository).save(any(User.class));
        verify(refreshTokenRepository).save(any(RefreshToken.class));
    }

    @Test
    void register_ShouldThrowExceptionWhenLoginExists() {
        when(userRepository.findByLogin(userRequest.login())).thenReturn(Optional.of(user));

        assertThrows(BusinessException.class, () -> authService.register(userRequest));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void login_ShouldReturnTokensWhenCredentialsValid() {
        when(userRepository.findByLogin(userRequest.login())).thenReturn(Optional.of(user));
        when(jwtUtil.generateAccessToken(user)).thenReturn(accessToken);
        when(jwtUtil.generateRefreshToken(user)).thenReturn(refreshToken);
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenReturn(new RefreshToken());

        AuthResponse response = authService.login(userRequest);

        assertNotNull(response);
        assertEquals(accessToken, response.accessToken());
        assertEquals(refreshToken, response.refreshToken());
    }

    @Test
    void login_ShouldThrowExceptionWhenUserNotFound() {
        when(userRepository.findByLogin(userRequest.login())).thenReturn(Optional.empty());

        assertThrows(BusinessException.class, () -> authService.login(userRequest));
    }

    @Test
    void login_ShouldThrowExceptionWhenPasswordWrong() {
        UserRequest wrongPasswordRequest = new UserRequest("testuser", "wrongpassword");
        when(userRepository.findByLogin(wrongPasswordRequest.login())).thenReturn(Optional.of(user));

        assertThrows(BusinessException.class, () -> authService.login(wrongPasswordRequest));
    }

    @Test
    void refresh_ShouldReturnNewTokensWhenTokenValid() {
        RefreshTokenRequest request = new RefreshTokenRequest(refreshToken);
        RefreshToken refreshTokenEntity = new RefreshToken();
        refreshTokenEntity.setToken(refreshToken);
        refreshTokenEntity.setUser(user);
        refreshTokenEntity.setExpiresAt(LocalDateTime.now().plusDays(7));

        String newAccessToken = "new.access.token";
        String newRefreshToken = "new.refresh.token";

        when(refreshTokenRepository.findByToken(refreshToken)).thenReturn(Optional.of(refreshTokenEntity));
        when(jwtUtil.generateAccessToken(user)).thenReturn(newAccessToken);
        when(jwtUtil.generateRefreshToken(user)).thenReturn(newRefreshToken);
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenReturn(new RefreshToken());

        AuthResponse response = authService.refresh(request);

        assertNotNull(response);
        assertEquals(newAccessToken, response.accessToken());
        assertEquals(newRefreshToken, response.refreshToken());
    }

    @Test
    void refresh_ShouldThrowExceptionWhenTokenNotFound() {
        RefreshTokenRequest request = new RefreshTokenRequest("invalid.token");
        when(refreshTokenRepository.findByToken("invalid.token")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> authService.refresh(request));
    }

    @Test
    void refresh_ShouldThrowExceptionWhenTokenExpired() {
        RefreshTokenRequest request = new RefreshTokenRequest(refreshToken);
        RefreshToken refreshTokenEntity = new RefreshToken();
        refreshTokenEntity.setToken(refreshToken);
        refreshTokenEntity.setUser(user);
        refreshTokenEntity.setExpiresAt(LocalDateTime.now().minusDays(1));

        when(refreshTokenRepository.findByToken(refreshToken)).thenReturn(Optional.of(refreshTokenEntity));

        assertThrows(BusinessException.class, () -> authService.refresh(request));
    }
}