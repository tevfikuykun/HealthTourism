package com.healthtourism.paymentservice.controller;

import com.healthtourism.paymentservice.integration.StripePaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/payments/stripe")
@CrossOrigin(origins = "*")
public class StripePaymentController {
    
    @Autowired
    private StripePaymentService stripePaymentService;
    
    @PostMapping("/create")
    public ResponseEntity<Map<String, Object>> createPaymentIntent(@RequestBody Map<String, Object> request) {
        Double amount = Double.valueOf(request.get("amount").toString());
        String currency = request.getOrDefault("currency", "usd").toString();
        String description = request.getOrDefault("description", "Health Tourism Payment").toString();
        
        return ResponseEntity.ok(stripePaymentService.createPaymentIntent(amount, currency, description));
    }
    
    @PostMapping("/confirm")
    public ResponseEntity<Map<String, Object>> confirmPayment(@RequestBody Map<String, String> request) {
        String paymentIntentId = request.get("paymentIntentId");
        return ResponseEntity.ok(stripePaymentService.confirmPayment(paymentIntentId));
    }
    
    @GetMapping("/status/{paymentIntentId}")
    public ResponseEntity<Map<String, Object>> getPaymentStatus(@PathVariable String paymentIntentId) {
        return ResponseEntity.ok(stripePaymentService.getPaymentStatus(paymentIntentId));
    }
}

