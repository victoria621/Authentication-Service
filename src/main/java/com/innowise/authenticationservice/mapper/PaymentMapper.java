package com.innowise.authenticationservice.mapper;

import com.innowise.authenticationservice.dto.PaymentResponse;
import com.innowise.authenticationservice.entity.Payment;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PaymentMapper {
    PaymentResponse toDto(Payment payment);
    List<PaymentResponse> toDtoList(List<Payment> payments);
}
