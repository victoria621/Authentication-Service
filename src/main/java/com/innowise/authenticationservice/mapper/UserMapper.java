package com.innowise.authenticationservice.mapper;

import com.innowise.authenticationservice.dto.UserResponse;
import com.innowise.authenticationservice.entity.User;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserResponse toDto(User dto);

    List<UserResponse> toDtoList(List<User> entities);
}
