package com.healthtourism.paymentservice.controller;

import com.healthtourism.paymentservice.integration.PayPalPaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/api/payments/paypal")
@CrossOrigin(origins = "*")
public class PayPalPaymentController {
    
    @Autowired
    private PayPalPaymentService payPalPaymentService;
    
    @PostMapping("/create")
    public ResponseEntity<Map<String, Object>> createOrder(@RequestBody Map<String, Object> request) {
        BigDecimal amount = new BigDecimal(request.get("amount").toString());
        String currency = request.getOrDefault("currency", "USD").toString();
        String description = request.getOrDefault("description", "Health Tourism Payment").toString();
        
        return ResponseEntity.ok(payPalPaymentService.createOrder(amount, currency, description));
    }
    
    @PostMapping("/capture/{orderId}")
    public ResponseEntity<Map<String, Object>> captureOrder(@PathVariable String orderId) {
        return ResponseEntity.ok(payPalPaymentService.captureOrder(orderId));
    }
    
    @GetMapping("/status/{orderId}")
    public ResponseEntity<Map<String, Object>> getOrderStatus(@PathVariable String orderId) {
        return ResponseEntity.ok(payPalPaymentService.getOrderStatus(orderId));
    }
    
    @PostMapping("/refund/{captureId}")
    public ResponseEntity<Map<String, Object>> refundPayment(
            @PathVariable String captureId,
            @RequestBody(required = false) Map<String, Object> request) {
        BigDecimal amount = null;
        if (request != null && request.containsKey("amount")) {
            amount = new BigDecimal(request.get("amount").toString());
        }
        return ResponseEntity.ok(payPalPaymentService.processRefund(captureId, amount));
    }
}

