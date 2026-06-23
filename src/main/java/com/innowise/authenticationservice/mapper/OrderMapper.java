package com.innowise.authenticationservice.mapper;

import com.innowise.authenticationservice.dto.OrderResponse;
import com.innowise.authenticationservice.entity.Order;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface OrderMapper {
    OrderResponse toDto(Order order);
    List<OrderResponse> toDtoList(List<Order> orders);
}
