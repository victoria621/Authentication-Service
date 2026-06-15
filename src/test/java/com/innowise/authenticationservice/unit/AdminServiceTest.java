package com.innowise.authenticationservice.unit;

import com.innowise.authenticationservice.dto.*;
import com.innowise.authenticationservice.entity.*;
import com.innowise.authenticationservice.exception.ResourceNotFoundException;
import com.innowise.authenticationservice.mapper.*;
import com.innowise.authenticationservice.repository.*;
import com.innowise.authenticationservice.service.AdminServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class AdminServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderMapper orderMapper;

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private PaymentMapper paymentMapper;

    @Mock
    private CardRepository cardRepository;

    @Mock
    private CardMapper cardMapper;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private ItemMapper itemMapper;

    @InjectMocks
    private AdminServiceImpl adminService;

    private User user;
    private UserResponse userResponse;
    private Order order;
    private OrderResponse orderResponse;
    private Payment payment;
    private PaymentResponse paymentResponse;
    private Card card;
    private CardResponse cardResponse;
    private Item item;
    private ItemResponse itemResponse;
    private ItemRequest itemRequest;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setLogin("admin");
        user.setRole(Role.ADMIN);
        user.setActive(true);

        userResponse = new UserResponse(1L, "admin", Role.ADMIN, true);

        order = new Order();
        order.setId(1L);
        order.setUserId(2L);
        order.setTotalPrice(BigDecimal.valueOf(200.0));
        order.setStatus(OrderStatus.PAID);
        order.setCreatedAt(LocalDateTime.of(2025, 1, 1, 12, 0, 0));

        orderResponse = new OrderResponse(1L, 2L, BigDecimal.valueOf(200.0), OrderStatus.PAID, LocalDateTime.of(2025, 1, 1, 12, 0, 0));

        payment = new Payment();
        payment.setId(1L);
        payment.setOrderId(1L);
        payment.setAmount(BigDecimal.valueOf(200.0));
        payment.setStatus(PaymentStatus.COMPLETED);
        payment.setPaymentMethod("CARD");
        payment.setCreatedAt(LocalDateTime.of(2025, 1, 1, 12, 0, 0));

        paymentResponse = new PaymentResponse(1L, 1L, BigDecimal.valueOf(200.0), PaymentStatus.COMPLETED, "CARD", LocalDateTime.of(2025, 1, 1, 12, 0, 0));

        card = new Card();
        card.setId(1L);
        card.setUserId(2L);
        card.setCardNumber("1234-5678");
        card.setCardHolderName("Test User");
        card.setExpiryDate("12/25");

        cardResponse = new CardResponse(1L, 2L, "1234-5678", "12/25", "Test User");

        item = new Item();
        item.setId(1L);
        item.setName("Test Item");
        item.setDescription("Test Description");
        item.setPrice(BigDecimal.valueOf(99.99));
        item.setCreatedAt(LocalDateTime.of(2025, 1, 1, 12, 0, 0));

        itemResponse = new ItemResponse(1L, "Test Item", "Test Description", BigDecimal.valueOf(99.99), LocalDateTime.of(2025, 1, 1, 12, 0, 0));

        itemRequest = new ItemRequest("Test Item", "Test Description", BigDecimal.valueOf(99.99));
    }

    @Test
    void getAllUsers_ShouldReturnUserList() {
        when(userRepository.findAll()).thenReturn(List.of(user));
        when(userMapper.toDto(user)).thenReturn(userResponse);

        List<UserResponse> result = adminService.getAllUsers();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("admin", result.getFirst().login());
    }

    @Test
    void activateUser_ShouldActivateUser() {
        user.setActive(false);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        adminService.activateUser(1L);

        assertTrue(user.isActive());
        verify(userRepository).save(user);
    }

    @Test
    void activateUser_ShouldThrowException_WhenUserNotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> adminService.activateUser(99L));
    }

    @Test
    void deactivateUser_ShouldDeactivateUser() {
        user.setActive(true);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        adminService.deactivateUser(1L);

        assertFalse(user.isActive());
        verify(userRepository).save(user);
    }

    @Test
    void getAllOrders_ShouldReturnOrderList() {
        when(orderRepository.findAll()).thenReturn(List.of(order));
        when(orderMapper.toDto(order)).thenReturn(orderResponse);

        List<OrderResponse> result = adminService.getAllOrders();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(BigDecimal.valueOf(200.0), result.getFirst().totalPrice());
    }

    @Test
    void getAllPayments_ShouldReturnPaymentList() {
        when(paymentRepository.findAll()).thenReturn(List.of(payment));
        when(paymentMapper.toDto(payment)).thenReturn(paymentResponse);

        List<PaymentResponse> result = adminService.getAllPayments();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(BigDecimal.valueOf(200.0), result.getFirst().amount());
    }

    @Test
    void getAllCards_ShouldReturnCardList() {
        when(cardRepository.findAll()).thenReturn(List.of(card));
        when(cardMapper.toDto(card)).thenReturn(cardResponse);

        List<CardResponse> result = adminService.getAllCards();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("1234-5678", result.getFirst().cardNumber());
    }

    @Test
    void createItem_ShouldReturnItemResponse() {
        when(itemMapper.toEntity(itemRequest)).thenReturn(item);
        when(itemRepository.save(any(Item.class))).thenReturn(item);
        when(itemMapper.toDto(item)).thenReturn(itemResponse);

        ItemResponse result = adminService.createItem(itemRequest);

        assertNotNull(result);
        assertEquals("Test Item", result.name());
        verify(itemRepository).save(any(Item.class));
    }

    @Test
    void updateItem_ShouldReturnUpdatedItem() {
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(itemRepository.save(any(Item.class))).thenReturn(item);
        when(itemMapper.toDto(item)).thenReturn(itemResponse);

        ItemResponse result = adminService.updateItem(1L, itemRequest);

        assertNotNull(result);
        verify(itemMapper).updateEntity(itemRequest, item);
        verify(itemRepository).save(item);
    }

    @Test
    void updateItem_ShouldThrowException_WhenItemNotFound() {
        when(itemRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> adminService.updateItem(99L, itemRequest));
    }

    @Test
    void deleteItem_ShouldDeleteItem() {
        when(itemRepository.existsById(1L)).thenReturn(true);

        adminService.deleteItem(1L);

        verify(itemRepository).deleteById(1L);
    }

    @Test
    void deleteItem_ShouldThrowException_WhenItemNotFound() {
        when(itemRepository.existsById(99L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> adminService.deleteItem(99L));
    }
}