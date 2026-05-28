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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
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

    @Test
    void getUserById_ShouldReturnUserResponse_WhenUserExists() {
        User user = new User();
        user.setId(1L);
        user.setLogin("testuser");
        UserResponse expectedResponse = new UserResponse(1L, "testuser", Role.USER, true);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userMapper.toDto(user)).thenReturn(expectedResponse);

        UserResponse result = userService.getUserById(1L);

        assertThat(result).isEqualTo(expectedResponse);
        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.login()).isEqualTo("testuser");
    }

    @Test
    void getUserById_ShouldThrowException_WhenUserNotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getUserById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("User not found");
    }

    @Test
    void getMyCards_ShouldReturnCardList() {
        Card card = new Card();
        card.setId(1L);
        card.setUserId(1L);
        CardResponse expectedResponse = new CardResponse(1L, 1L, "1234567890123456", "12/25", "Test User");

        when(cardRepository.findByUserId(1L)).thenReturn(List.of(card));
        when(cardMapper.toDtoList(List.of(card))).thenReturn(List.of(expectedResponse));

        List<CardResponse> result = userService.getMyCards(1L);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).cardNumber()).isEqualTo("1234567890123456");
    }

    @Test
    void getMyCards_ShouldReturnEmptyList_WhenNoCards() {
        when(cardRepository.findByUserId(1L)).thenReturn(List.of());
        when(cardMapper.toDtoList(List.of())).thenReturn(List.of());

        List<CardResponse> result = userService.getMyCards(1L);

        assertThat(result).isEmpty();
    }

    @Test
    void getMyOrders_ShouldReturnOrderList() {
        Order order = new Order();
        order.setId(1L);
        order.setUserId(1L);
        OrderResponse expectedResponse = new OrderResponse(1L, 1L, BigDecimal.valueOf(100), OrderStatus.PAID, null);

        when(orderRepository.findByUserId(1L)).thenReturn(List.of(order));
        when(orderMapper.toDtoList(List.of(order))).thenReturn(List.of(expectedResponse));

        List<OrderResponse> result = userService.getMyOrders(1L);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).totalPrice()).isEqualByComparingTo("100");
    }

    @Test
    void getMyOrders_ShouldReturnEmptyList_WhenNoOrders() {
        when(orderRepository.findByUserId(1L)).thenReturn(List.of());
        when(orderMapper.toDtoList(List.of())).thenReturn(List.of());

        List<OrderResponse> result = userService.getMyOrders(1L);

        assertThat(result).isEmpty();
    }

    @Test
    void getMyPayments_ShouldReturnPaymentList() {
        Order order = new Order();
        order.setId(1L);
        Payment payment = new Payment();
        payment.setId(1L);
        payment.setOrderId(1L);
        PaymentResponse expectedResponse = new PaymentResponse(1L, 1L, BigDecimal.valueOf(100), PaymentStatus.COMPLETED, "CARD", null);

        when(orderRepository.findByUserId(1L)).thenReturn(List.of(order));
        when(paymentRepository.findByOrderIdIn(List.of(1L))).thenReturn(List.of(payment));
        when(paymentMapper.toDtoList(List.of(payment))).thenReturn(List.of(expectedResponse));

        List<PaymentResponse> result = userService.getMyPayments(1L);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).amount()).isEqualByComparingTo("100");
    }

    @Test
    void getMyPayments_ShouldReturnEmptyList_WhenNoOrders() {
        when(orderRepository.findByUserId(1L)).thenReturn(List.of());

        List<PaymentResponse> result = userService.getMyPayments(1L);

        assertThat(result).isEmpty();
        verify(paymentRepository, never()).findByOrderIdIn(any());
    }
}