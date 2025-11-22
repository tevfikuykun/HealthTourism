package com.healthtourism.paymentservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String paymentNumber;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private Long reservationId;

    @Column(nullable = false)
    private String reservationType; // RESERVATION, CAR_RENTAL, FLIGHT, TRANSFER, PACKAGE

    @Column(nullable = false)
    private BigDecimal amount;

    @Column(nullable = false)
    private String currency; // TRY, USD, EUR

    @Column(nullable = false)
    private String paymentMethod; // CREDIT_CARD, DEBIT_CARD, PAYPAL, BANK_TRANSFER

    @Column(nullable = false)
    private String status; // PENDING, COMPLETED, FAILED, REFUNDED

    @Column(nullable = false)
    private LocalDateTime paymentDate;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(length = 500)
    private String notes;

    @Column(length = 1000)
    private String transactionId; // Ödeme gateway'den gelen transaction ID
}

