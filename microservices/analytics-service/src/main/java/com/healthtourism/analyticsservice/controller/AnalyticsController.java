package com.healthtourism.analyticsservice.controller;

import com.healthtourism.analyticsservice.service.AnalyticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/analytics")
@CrossOrigin(origins = "*")
public class AnalyticsController {

    @Autowired
    private AnalyticsService analyticsService;

    @GetMapping("/revenue")
    public ResponseEntity<Map<String, Object>> getRevenue(@RequestParam(defaultValue = "monthly") String period) {
        return ResponseEntity.ok(analyticsService.getRevenue(period));
    }

    @GetMapping("/users")
    public ResponseEntity<Map<String, Object>> getUsers(@RequestParam(defaultValue = "monthly") String period) {
        return ResponseEntity.ok(analyticsService.getUsers(period));
    }

    @GetMapping("/reservations")
    public ResponseEntity<Map<String, Object>> getReservations(@RequestParam(defaultValue = "weekly") String period) {
        return ResponseEntity.ok(analyticsService.getReservations(period));
    }

    @GetMapping("/services")
    public ResponseEntity<Map<String, Object>> getServices(@RequestParam(defaultValue = "monthly") String period) {
        return ResponseEntity.ok(analyticsService.getServices(period));
    }
}

