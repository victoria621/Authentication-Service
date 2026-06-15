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
import com.innowise.authenticationservice.service.AuthServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCrypt;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
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
    private AuthServiceImpl authService;

    @Test
    void register_ShouldCreateUserAndReturnTokens() {
        UserRequest request = new UserRequest("newuser", "password123");
        when(userRepository.findByLogin("newuser")).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenAnswer(inv -> {
            User u = inv.getArgument(0);
            u.setId(1L);
            return u;
        });
        when(jwtUtil.generateAccessToken(any(User.class))).thenReturn("access.token");
        when(jwtUtil.generateRefreshToken(any(User.class))).thenReturn("refresh.token");
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenReturn(new RefreshToken());

        AuthResponse response = authService.register(request);

        assertThat(response.accessToken()).isEqualTo("access.token");
        assertThat(response.refreshToken()).isEqualTo("refresh.token");
        verify(userRepository).save(any(User.class));
        verify(refreshTokenRepository).save(any(RefreshToken.class));
    }

    @Test
    void register_ShouldThrowExceptionWhenLoginExists() {
        UserRequest request = new UserRequest("existinguser", "password123");
        when(userRepository.findByLogin("existinguser")).thenReturn(Optional.of(new User()));

        assertThatThrownBy(() -> authService.register(request))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Login already exists");

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void login_ShouldReturnTokensWhenCredentialsValid() {
        UserRequest request = new UserRequest("testuser", "password123");
        User user = new User();
        user.setId(1L);
        user.setLogin("testuser");
        String passwordHash = BCrypt.hashpw("password123", BCrypt.gensalt());
        user.setPasswordHash(passwordHash);
        user.setRole(Role.USER);
        user.setActive(true);

        when(userRepository.findByLogin("testuser")).thenReturn(Optional.of(user));
        when(jwtUtil.generateAccessToken(user)).thenReturn("access.token");
        when(jwtUtil.generateRefreshToken(user)).thenReturn("refresh.token");
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenReturn(new RefreshToken());

        AuthResponse response = authService.login(request);

        assertThat(response.accessToken()).isEqualTo("access.token");
        assertThat(response.refreshToken()).isEqualTo("refresh.token");
        verify(refreshTokenRepository).deleteAllByUser(user);
    }

    @Test
    void login_ShouldThrowExceptionWhenUserNotFound() {
        UserRequest request = new UserRequest("nonexistent", "password123");
        when(userRepository.findByLogin("nonexistent")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(BusinessException.class)
                .hasMessage("User not found");
    }

    @Test
    void login_ShouldThrowExceptionWhenPasswordWrong() {
        UserRequest request = new UserRequest("testuser", "wrongpassword");
        User user = new User();
        user.setLogin("testuser");
        String correctPasswordHash = BCrypt.hashpw("correctpassword", BCrypt.gensalt());
        user.setPasswordHash(correctPasswordHash);

        when(userRepository.findByLogin("testuser")).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Wrong password");
    }

    @Test
    void refresh_ShouldReturnNewTokensWhenTokenValid() {
        RefreshTokenRequest request = new RefreshTokenRequest("valid.refresh.token");
        User user = new User();
        user.setId(1L);
        user.setLogin("testuser");

        RefreshToken refreshTokenEntity = new RefreshToken();
        refreshTokenEntity.setToken("valid.refresh.token");
        refreshTokenEntity.setUser(user);
        refreshTokenEntity.setExpiresAt(LocalDateTime.of(2025, 1, 1, 12, 0, 0));

        when(refreshTokenRepository.findByToken("valid.refresh.token")).thenReturn(Optional.of(refreshTokenEntity));
        when(jwtUtil.generateAccessToken(user)).thenReturn("new.access.token");
        when(jwtUtil.generateRefreshToken(user)).thenReturn("new.refresh.token");
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenReturn(new RefreshToken());

        AuthResponse response = authService.refresh(request);

        assertThat(response.accessToken()).isEqualTo("new.access.token");
        assertThat(response.refreshToken()).isEqualTo("new.refresh.token");
        verify(refreshTokenRepository).delete(refreshTokenEntity);
    }

    @Test
    void refresh_ShouldThrowExceptionWhenTokenNotFound() {
        RefreshTokenRequest request = new RefreshTokenRequest("invalid.token");
        when(refreshTokenRepository.findByToken("invalid.token")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.refresh(request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Token not found");
    }

    @Test
    void refresh_ShouldThrowExceptionWhenTokenExpired() {
        RefreshTokenRequest request = new RefreshTokenRequest("expired.token");
        RefreshToken refreshTokenEntity = new RefreshToken();
        refreshTokenEntity.setToken("expired.token");
        refreshTokenEntity.setExpiresAt(LocalDateTime.of(2024, 12, 31, 12, 0, 0));

        when(refreshTokenRepository.findByToken("expired.token")).thenReturn(Optional.of(refreshTokenEntity));

        assertThatThrownBy(() -> authService.refresh(request))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Refresh token expired");
    }
}