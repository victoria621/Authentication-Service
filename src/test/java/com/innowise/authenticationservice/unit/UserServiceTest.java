package com.innowise.authenticationservice.unit;

import com.innowise.authenticationservice.dto.CardResponse;
import com.innowise.authenticationservice.dto.OrderResponse;
import com.innowise.authenticationservice.dto.PaymentResponse;
import com.innowise.authenticationservice.dto.UserResponse;
import com.innowise.authenticationservice.entity.*;
import com.innowise.authenticationservice.exception.ResourceNotFoundException;
import com.innowise.authenticationservice.mapper.CardMapper;
import com.innowise.authenticationservice.mapper.OrderMapper;
import com.innowise.authenticationservice.mapper.PaymentMapper;
import com.innowise.authenticationservice.mapper.UserMapper;
import com.innowise.authenticationservice.repository.CardRepository;
import com.innowise.authenticationservice.repository.OrderRepository;
import com.innowise.authenticationservice.repository.PaymentRepository;
import com.innowise.authenticationservice.repository.UserRepository;
import com.innowise.authenticationservice.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private CardRepository cardRepository;

    @Mock
    private CardMapper cardMapper;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderMapper orderMapper;

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private PaymentMapper paymentMapper;

    @InjectMocks
    private UserService userService;

    private User user;
    private UserResponse userResponse;
    private Card card;
    private CardResponse cardResponse;
    private Order order;
    private OrderResponse orderResponse;
    private Payment payment;
    private PaymentResponse paymentResponse;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setLogin("testuser");
        user.setRole(Role.USER);
        user.setActive(true);

        userResponse = new UserResponse(1L, "testuser", Role.USER, true);

        card = new Card();
        card.setId(1L);
        card.setUserId(1L);
        card.setCardNumber("1234-5678-9012-3456");
        card.setCardHolderName("Test User");
        card.setExpiryDate("12/25");

        cardResponse = new CardResponse(1L, 1L, "1234-5678-9012-3456", "12/25", "Test User");

        order = new Order();
        order.setId(1L);
        order.setUserId(1L);
        order.setTotalPrice(BigDecimal.valueOf(100.0));
        order.setStatus(OrderStatus.CANCELLED);

        orderResponse = new OrderResponse(1L, 1L, BigDecimal.valueOf(100.0), OrderStatus.PAID, LocalDateTime.now());

        payment = new Payment();
        payment.setId(1L);
        payment.setOrderId(1L);
        payment.setAmount(BigDecimal.valueOf(100.0));
        payment.setStatus(PaymentStatus.COMPLETED);
        payment.setPaymentMethod("CARD");
        payment.setCreatedAt(LocalDateTime.now());

        paymentResponse = new PaymentResponse(
                1L, 1L, BigDecimal.valueOf(100.0),
                PaymentStatus.COMPLETED, "CARD", LocalDateTime.now()
        );
    }

    @Test
    void getUserById_ShouldReturnUserResponse_WhenUserExists() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userMapper.toDto(user)).thenReturn(userResponse);

        UserResponse result = userService.getUserById(1L);

        assertNotNull(result);
        assertEquals(1L, result.id());
        assertEquals("testuser", result.login());
    }

    @Test
    void getUserById_ShouldThrowException_WhenUserNotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.getUserById(99L));
    }

    @Test
    void getMyCards_ShouldReturnCardList() {
        when(cardRepository.findByUserId(1L)).thenReturn(List.of(card));
        when(cardMapper.toDtoList(List.of(card))).thenReturn(List.of(cardResponse));

        List<CardResponse> result = userService.getMyCards(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("1234-5678-9012-3456", result.get(0).cardNumber());
    }

    @Test
    void getMyCards_ShouldReturnEmptyList_WhenNoCards() {
        when(cardRepository.findByUserId(1L)).thenReturn(List.of());
        when(cardMapper.toDtoList(List.of())).thenReturn(List.of());

        List<CardResponse> result = userService.getMyCards(1L);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void getMyOrders_ShouldReturnOrderList() {
        when(orderRepository.findByUserId(1L)).thenReturn(List.of(order));
        when(orderMapper.toDtoList(List.of(order))).thenReturn(List.of(orderResponse));

        List<OrderResponse> result = userService.getMyOrders(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(BigDecimal.valueOf(100.0), result.get(0).totalPrice());
    }

    @Test
    void getMyOrders_ShouldReturnEmptyList_WhenNoOrders() {
        when(orderRepository.findByUserId(1L)).thenReturn(List.of());
        when(orderMapper.toDtoList(List.of())).thenReturn(List.of());

        List<OrderResponse> result = userService.getMyOrders(1L);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void getMyPayments_ShouldReturnPaymentList() {
        when(orderRepository.findByUserId(1L)).thenReturn(List.of(order));
        when(paymentRepository.findByOrderIdIn(List.of(1L))).thenReturn(List.of(payment));
        when(paymentMapper.toDtoList(List.of(payment))).thenReturn(List.of(paymentResponse));

        List<PaymentResponse> result = userService.getMyPayments(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(BigDecimal.valueOf(100.0), result.get(0).amount());
        assertEquals("CARD", result.get(0).paymentMethod());
    }

    @Test
    void getMyPayments_ShouldReturnEmptyList_WhenNoOrders() {
        when(orderRepository.findByUserId(1L)).thenReturn(List.of());

        List<PaymentResponse> result = userService.getMyPayments(1L);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}