package com.healthtourism.audit.controller;

import com.healthtourism.audit.entity.AuditLog;
import com.healthtourism.audit.service.AuditService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/audit")
@CrossOrigin(origins = "*")
public class AuditController {
    
    @Autowired
    private AuditService auditService;
    
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<AuditLog>> getAccessHistoryByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(auditService.getAccessHistoryByUser(userId));
    }
    
    @GetMapping("/resource/{resourceType}/{resourceId}")
    public ResponseEntity<List<AuditLog>> getAccessHistoryByResource(
            @PathVariable AuditLog.ResourceType resourceType,
            @PathVariable String resourceId) {
        return ResponseEntity.ok(auditService.getAccessHistoryByResource(resourceType, resourceId));
    }
    
    @GetMapping("/action/{action}")
    public ResponseEntity<List<AuditLog>> getAccessHistoryByAction(
            @PathVariable AuditLog.Action action,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        return ResponseEntity.ok(auditService.getAccessHistoryByAction(action, start, end));
    }
    
    @GetMapping("/range")
    public ResponseEntity<List<AuditLog>> getAccessHistoryByTimeRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        return ResponseEntity.ok(auditService.getAccessHistoryByTimeRange(start, end));
    }
    
    @GetMapping("/resource/{resourceType}/{resourceId}/count")
    public ResponseEntity<Long> getAccessCountByResource(
            @PathVariable AuditLog.ResourceType resourceType,
            @PathVariable String resourceId) {
        return ResponseEntity.ok(auditService.getAccessCountByResource(resourceType, resourceId));
    }
}
