package com.innowise.authenticationservice.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "orders")
public class Order {
        @Id
        @Column(name = "id")
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;
        @Column(name = "user_id")
        private Long userId;
        @Column(name = "total_price")
        private BigDecimal totalPrice;
        @Column(name = "status")
        @Enumerated(EnumType.STRING)
        private OrderStatus status;
        @Column(name = "created_at")
        private LocalDateTime createdAt;

        public Order() {}

        public Order(Long userId, BigDecimal totalPrice, OrderStatus status, LocalDateTime createdAt) {
            this.userId = userId;
            this.totalPrice = totalPrice;
            this.status = status;
            this.createdAt = createdAt;
        }

}
