package com.healthtourism.loyaltyservice.controller;
import com.healthtourism.loyaltyservice.service.LoyaltyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/loyalty")
@CrossOrigin(origins = "*")
public class LoyaltyController {
    @Autowired
    private LoyaltyService service;

    @GetMapping("/points")
    public ResponseEntity<Map<String, Object>> getPoints(
            @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        if (userId == null) userId = 1L;
        return ResponseEntity.ok(service.getPoints(userId));
    }

    @PostMapping("/redeem")
    public ResponseEntity<Map<String, Object>> redeem(@RequestBody Map<String, Long> request) {
        return ResponseEntity.ok(service.redeem(request.get("rewardId")));
    }

    @GetMapping("/rewards")
    public ResponseEntity<List<Map<String, Object>>> getRewards() {
        return ResponseEntity.ok(service.getRewards());
    }

    @GetMapping("/history")
    public ResponseEntity<List<Map<String, Object>>> getHistory(
            @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        if (userId == null) userId = 1L;
        return ResponseEntity.ok(service.getHistory(userId));
    }
}

