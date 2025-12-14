package com.healthtourism.twofactorservice.controller;
import com.healthtourism.twofactorservice.service.TwoFactorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/2fa")
@CrossOrigin(origins = "*")
public class TwoFactorController {
    @Autowired
    private TwoFactorService service;

    @PostMapping("/enable")
    public ResponseEntity<Map<String, Object>> enable(@RequestBody Map<String, Object> request) {
        return ResponseEntity.ok(service.enable(request.get("method").toString(), request));
    }

    @PostMapping("/verify")
    public ResponseEntity<Map<String, Object>> verify(@RequestBody Map<String, String> request) {
        return ResponseEntity.ok(service.verify(request.get("code")));
    }

    @PostMapping("/disable")
    public ResponseEntity<Map<String, Object>> disable() {
        return ResponseEntity.ok(service.disable());
    }

    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getStatus() {
        return ResponseEntity.ok(service.getStatus());
    }
}

