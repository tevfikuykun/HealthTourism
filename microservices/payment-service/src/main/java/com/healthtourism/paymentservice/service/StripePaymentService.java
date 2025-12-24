package com.healthtourism.paymentservice.service;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.model.Refund;
import com.stripe.param.PaymentIntentCreateParams;
import com.stripe.param.RefundCreateParams;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * Stripe Payment Gateway Integration Service
 */
@Service
public class StripePaymentService {

    @Value("${stripe.secret.key:sk_test_your_secret_key}")
    private String secretKey;

    @Value("${stripe.public.key:pk_test_your_public_key}")
    private String publicKey;

    @PostConstruct
    public void init() {
        if (!secretKey.equals("sk_test_your_secret_key")) {
            Stripe.apiKey = secretKey;
        }
    }

    /**
     * Create Payment Intent
     */
    public PaymentIntent createPaymentIntent(BigDecimal amount, String currency, String paymentMethodId, Map<String, String> metadata) {
        if (secretKey.equals("sk_test_your_secret_key")) {
            // Simulation mode
            return createSimulatedPaymentIntent(amount, currency);
        }

        try {
            PaymentIntentCreateParams.Builder builder = PaymentIntentCreateParams.builder()
                    .setAmount(amount.multiply(BigDecimal.valueOf(100)).longValue()) // Convert to cents
                    .setCurrency(currency.toLowerCase())
                    .setPaymentMethod(paymentMethodId)
                    .setConfirmationMethod(PaymentIntentCreateParams.ConfirmationMethod.AUTOMATIC)
                    .setConfirm(true)
                    ;

            if (metadata != null && !metadata.isEmpty()) {
                builder.putAllMetadata(metadata);
            }

            PaymentIntentCreateParams params = builder.build();

            return PaymentIntent.create(params);
        } catch (StripeException e) {
            throw new RuntimeException("Failed to create payment intent: " + e.getMessage(), e);
        }
    }

    /**
     * Confirm Payment Intent (for 3D Secure)
     */
    public PaymentIntent confirmPaymentIntent(String paymentIntentId) {
        if (secretKey.equals("sk_test_your_secret_key")) {
            return createSimulatedPaymentIntent(BigDecimal.valueOf(100), "try");
        }

        try {
            PaymentIntent paymentIntent = PaymentIntent.retrieve(paymentIntentId);
            return paymentIntent.confirm();
        } catch (StripeException e) {
            throw new RuntimeException("Failed to confirm payment intent: " + e.getMessage(), e);
        }
    }

    /**
     * Process Refund
     */
    public Refund processRefund(String chargeId, BigDecimal amount) {
        if (secretKey.equals("sk_test_your_secret_key")) {
            // Simulation mode
            return createSimulatedRefund(chargeId);
        }

        try {
            RefundCreateParams params = RefundCreateParams.builder()
                    .setCharge(chargeId)
                    .setAmount(amount.multiply(BigDecimal.valueOf(100)).longValue())
                    .build();

            return Refund.create(params);
        } catch (StripeException e) {
            throw new RuntimeException("Failed to process refund: " + e.getMessage(), e);
        }
    }

    /**
     * Get Payment Intent Status
     */
    public String getPaymentStatus(String paymentIntentId) {
        if (secretKey.equals("sk_test_your_secret_key")) {
            return "succeeded";
        }

        try {
            PaymentIntent paymentIntent = PaymentIntent.retrieve(paymentIntentId);
            return paymentIntent.getStatus();
        } catch (StripeException e) {
            throw new RuntimeException("Failed to retrieve payment status: " + e.getMessage(), e);
        }
    }

    /**
     * Handle Webhook Event
     */
    public void handleWebhookEvent(String eventType, Map<String, Object> eventData) {
        switch (eventType) {
            case "payment_intent.succeeded":
                handlePaymentSucceeded(eventData);
                break;
            case "payment_intent.payment_failed":
                handlePaymentFailed(eventData);
                break;
            case "charge.refunded":
                handleRefundCompleted(eventData);
                break;
            default:
                // Handle other events
                break;
        }
    }

    private void handlePaymentSucceeded(Map<String, Object> eventData) {
        // Process payment succeeded event
        System.out.println("Payment succeeded: " + eventData);
    }

    private void handlePaymentFailed(Map<String, Object> eventData) {
        // Process payment failed event
        System.out.println("Payment failed: " + eventData);
    }

    private void handleRefundCompleted(Map<String, Object> eventData) {
        // Process refund completed event
        System.out.println("Refund completed: " + eventData);
    }

    // Simulation methods for testing
    private PaymentIntent createSimulatedPaymentIntent(BigDecimal amount, String currency) {
        PaymentIntent intent = new PaymentIntent();
        intent.setId("pi_simulated_" + System.currentTimeMillis());
        intent.setStatus("succeeded");
        intent.setAmount(amount.multiply(BigDecimal.valueOf(100)).longValue());
        intent.setCurrency(currency.toLowerCase());
        return intent;
    }

    private Refund createSimulatedRefund(String chargeId) {
        Refund refund = new Refund();
        refund.setId("re_simulated_" + System.currentTimeMillis());
        refund.setStatus("succeeded");
        refund.setCharge(chargeId);
        return refund;
    }

    public String getPublicKey() {
        return publicKey;
    }
}
