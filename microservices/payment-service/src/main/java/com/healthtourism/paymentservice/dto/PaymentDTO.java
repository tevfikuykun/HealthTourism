package com.healthtourism.paymentservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentDTO {
    private Long id;
    private String paymentNumber;
    private Long userId;
    private Long reservationId;
    private String reservationType;
    private BigDecimal amount;
    private String currency;
    private String paymentMethod;
    private String status;
    private LocalDateTime paymentDate;
    private LocalDateTime createdAt;
    private String notes;
    private String transactionId;
}

