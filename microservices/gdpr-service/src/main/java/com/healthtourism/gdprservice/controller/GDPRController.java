package com.healthtourism.gdprservice.controller;
import com.healthtourism.gdprservice.service.GDPRService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/gdpr")
@CrossOrigin(origins = "*")
public class GDPRController {
    @Autowired
    private GDPRService service;

    @GetMapping("/export")
    public ResponseEntity<byte[]> exportData(
            @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        if (userId == null) userId = 1L;
        return ResponseEntity.ok(service.exportData(userId));
    }

    @DeleteMapping("/account")
    public ResponseEntity<Map<String, Object>> deleteAccount(
            @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        if (userId == null) userId = 1L;
        return ResponseEntity.ok(service.deleteAccount(userId));
    }
}

