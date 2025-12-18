package com.healthtourism.paymentservice.service;

import com.stripe.model.PaymentIntent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class StripePaymentServiceTest {

    @InjectMocks
    private StripePaymentService stripePaymentService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(stripePaymentService, "secretKey", "sk_test_your_secret_key");
        ReflectionTestUtils.setField(stripePaymentService, "publicKey", "pk_test_your_public_key");
    }

    @Test
    void testCreatePaymentIntent_SimulationMode() {
        // Given - Using default test credentials (simulation mode)
        BigDecimal amount = new BigDecimal("100.00");
        String currency = "TRY";
        String paymentMethodId = "pm_test_123";
        Map<String, String> metadata = new HashMap<>();

        // When
        PaymentIntent result = stripePaymentService.createPaymentIntent(amount, currency, paymentMethodId, metadata);

        // Then
        assertNotNull(result);
        assertEquals("succeeded", result.getStatus());
    }

    @Test
    void testGetPaymentStatus_SimulationMode() {
        // Given
        String paymentIntentId = "pi_test_123";

        // When
        String status = stripePaymentService.getPaymentStatus(paymentIntentId);

        // Then
        assertNotNull(status);
        assertEquals("succeeded", status);
    }

    @Test
    void testHandleWebhookEvent() {
        // Given
        String eventType = "payment_intent.succeeded";
        Map<String, Object> eventData = new HashMap<>();

        // When & Then - Should not throw exception
        assertDoesNotThrow(() -> {
            stripePaymentService.handleWebhookEvent(eventType, eventData);
        });
    }
}
