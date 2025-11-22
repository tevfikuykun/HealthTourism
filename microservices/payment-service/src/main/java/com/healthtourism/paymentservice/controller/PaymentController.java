package com.healthtourism.paymentservice.controller;

import com.healthtourism.paymentservice.dto.PaymentDTO;
import com.healthtourism.paymentservice.dto.PaymentRequestDTO;
import com.healthtourism.paymentservice.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
@CrossOrigin(origins = "*")
public class PaymentController {
    
    @Autowired
    private PaymentService paymentService;
    
    @PostMapping
    public ResponseEntity<?> processPayment(@RequestBody PaymentRequestDTO request) {
        try {
            return ResponseEntity.ok(paymentService.processPayment(request));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<PaymentDTO>> getPaymentsByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(paymentService.getPaymentsByUser(userId));
    }
    
    @GetMapping("/number/{paymentNumber}")
    public ResponseEntity<PaymentDTO> getPaymentByNumber(@PathVariable String paymentNumber) {
        try {
            return ResponseEntity.ok(paymentService.getPaymentByNumber(paymentNumber));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @PostMapping("/{id}/refund")
    public ResponseEntity<?> refundPayment(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(paymentService.refundPayment(id));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}

