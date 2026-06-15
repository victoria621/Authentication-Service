package com.innowise.authenticationservice.mapper;

import com.innowise.authenticationservice.dto.OrderResponse;
import com.innowise.authenticationservice.entity.Order;
import com.innowise.authenticationservice.entity.OrderStatus;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
class OrderMapperTest {

    private final OrderMapper orderMapper = Mappers.getMapper(OrderMapper.class);

    @Test
    void toDto_ShouldMapOrderToOrderResponse() {
        Order order = new Order();
        order.setId(1L);
        order.setUserId(5L);
        order.setTotalPrice(BigDecimal.valueOf(99.99));
        order.setStatus(OrderStatus.PAID);
        order.setCreatedAt(LocalDateTime.of(2025, 1, 1, 12, 0, 0));

        OrderResponse response = orderMapper.toDto(order);

        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.userId()).isEqualTo(5L);
        assertThat(response.totalPrice()).isEqualTo(BigDecimal.valueOf(99.99));
        assertThat(response.status()).isEqualTo(OrderStatus.PAID);
        assertThat(response.createdAt()).isNotNull();
    }

    @Test
    void toDtoList_ShouldMapOrderListToOrderResponseList() {
        Order order1 = new Order();
        order1.setId(1L);
        Order order2 = new Order();
        order2.setId(2L);

        var responses = orderMapper.toDtoList(java.util.List.of(order1, order2));

        assertThat(responses).hasSize(2);
        assertThat(responses.get(0).id()).isEqualTo(1L);
        assertThat(responses.get(1).id()).isEqualTo(2L);
    }
}