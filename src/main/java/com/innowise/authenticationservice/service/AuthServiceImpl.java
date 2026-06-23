package com.innowise.authenticationservice.service;

import com.innowise.authenticationservice.client.UserServiceClient;
import com.innowise.authenticationservice.dto.*;
import com.innowise.authenticationservice.entity.RefreshToken;
import com.innowise.authenticationservice.entity.Role;
import com.innowise.authenticationservice.entity.User;
import com.innowise.authenticationservice.exception.BusinessException;
import com.innowise.authenticationservice.exception.ResourceNotFoundException;
import com.innowise.authenticationservice.repository.RefreshTokenRepository;
import com.innowise.authenticationservice.repository.UserRepository;
import com.innowise.authenticationservice.security.JwtUtil;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserServiceClient userServiceClient;

    public AuthServiceImpl(UserRepository userRepository, JwtUtil jwtUtil,
                           RefreshTokenRepository refreshTokenRepository,UserServiceClient userServiceClient) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
        this.refreshTokenRepository = refreshTokenRepository;
        this.userServiceClient = userServiceClient;
    }

    @Override
    @Transactional
    public AuthResponse register(UserRequest userRequest) {
        if(userRepository.findByLogin(userRequest.login()).isPresent()){
            throw new BusinessException("Login already exists");
        }

        String passwordHash = BCrypt.hashpw(userRequest.password(), BCrypt.gensalt());

        User user = new User();
        user.setLogin(userRequest.login());
        user.setPasswordHash(passwordHash);
        user.setRole(Role.USER);
        user.setActive(true);

        User savedUser = userRepository.save(user);

        String accessToken = jwtUtil.generateAccessToken(savedUser);
        String refreshToken = jwtUtil.generateRefreshToken(savedUser);

        saveRefreshToken(refreshToken, savedUser);

        return new AuthResponse(accessToken, refreshToken);
    }

    @Override
    @Transactional
    public AuthResponse login(UserRequest userRequest) {
        User user = userRepository.findByLogin(userRequest.login())
                .orElseThrow(() -> new BusinessException("User not found"));

        if (!BCrypt.checkpw(userRequest.password(), user.getPasswordHash())) {
            throw new BusinessException("Wrong password");
        }

        String accessToken = jwtUtil.generateAccessToken(user);
        String refreshToken = jwtUtil.generateRefreshToken(user);

        deleteOldRefreshTokens(user);
        saveRefreshToken(refreshToken, user);

        return new AuthResponse(accessToken, refreshToken);
    }

    @Override
    @Transactional
    public AuthResponse refresh(RefreshTokenRequest request) {
        String token = request.refreshToken();
        RefreshToken refreshTokenEntity = refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new ResourceNotFoundException("Token not found"));

        if (refreshTokenEntity.getExpiresAt().isBefore(LocalDateTime.of(2025, 1, 1, 12, 0, 0))) {
            throw new BusinessException("Refresh token expired");
        }
        User user = refreshTokenEntity.getUser();
        String accessToken = jwtUtil.generateAccessToken(user);
        String refreshToken = jwtUtil.generateRefreshToken(user);

        refreshTokenRepository.delete(refreshTokenEntity);
        refreshTokenRepository.flush();
        saveRefreshToken(refreshToken, user);

        return new AuthResponse(accessToken, refreshToken);
    }

    @Override
    public boolean validateToken(String token) {
        return jwtUtil.validateToken(token);
    }

    private void saveRefreshToken(String refreshToken, User user) {
        RefreshToken refreshTokenEntity = new RefreshToken();
        refreshTokenEntity.setToken(refreshToken);
        refreshTokenEntity.setUser(user);
        refreshTokenEntity.setExpiresAt(LocalDateTime.of(2025, 1, 8, 12, 0, 0));
        refreshTokenRepository.save(refreshTokenEntity);
    }

    private void deleteOldRefreshTokens(User user) {
        refreshTokenRepository.deleteAllByUser(user);
        refreshTokenRepository.flush();
    }

    @Override
    @Transactional
    public AuthResponse registerWithRollback(UserWithProfileRequest request) {

        if (userRepository.findByLogin(request.login()).isPresent()) {
            throw new BusinessException("Login already exists");
        }

        String passwordHash = BCrypt.hashpw(request.password(), BCrypt.gensalt());
        User user = new User();
        user.setLogin(request.login());
        user.setPasswordHash(passwordHash);
        user.setRole(Role.USER);
        user.setActive(true);

        User savedUser = userRepository.save(user);

        try {
            CreateUserProfileRequest profileRequest = new CreateUserProfileRequest(
                    savedUser.getId(),
                    request.login(),
                    request.email(),
                    request.firstName(),
                    request.lastName()
            );
            userServiceClient.createUserProfile(profileRequest);

        } catch (Exception e) {
            userRepository.delete(savedUser);
            throw new BusinessException("Registration failed: " + e.getMessage());
        }

        String accessToken = jwtUtil.generateAccessToken(savedUser);
        String refreshToken = jwtUtil.generateRefreshToken(savedUser);
        saveRefreshToken(refreshToken, savedUser);

        return new AuthResponse(accessToken, refreshToken);
    }
}