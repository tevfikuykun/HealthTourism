package com.healthtourism.installmentservice.controller;
import com.healthtourism.installmentservice.service.InstallmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/installments")
@CrossOrigin(origins = "*")
public class InstallmentController {
    @Autowired
    private InstallmentService installmentService;

    @GetMapping("/plans")
    public ResponseEntity<List<Map<String, Object>>> getPlans(@RequestParam Double amount) {
        return ResponseEntity.ok(installmentService.getPlans(amount));
    }

    @PostMapping("/calculate")
    public ResponseEntity<Map<String, Object>> calculate(@RequestBody Map<String, Object> request) {
        Double amount = Double.valueOf(request.get("amount").toString());
        Integer installments = Integer.valueOf(request.get("installments").toString());
        return ResponseEntity.ok(installmentService.calculate(amount, installments));
    }
}

