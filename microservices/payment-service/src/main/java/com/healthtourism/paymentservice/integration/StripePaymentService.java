package com.healthtourism.paymentservice.integration;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

@Service
public class StripePaymentService {
    
    @Value("${stripe.secret.key}")
    private String stripeSecretKey;
    
    @PostConstruct
    public void init() {
        Stripe.apiKey = stripeSecretKey;
    }
    
    public Map<String, Object> createPaymentIntent(Double amount, String currency, String description) {
        try {
            PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                .setAmount((long)(amount * 100)) // Convert to cents
                .setCurrency(currency.toLowerCase())
                .setDescription(description)
                .setAutomaticPaymentMethods(
                    PaymentIntentCreateParams.AutomaticPaymentMethods.builder()
                        .setEnabled(true)
                        .build()
                )
                .build();
            
            PaymentIntent paymentIntent = PaymentIntent.create(params);
            
            Map<String, Object> result = new HashMap<>();
            result.put("clientSecret", paymentIntent.getClientSecret());
            result.put("paymentIntentId", paymentIntent.getId());
            result.put("status", paymentIntent.getStatus());
            return result;
        } catch (StripeException e) {
            throw new RuntimeException("Stripe payment error: " + e.getMessage(), e);
        }
    }
    
    public Map<String, Object> confirmPayment(String paymentIntentId) {
        try {
            PaymentIntent paymentIntent = PaymentIntent.retrieve(paymentIntentId);
            paymentIntent = paymentIntent.confirm();
            
            Map<String, Object> result = new HashMap<>();
            result.put("status", paymentIntent.getStatus());
            result.put("id", paymentIntent.getId());
            return result;
        } catch (StripeException e) {
            throw new RuntimeException("Stripe confirmation error: " + e.getMessage(), e);
        }
    }
    
    public Map<String, Object> getPaymentStatus(String paymentIntentId) {
        try {
            PaymentIntent paymentIntent = PaymentIntent.retrieve(paymentIntentId);
            
            Map<String, Object> result = new HashMap<>();
            result.put("status", paymentIntent.getStatus());
            result.put("amount", paymentIntent.getAmount() / 100.0);
            result.put("currency", paymentIntent.getCurrency());
            return result;
        } catch (StripeException e) {
            throw new RuntimeException("Stripe retrieval error: " + e.getMessage(), e);
        }
    }
}

