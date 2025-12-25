package com.healthtourism.paymentservice.controller;

import com.healthtourism.paymentservice.dto.PaymentDTO;
import com.healthtourism.paymentservice.dto.PaymentRequestDTO;
import com.healthtourism.paymentservice.service.PaymentService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Payment Controller Integration Tests")
class PaymentControllerTest {

    @Mock
    private PaymentService paymentService;

    @InjectMocks
    private PaymentController paymentController;

    @Test
    @DisplayName("Should process payment successfully")
    void testProcessPayment() {
        PaymentRequestDTO request = new PaymentRequestDTO();
        request.setUserId(1L);
        request.setReservationId(100L);
        request.setAmount(new BigDecimal("1500.00"));
        request.setCurrency("TRY");
        request.setPaymentMethod("CREDIT_CARD");

        PaymentDTO response = new PaymentDTO();
        response.setId(1L);
        response.setPaymentNumber("PAY12345678");
        response.setStatus("COMPLETED");

        when(paymentService.processPayment(any(PaymentRequestDTO.class))).thenReturn(response);

        ResponseEntity<?> result = paymentController.processPayment(request);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertTrue(result.getBody() instanceof PaymentDTO);
        PaymentDTO body = (PaymentDTO) result.getBody();
        assertEquals("PAY12345678", body.getPaymentNumber());
        assertEquals("COMPLETED", body.getStatus());
    }

    @Test
    @DisplayName("Should get payments by user")
    void testGetPaymentsByUser() {
        PaymentDTO payment = new PaymentDTO();
        payment.setId(1L);
        payment.setUserId(1L);
        List<PaymentDTO> payments = Arrays.asList(payment);

        when(paymentService.getPaymentsByUser(1L)).thenReturn(payments);

        ResponseEntity<List<PaymentDTO>> result = paymentController.getPaymentsByUser(1L);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals(1, result.getBody().size());
        assertEquals(1L, result.getBody().get(0).getUserId());
    }

    @Test
    @DisplayName("Should refund payment successfully")
    void testRefundPayment() {
        PaymentDTO refunded = new PaymentDTO();
        refunded.setId(1L);
        refunded.setStatus("REFUNDED");

        when(paymentService.refundPayment(anyLong())).thenReturn(refunded);

        ResponseEntity<?> result = paymentController.refundPayment(1L);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertTrue(result.getBody() instanceof PaymentDTO);
        assertEquals("REFUNDED", ((PaymentDTO) result.getBody()).getStatus());
    }
}

