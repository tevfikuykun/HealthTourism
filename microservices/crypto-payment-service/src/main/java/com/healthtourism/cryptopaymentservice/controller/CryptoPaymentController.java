package com.healthtourism.cryptopaymentservice.controller;
import com.healthtourism.cryptopaymentservice.service.CryptoPaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/crypto")
@CrossOrigin(origins = "*")
public class CryptoPaymentController {
    @Autowired
    private CryptoPaymentService cryptoPaymentService;

    @PostMapping("/payment")
    public ResponseEntity<Map<String, Object>> createPayment(@RequestBody Map<String, Object> payment) {
        return ResponseEntity.ok(cryptoPaymentService.createPayment(payment));
    }

    @GetMapping("/status/{id}")
    public ResponseEntity<Map<String, Object>> getStatus(@PathVariable String id) {
        return ResponseEntity.ok(cryptoPaymentService.getStatus(id));
    }
}

