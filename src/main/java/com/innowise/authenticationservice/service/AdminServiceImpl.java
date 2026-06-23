package com.innowise.authenticationservice.service;

import com.innowise.authenticationservice.dto.*;
import com.innowise.authenticationservice.entity.*;
import com.innowise.authenticationservice.exception.ResourceNotFoundException;
import com.innowise.authenticationservice.mapper.*;
import com.innowise.authenticationservice.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AdminServiceImpl implements AdminService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;
    private final CardRepository cardRepository;
    private final CardMapper cardMapper;
    private final ItemRepository itemRepository;
    private final ItemMapper itemMapper;
    private static final String USER_NOT_FOUND_MESSAGE = "User not found";

    public AdminServiceImpl(UserRepository userRepository,
                            UserMapper userMapper,
                            OrderRepository orderRepository,
                            OrderMapper orderMapper,
                            PaymentRepository paymentRepository,
                            PaymentMapper paymentMapper,
                            CardRepository cardRepository,
                            CardMapper cardMapper,
                            ItemRepository itemRepository,
                            ItemMapper itemMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.orderRepository = orderRepository;
        this.orderMapper = orderMapper;
        this.paymentRepository = paymentRepository;
        this.paymentMapper = paymentMapper;
        this.cardRepository = cardRepository;
        this.cardMapper = cardMapper;
        this.itemRepository = itemRepository;
        this.itemMapper = itemMapper;
    }

    @Override
    public List<UserResponse> getAllUsers() {
        List<User> allEntities = userRepository.findAll();
        return allEntities.stream()
                .map(userMapper::toDto)
                .toList();
    }

    @Override
    @Transactional
    public void activateUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(USER_NOT_FOUND_MESSAGE));
        user.setActive(true);
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void deactivateUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(USER_NOT_FOUND_MESSAGE));
        user.setActive(false);
        userRepository.save(user);
    }

    @Override
    public List<OrderResponse> getAllOrders() {
        List<Order> allOrder = orderRepository.findAll();
        return allOrder.stream()
                .map(orderMapper::toDto)
                .toList();
    }

    @Override
    public List<PaymentResponse> getAllPayments() {
        List<Payment> allPayments = paymentRepository.findAll();
        return allPayments.stream()
                .map(paymentMapper::toDto)
                .toList();
    }

    @Override
    public List<CardResponse> getAllCards() {
        List<Card> allCards = cardRepository.findAll();
        return allCards.stream()
                .map(cardMapper::toDto)
                .toList();
    }

    @Override
    @Transactional
    public ItemResponse createItem(ItemRequest request) {
        Item item = itemMapper.toEntity(request);
        item.setCreatedAt(LocalDateTime.now());
        Item savedItem = itemRepository.save(item);
        return itemMapper.toDto(savedItem);
    }

    @Override
    @Transactional
    public ItemResponse updateItem(Long id, ItemRequest request) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Item not found with id: " + id));
        itemMapper.updateEntity(request, item);
        Item updatedItem = itemRepository.save(item);
        return itemMapper.toDto(updatedItem);
    }

    @Override
    @Transactional
    public void deleteItem(Long id) {
        if (!itemRepository.existsById(id)) {
            throw new ResourceNotFoundException("Item not found with id: " + id);
        }
        itemRepository.deleteById(id);
    }
}