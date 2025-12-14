package com.healthtourism.referralservice.controller;

import com.healthtourism.referralservice.service.ReferralService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/referral")
@CrossOrigin(origins = "*")
public class ReferralController {
    @Autowired
    private ReferralService referralService;

    @GetMapping("/code")
    public ResponseEntity<Map<String, String>> getCode(
            @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        if (userId == null) userId = 1L;
        Map<String, String> response = Map.of("code", referralService.getCode(userId));
        return ResponseEntity.ok(response);
    }

    @PostMapping("/use")
    public ResponseEntity<Map<String, Object>> useCode(@RequestBody Map<String, String> request) {
        return ResponseEntity.ok(referralService.useCode(request.get("code")));
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats(
            @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        if (userId == null) userId = 1L;
        return ResponseEntity.ok(referralService.getStats(userId));
    }
}

