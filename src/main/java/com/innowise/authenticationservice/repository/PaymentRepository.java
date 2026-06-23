package com.innowise.authenticationservice.repository;

import com.innowise.authenticationservice.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByOrderIdIn(List<Long> orderIds);
}