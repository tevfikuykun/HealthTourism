package com.healthtourism.biometricservice.controller;
import com.healthtourism.biometricservice.service.BiometricService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/biometric")
@CrossOrigin(origins = "*")
public class BiometricController {
    @Autowired
    private BiometricService service;

    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@RequestBody Map<String, Object> data) {
        return ResponseEntity.ok(service.register(data));
    }

    @PostMapping("/verify")
    public ResponseEntity<Map<String, Object>> verify(@RequestBody Map<String, Object> data) {
        return ResponseEntity.ok(service.verify(data));
    }
}

