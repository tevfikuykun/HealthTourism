package com.healthtourism.monitoringservice.controller;

import com.healthtourism.monitoringservice.dto.MetricDTO;
import com.healthtourism.monitoringservice.dto.ServiceHealthDTO;
import com.healthtourism.monitoringservice.service.MonitoringService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/monitoring")
@CrossOrigin(origins = "*")
public class MonitoringController {
    
    @Autowired
    private MonitoringService monitoringService;
    
    @PostMapping("/health")
    public ResponseEntity<ServiceHealthDTO> recordHealth(
            @RequestParam String serviceName,
            @RequestParam String status,
            @RequestParam(required = false) String message,
            @RequestParam(required = false) Long responseTime) {
        return ResponseEntity.ok(monitoringService.recordHealthCheck(serviceName, status, message, responseTime));
    }
    
    @GetMapping("/health/{serviceName}")
    public ResponseEntity<ServiceHealthDTO> getLatestHealth(@PathVariable String serviceName) {
        ServiceHealthDTO health = monitoringService.getLatestHealth(serviceName);
        if (health != null) {
            return ResponseEntity.ok(health);
        }
        return ResponseEntity.notFound().build();
    }
    
    @GetMapping("/health/{serviceName}/history")
    public ResponseEntity<List<ServiceHealthDTO>> getHealthHistory(@PathVariable String serviceName) {
        return ResponseEntity.ok(monitoringService.getHealthHistory(serviceName));
    }
    
    @PostMapping("/metrics")
    public ResponseEntity<MetricDTO> recordMetric(
            @RequestParam String serviceName,
            @RequestParam String metricName,
            @RequestParam Double value,
            @RequestParam(required = false) String unit) {
        return ResponseEntity.ok(monitoringService.recordMetric(serviceName, metricName, value, unit));
    }
    
    @GetMapping("/metrics/{serviceName}")
    public ResponseEntity<List<MetricDTO>> getAllMetrics(@PathVariable String serviceName) {
        return ResponseEntity.ok(monitoringService.getAllMetrics(serviceName));
    }
    
    @GetMapping("/metrics/{serviceName}/{metricName}")
    public ResponseEntity<List<MetricDTO>> getMetrics(
            @PathVariable String serviceName,
            @PathVariable String metricName,
            @RequestParam(required = false) LocalDateTime start,
            @RequestParam(required = false) LocalDateTime end) {
        if (start == null) start = LocalDateTime.now().minusDays(1);
        if (end == null) end = LocalDateTime.now();
        return ResponseEntity.ok(monitoringService.getMetrics(serviceName, metricName, start, end));
    }
}

