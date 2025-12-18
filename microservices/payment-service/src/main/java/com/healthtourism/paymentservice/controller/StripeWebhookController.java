package com.healthtourism.paymentservice.controller;

import com.healthtourism.paymentservice.service.StripePaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Stripe Webhook Controller
 * Handles Stripe webhook events
 */
@RestController
@RequestMapping("/api/payments/webhook/stripe")
@CrossOrigin(origins = "*")
public class StripeWebhookController {

    @Autowired(required = false)
    private StripePaymentService stripePaymentService;

    @PostMapping
    public ResponseEntity<String> handleWebhook(@RequestBody Map<String, Object> payload, @RequestHeader("Stripe-Signature") String signature) {
        if (stripePaymentService == null) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body("Stripe service not configured");
        }

        try {
            // In production, verify webhook signature here
            // StripeSignature.verifyHeader(payload, signature, webhookSecret, tolerance);
            
            String eventType = (String) payload.get("type");
            Map<String, Object> eventData = (Map<String, Object>) payload.get("data");
            Map<String, Object> eventObject = (Map<String, Object>) eventData.get("object");

            stripePaymentService.handleWebhookEvent(eventType, eventObject);

            return ResponseEntity.ok("Webhook processed successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Webhook processing failed: " + e.getMessage());
        }
    }
}
