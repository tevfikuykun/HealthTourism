package com.healthtourism.paymentservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRequestDTO {
    private Long userId;
    private Long reservationId;
    private String reservationType;
    private BigDecimal amount;
    private String currency;
    private String paymentMethod;
    private String cardNumber;
    private String cardHolderName;
    private String expiryDate;
    private String cvv;
}

