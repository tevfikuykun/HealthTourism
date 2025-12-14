package com.healthtourism.securityalertsservice.controller;
import com.healthtourism.securityalertsservice.service.SecurityAlertsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/security")
@CrossOrigin(origins = "*")
public class SecurityAlertsController {
    @Autowired
    private SecurityAlertsService service;

    @GetMapping("/alerts")
    public ResponseEntity<List<Map<String, Object>>> getAll(@RequestParam Map<String, Object> params) {
        return ResponseEntity.ok(service.getAll(params));
    }

    @PostMapping("/alerts")
    public ResponseEntity<Map<String, Object>> create(@RequestBody Map<String, Object> alert) {
        return ResponseEntity.ok(service.create(alert));
    }

    @PutMapping("/preferences")
    public ResponseEntity<Map<String, Object>> updatePreferences(@RequestBody Map<String, Object> preferences) {
        return ResponseEntity.ok(service.updatePreferences(preferences));
    }
}

