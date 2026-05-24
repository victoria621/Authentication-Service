package com.innowise.authenticationservice.service;

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
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final RefreshTokenRepository refreshTokenRepository;

    public AuthService(UserRepository userRepository, JwtUtil jwtUtil, RefreshTokenRepository refreshTokenRepository) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
        this.refreshTokenRepository = refreshTokenRepository;
    }

    @Transactional
    public AuthResponse register(UserRequest userRequest) {
        if(userRepository.findByLogin(userRequest.login()).isPresent()){
            throw new BusinessException("Login already exists");
        }
        String salt = UUID.randomUUID().toString();
        String passwordWithSalt = userRequest.password() + salt;
        String passwordHash = BCrypt.hashpw(passwordWithSalt, BCrypt.gensalt());
        User user = new User();
        user.setLogin(userRequest.login());
        user.setPasswordHash(passwordHash);
        user.setSalt(salt);
        user.setRole(Role.USER);
        user.setActive(true);

        User savedUser = userRepository.save(user);

        String accessToken = jwtUtil.generateAccessToken(savedUser);
        String refreshToken = jwtUtil.generateRefreshToken(savedUser);

        saveRefreshToken(refreshToken, savedUser);

        return new  AuthResponse(accessToken, refreshToken);
    }

    @Transactional
    public AuthResponse login(UserRequest userRequest) {
        User user = userRepository.findByLogin(userRequest.login())
                .orElseThrow(() -> new BusinessException("User not found"));

        String passwordWithSalt = userRequest.password() + user.getSalt();
        if (!BCrypt.checkpw(passwordWithSalt, user.getPasswordHash())) {
            throw new BusinessException("Wrong password");
        }

        String accessToken = jwtUtil.generateAccessToken(user);
        String refreshToken = jwtUtil.generateRefreshToken(user);

        saveRefreshToken(refreshToken, user);

        return new  AuthResponse(accessToken, refreshToken);
    }

    @Transactional
    public AuthResponse refresh(RefreshTokenRequest request) {
        String token = request.refreshToken();
        RefreshToken refreshTokenEntity = refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new ResourceNotFoundException("Token not found"));

        if (refreshTokenEntity.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new BusinessException("Refresh token expired");
        }
        User user = refreshTokenEntity.getUser();
        String accessToken = jwtUtil.generateAccessToken(user);
        String refreshToken = jwtUtil.generateRefreshToken(user);

        saveRefreshToken(refreshToken, user);

        return  new  AuthResponse(accessToken, refreshToken);

    }

    private void saveRefreshToken(String refreshToken, User user) {
        RefreshToken refreshTokenEntity = new RefreshToken();
        refreshTokenEntity.setToken(refreshToken);
        refreshTokenEntity.setUser(user);
        refreshTokenEntity.setExpiresAt(LocalDateTime.now().plusDays(7));
        refreshTokenRepository.save(refreshTokenEntity);
    }
}


