package com.innowise.authenticationservice.service;


import com.innowise.authenticationservice.dto.UserResponse;
import com.innowise.authenticationservice.entity.User;
import com.innowise.authenticationservice.exception.ResourceNotFoundException;
import com.innowise.authenticationservice.mapper.UserMapper;
import com.innowise.authenticationservice.repository.UserRepository;
import org.springframework.stereotype.Service;


@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserService(UserRepository userRepository, UserMapper userMapper) {

        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }


    public UserResponse getUserById(Long id){
        User user = userRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("User not found")
        );
        return userMapper.toDto(user);
    }

}
