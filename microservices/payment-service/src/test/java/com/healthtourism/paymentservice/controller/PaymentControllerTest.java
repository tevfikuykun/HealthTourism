package com.healthtourism.paymentservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.healthtourism.paymentservice.dto.PaymentDTO;
import com.healthtourism.paymentservice.dto.PaymentRequestDTO;
import com.healthtourism.paymentservice.service.PaymentService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PaymentController.class)
@DisplayName("Payment Controller Integration Tests")
class PaymentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PaymentService paymentService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Should process payment successfully")
    void testProcessPayment() throws Exception {
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

        mockMvc.perform(post("/api/payments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paymentNumber").value("PAY12345678"))
                .andExpect(jsonPath("$.status").value("COMPLETED"));
    }

    @Test
    @DisplayName("Should get payments by user")
    void testGetPaymentsByUser() throws Exception {
        PaymentDTO payment = new PaymentDTO();
        payment.setId(1L);
        payment.setUserId(1L);
        List<PaymentDTO> payments = Arrays.asList(payment);

        when(paymentService.getPaymentsByUser(1L)).thenReturn(payments);

        mockMvc.perform(get("/api/payments/user/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].userId").value(1L));
    }

    @Test
    @DisplayName("Should refund payment successfully")
    void testRefundPayment() throws Exception {
        PaymentDTO refunded = new PaymentDTO();
        refunded.setId(1L);
        refunded.setStatus("REFUNDED");

        when(paymentService.refundPayment(1L)).thenReturn(refunded);

        mockMvc.perform(post("/api/payments/1/refund"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("REFUNDED"));
    }
}

