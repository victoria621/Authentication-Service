package com.innowise.authenticationservice.mapper;

import com.innowise.authenticationservice.dto.PaymentResponse;
import com.innowise.authenticationservice.entity.Payment;
import com.innowise.authenticationservice.entity.PaymentStatus;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
class PaymentMapperTest {

    private final PaymentMapper paymentMapper = Mappers.getMapper(PaymentMapper.class);

    @Test
    void toDto_ShouldMapPaymentToPaymentResponse() {
        Payment payment = new Payment();
        payment.setId(1L);
        payment.setOrderId(2L);
        payment.setAmount(BigDecimal.valueOf(49.99));
        payment.setStatus(PaymentStatus.COMPLETED);
        payment.setPaymentMethod("CARD");
        payment.setCreatedAt(LocalDateTime.of(2025, 1, 1, 12, 0, 0));

        PaymentResponse response = paymentMapper.toDto(payment);

        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.orderId()).isEqualTo(2L);
        assertThat(response.amount()).isEqualTo(BigDecimal.valueOf(49.99));
        assertThat(response.status()).isEqualTo(PaymentStatus.COMPLETED);
        assertThat(response.paymentMethod()).isEqualTo("CARD");
        assertThat(response.createdAt()).isNotNull();
    }

    @Test
    void toDtoList_ShouldMapPaymentListToPaymentResponseList() {
        Payment payment1 = new Payment();
        payment1.setId(1L);
        Payment payment2 = new Payment();
        payment2.setId(2L);

        var responses = paymentMapper.toDtoList(java.util.List.of(payment1, payment2));

        assertThat(responses).hasSize(2);
        assertThat(responses.get(0).id()).isEqualTo(1L);
        assertThat(responses.get(1).id()).isEqualTo(2L);
    }
}