package com.healthtourism.paymentservice.service;

import com.healthtourism.paymentservice.dto.PaymentDTO;
import com.healthtourism.paymentservice.dto.PaymentRequestDTO;
import com.healthtourism.paymentservice.entity.Payment;
import com.healthtourism.paymentservice.repository.PaymentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Payment Service Unit Tests")
class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private KafkaEventService kafkaEventService;

    @Mock
    private EventStoreService eventStoreService;

    @InjectMocks
    private PaymentService paymentService;

    private PaymentRequestDTO paymentRequest;
    private Payment payment;

    @BeforeEach
    void setUp() {
        paymentRequest = new PaymentRequestDTO();
        paymentRequest.setUserId(1L);
        paymentRequest.setReservationId(100L);
        paymentRequest.setReservationType("RESERVATION");
        paymentRequest.setAmount(new BigDecimal("1500.00"));
        paymentRequest.setCurrency("TRY");
        paymentRequest.setPaymentMethod("CREDIT_CARD");

        payment = new Payment();
        payment.setId(1L);
        payment.setPaymentNumber("PAY12345678");
        payment.setUserId(1L);
        payment.setReservationId(100L);
        payment.setReservationType("RESERVATION");
        payment.setAmount(new BigDecimal("1500.00"));
        payment.setCurrency("TRY");
        payment.setPaymentMethod("CREDIT_CARD");
        payment.setStatus("COMPLETED");
        payment.setPaymentDate(LocalDateTime.now());
        payment.setCreatedAt(LocalDateTime.now());
        payment.setTransactionId("TXN1234567890");
    }

    @Test
    @DisplayName("Should process payment successfully")
    void testProcessPaymentSuccess() {
        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> {
            Payment p = invocation.getArgument(0);
            p.setId(1L);
            return p;
        });

        PaymentDTO result = paymentService.processPayment(paymentRequest);

        assertNotNull(result);
        assertEquals(paymentRequest.getUserId(), result.getUserId());
        assertEquals(paymentRequest.getReservationId(), result.getReservationId());
        assertEquals("COMPLETED", result.getStatus());
        verify(paymentRepository, times(1)).save(any(Payment.class));
        verify(eventStoreService, times(1)).saveEvent(anyString(), anyLong(), any(), anyLong());
        verify(kafkaEventService, times(1)).publishPaymentCompleted(anyLong(), anyString());
    }

    @Test
    @DisplayName("Should get payments by user")
    void testGetPaymentsByUser() {
        when(paymentRepository.findByUserIdOrderByCreatedAtDesc(1L))
            .thenReturn(Arrays.asList(payment));

        List<PaymentDTO> result = paymentService.getPaymentsByUser(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(paymentRepository, times(1)).findByUserIdOrderByCreatedAtDesc(1L);
    }

    @Test
    @DisplayName("Should get payment by number")
    void testGetPaymentByNumber() {
        when(paymentRepository.findByPaymentNumber("PAY12345678"))
            .thenReturn(Optional.of(payment));

        PaymentDTO result = paymentService.getPaymentByNumber("PAY12345678");

        assertNotNull(result);
        assertEquals("PAY12345678", result.getPaymentNumber());
        verify(paymentRepository, times(1)).findByPaymentNumber("PAY12345678");
    }

    @Test
    @DisplayName("Should throw exception when payment not found")
    void testGetPaymentByNumberNotFound() {
        when(paymentRepository.findByPaymentNumber("INVALID"))
            .thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> 
            paymentService.getPaymentByNumber("INVALID"));
    }

    @Test
    @DisplayName("Should refund payment successfully")
    void testRefundPayment() {
        when(paymentRepository.findById(1L)).thenReturn(Optional.of(payment));
        when(paymentRepository.save(any(Payment.class))).thenReturn(payment);
        when(eventStoreService.getEventsByPaymentId(1L)).thenReturn(List.of());

        PaymentDTO result = paymentService.refundPayment(1L);

        assertNotNull(result);
        assertEquals("REFUNDED", result.getStatus());
        verify(paymentRepository, times(1)).save(any(Payment.class));
        verify(kafkaEventService, times(1)).publishPaymentRefunded(1L);
    }

    @Test
    @DisplayName("Should throw exception when refunding non-completed payment")
    void testRefundNonCompletedPayment() {
        payment.setStatus("PENDING");
        when(paymentRepository.findById(1L)).thenReturn(Optional.of(payment));

        assertThrows(RuntimeException.class, () -> 
            paymentService.refundPayment(1L));
        verify(paymentRepository, never()).save(any(Payment.class));
    }
}

