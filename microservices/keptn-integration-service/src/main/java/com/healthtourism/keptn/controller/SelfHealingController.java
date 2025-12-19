package com.healthtourism.keptn.controller;

import com.healthtourism.keptn.entity.SelfHealingEvent;
import com.healthtourism.keptn.repository.SelfHealingEventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Self-Healing Controller
 * Provides endpoints for dashboard
 */
@RestController
@RequestMapping("/api/keptn")
public class SelfHealingController {
    
    @Autowired
    private SelfHealingEventRepository eventRepository;
    
    @GetMapping("/self-healing-events")
    public ResponseEntity<List<SelfHealingEvent>> getSelfHealingEvents() {
        List<SelfHealingEvent> events = eventRepository.findAllByOrderByTimestampDesc();
        return ResponseEntity.ok(events);
    }
    
    @GetMapping("/metrics")
    public ResponseEntity<Map<String, Object>> getMetrics() {
        long totalEvents = eventRepository.count();
        long successfulEvents = eventRepository.findAll().stream()
            .filter(e -> "SUCCESS".equals(e.getStatus()))
            .count();
        
        double uptime = totalEvents > 0 
            ? (double) successfulEvents / totalEvents * 100 
            : 99.9;
        
        Map<String, Object> metrics = new HashMap<>();
        metrics.put("uptime", String.format("%.1f", uptime));
        metrics.put("totalEvents", totalEvents);
        metrics.put("successfulEvents", successfulEvents);
        metrics.put("last24Hours", eventRepository
            .findByTimestampAfterOrderByTimestampDesc(LocalDateTime.now().minusHours(24))
            .size());
        
        return ResponseEntity.ok(metrics);
    }
    
    @GetMapping("/cost-savings")
    public ResponseEntity<Map<String, Object>> getCostSavings() {
        // Calculate cost savings based on prevented incidents
        long incidentsPrevented = eventRepository.count();
        long hoursSaved = incidentsPrevented * 2; // Average 2 hours per incident
        double totalSavings = incidentsPrevented * 500; // $500 per incident prevented
        
        Map<String, Object> savings = new HashMap<>();
        savings.put("incidentsPrevented", incidentsPrevented);
        savings.put("hoursSaved", hoursSaved);
        savings.put("totalSavings", totalSavings);
        
        return ResponseEntity.ok(savings);
    }
}


