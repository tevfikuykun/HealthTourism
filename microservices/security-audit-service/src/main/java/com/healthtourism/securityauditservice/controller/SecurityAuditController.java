package com.healthtourism.securityauditservice.controller;

import com.healthtourism.securityauditservice.entity.SecurityAuditLog;
import com.healthtourism.securityauditservice.repository.SecurityAuditLogRepository;
import com.healthtourism.securityauditservice.service.BOLASecurityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/security")
@Tag(name = "Security Audit", description = "Security audit and BOLA protection endpoints")
public class SecurityAuditController {
    
    @Autowired
    private BOLASecurityService bolaSecurityService;
    
    @Autowired
    private SecurityAuditLogRepository auditLogRepository;
    
    @PostMapping("/check-authorization")
    @Operation(summary = "Check BOLA authorization", description = "Verify if user is authorized to access a resource")
    public ResponseEntity<Map<String, Object>> checkAuthorization(
            @RequestParam String userId,
            @RequestParam String resourceId,
            @RequestParam String resourceType,
            @RequestParam String action,
            HttpServletRequest request) {
        
        boolean authorized = bolaSecurityService.checkAuthorization(
            userId, resourceId, resourceType, action, request);
        
        Map<String, Object> response = new HashMap<>();
        response.put("authorized", authorized);
        response.put("userId", userId);
        response.put("resourceId", resourceId);
        response.put("resourceType", resourceType);
        response.put("action", action);
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/audit-logs")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get audit logs", description = "Retrieve security audit logs (Admin only)")
    public ResponseEntity<List<SecurityAuditLog>> getAuditLogs(
            @RequestParam(required = false) String userId,
            @RequestParam(required = false) String resourceType,
            @RequestParam(required = false) Boolean authorized) {
        
        List<SecurityAuditLog> logs;
        
        if (userId != null) {
            logs = auditLogRepository.findAll().stream()
                .filter(log -> log.getUserId().equals(userId))
                .toList();
        } else {
            logs = auditLogRepository.findAll();
        }
        
        if (resourceType != null) {
            logs = logs.stream()
                .filter(log -> log.getResourceType().equals(resourceType))
                .toList();
        }
        
        if (authorized != null) {
            logs = logs.stream()
                .filter(log -> log.getAuthorized().equals(authorized))
                .toList();
        }
        
        return ResponseEntity.ok(logs);
    }
    
    @GetMapping("/suspicious-activity")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get suspicious activity", description = "Get count of suspicious access attempts")
    public ResponseEntity<Map<String, Object>> getSuspiciousActivity(
            @RequestParam(required = false) String userId,
            @RequestParam(defaultValue = "24") int hours) {
        
        long count;
        if (userId != null) {
            count = bolaSecurityService.getSuspiciousAttemptsCount(userId, hours);
        } else {
            count = auditLogRepository.countUnauthorizedAccessByResourceType("ALL", 
                java.time.LocalDateTime.now().minusHours(hours));
        }
        
        Map<String, Object> response = new HashMap<>();
        response.put("suspiciousAttempts", count);
        response.put("hours", hours);
        response.put("userId", userId);
        
        return ResponseEntity.ok(response);
    }
}
