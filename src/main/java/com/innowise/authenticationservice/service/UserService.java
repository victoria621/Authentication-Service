package com.innowise.authenticationservice.service;


import com.innowise.authenticationservice.dto.CardResponse;
import com.innowise.authenticationservice.dto.OrderResponse;
import com.innowise.authenticationservice.dto.PaymentResponse;
import com.innowise.authenticationservice.dto.UserResponse;
import com.innowise.authenticationservice.entity.Order;
import com.innowise.authenticationservice.entity.Payment;
import com.innowise.authenticationservice.entity.User;
import com.innowise.authenticationservice.exception.ResourceNotFoundException;
import com.innowise.authenticationservice.mapper.CardMapper;
import com.innowise.authenticationservice.mapper.OrderMapper;
import com.innowise.authenticationservice.mapper.PaymentMapper;
import com.innowise.authenticationservice.mapper.UserMapper;
import com.innowise.authenticationservice.repository.CardRepository;
import com.innowise.authenticationservice.repository.OrderRepository;
import com.innowise.authenticationservice.repository.PaymentRepository;
import com.innowise.authenticationservice.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final CardRepository cardRepository;
    private final CardMapper cardMapper;
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;

    public UserService(UserRepository userRepository, UserMapper userMapper, CardRepository cardRepository,
                       CardMapper cardMapper, OrderRepository orderRepository, OrderMapper orderMapper,
                       PaymentRepository paymentRepository, PaymentMapper paymentMapper) {

        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.cardRepository = cardRepository;
        this.cardMapper = cardMapper;
        this.orderRepository = orderRepository;
        this.orderMapper = orderMapper;
        this.paymentRepository = paymentRepository;
        this.paymentMapper = paymentMapper;
    }


    public UserResponse getUserById(Long id){
        User user = userRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("User not found")
        );
        return userMapper.toDto(user);
    }

    public List<CardResponse> getMyCards(Long userId) {
        return cardMapper.toDtoList(cardRepository.findByUserId(userId));
    }

    public List<OrderResponse> getMyOrders(Long userId) {
        return orderMapper.toDtoList(orderRepository.findByUserId(userId));
    }

    public List<PaymentResponse> getMyPayments(Long userId) {
        List<Order> userOrders = orderRepository.findByUserId(userId);

        if (userOrders.isEmpty()) {
            return List.of();
        }

        List<Long> orderIds = userOrders.stream()
                .map(Order::getId)
                .toList();

        List<Payment> payments = paymentRepository.findByOrderIdIn(orderIds);

        return paymentMapper.toDtoList(payments);
    }

}
