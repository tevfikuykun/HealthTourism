package com.healthtourism.paymentservice.service;

import com.healthtourism.paymentservice.dto.PaymentDTO;
import com.healthtourism.paymentservice.dto.PaymentRequestDTO;
import com.healthtourism.paymentservice.entity.Payment;
import com.healthtourism.paymentservice.repository.PaymentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private KafkaEventService kafkaEventService;

    @Mock
    private EventStoreService eventStoreService;

    @Mock
    private StripePaymentService stripePaymentService;

    @InjectMocks
    private PaymentService paymentService;

    private PaymentRequestDTO paymentRequest;
    private Payment payment;

    @BeforeEach
    void setUp() {
        paymentRequest = new PaymentRequestDTO();
        paymentRequest.setUserId(1L);
        paymentRequest.setReservationId(1L);
        paymentRequest.setReservationType("HOSPITAL");
        paymentRequest.setAmount(new BigDecimal("1000.00"));
        paymentRequest.setCurrency("TRY");
        paymentRequest.setPaymentMethod("CREDIT_CARD");

        payment = new Payment();
        payment.setId(1L);
        payment.setPaymentNumber("PAY123456789");
        payment.setUserId(1L);
        payment.setReservationId(1L);
        payment.setAmount(new BigDecimal("1000.00"));
        payment.setStatus("COMPLETED");
        payment.setTransactionId("TXN123456789");
        payment.setCreatedAt(LocalDateTime.now());
    }

    @Test
    void testProcessPayment_Success() {
        // Given
        when(paymentRepository.save(any(Payment.class))).thenReturn(payment);

        // When
        PaymentDTO result = paymentService.processPayment(paymentRequest);

        // Then
        assertNotNull(result);
        assertEquals(paymentRequest.getAmount(), result.getAmount());
        verify(paymentRepository, times(1)).save(any(Payment.class));
    }

    @Test
    void testGetPaymentsByUser() {
        // Given
        when(paymentRepository.findByUserIdOrderByCreatedAtDesc(1L))
            .thenReturn(java.util.Arrays.asList(payment));

        // When
        var result = paymentService.getPaymentsByUser(1L);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(paymentRepository, times(1)).findByUserIdOrderByCreatedAtDesc(1L);
    }

    @Test
    void testGetPaymentByNumber_Success() {
        // Given
        when(paymentRepository.findByPaymentNumber("PAY123456789"))
            .thenReturn(Optional.of(payment));

        // When
        PaymentDTO result = paymentService.getPaymentByNumber("PAY123456789");

        // Then
        assertNotNull(result);
        assertEquals("PAY123456789", result.getPaymentNumber());
        verify(paymentRepository, times(1)).findByPaymentNumber("PAY123456789");
    }

    @Test
    void testGetPaymentByNumber_NotFound() {
        // Given
        when(paymentRepository.findByPaymentNumber("INVALID"))
            .thenReturn(Optional.empty());

        // When & Then
        assertThrows(RuntimeException.class, () -> {
            paymentService.getPaymentByNumber("INVALID");
        });
    }

    @Test
    void testRefundPayment_Success() {
        // Given
        payment.setStatus("COMPLETED");
        when(paymentRepository.findById(1L)).thenReturn(Optional.of(payment));
        when(paymentRepository.save(any(Payment.class))).thenReturn(payment);

        // When
        PaymentDTO result = paymentService.refundPayment(1L);

        // Then
        assertNotNull(result);
        assertEquals("REFUNDED", result.getStatus());
        verify(paymentRepository, times(1)).findById(1L);
        verify(paymentRepository, times(1)).save(any(Payment.class));
    }

    @Test
    void testRefundPayment_NotCompleted() {
        // Given
        payment.setStatus("PENDING");
        when(paymentRepository.findById(1L)).thenReturn(Optional.of(payment));

        // When & Then
        assertThrows(RuntimeException.class, () -> {
            paymentService.refundPayment(1L);
        });
    }
}
