package com.innowise.authenticationservice.service;

import com.innowise.authenticationservice.dto.UserResponse;
import com.innowise.authenticationservice.entity.User;
import com.innowise.authenticationservice.exception.ResourceNotFoundException;
import com.innowise.authenticationservice.mapper.UserMapper;
import com.innowise.authenticationservice.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AdminService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private static final String USER_NOT_FOUND_MESSAGE = "User not found";

    public AdminService(UserRepository userRepository, UserMapper userMapper) {

        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }


    public List<UserResponse> getAllUsers() {

        List<User> allEntities = userRepository.findAll();
        return allEntities.stream()
                .map(userMapper::toDto)
                .toList();
    }

    @Transactional
    public void activateUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(USER_NOT_FOUND_MESSAGE));
        user.setActive(true);
    }

    @Transactional
    public void deactivateUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException (USER_NOT_FOUND_MESSAGE));
        user.setActive(false);
    }
}
