package com.healthtourism.securityauditservice.service;

import com.healthtourism.securityauditservice.entity.SecurityAuditLog;
import com.healthtourism.securityauditservice.repository.SecurityAuditLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * BOLA (Broken Object Level Authorization) Security Service
 * Prevents unauthorized access to resources by checking ownership
 */
@Service
public class BOLASecurityService {
    
    @Autowired
    private SecurityAuditLogRepository auditLogRepository;
    
    @Autowired(required = false)
    private RestTemplate restTemplate;
    
    @Value("${health.wallet.service.url:http://localhost:8037}")
    private String healthWalletServiceUrl;
    
    @Value("${reservation.service.url:http://localhost:8009}")
    private String reservationServiceUrl;
    
    private RestTemplate getRestTemplate() {
        if (restTemplate == null) {
            restTemplate = new RestTemplate();
        }
        return restTemplate;
    }
    
    /**
     * Check if user is authorized to access a resource (BOLA check)
     * 
     * @param userId User ID from JWT token
     * @param resourceId Resource ID being accessed
     * @param resourceType Type of resource (HEALTH_WALLET, RESERVATION, etc.)
     * @param action Action being performed (READ, WRITE, DELETE)
     * @param request HTTP request for IP and User-Agent
     * @return true if authorized, false otherwise
     */
    public boolean checkAuthorization(String userId, String resourceId, 
                                     String resourceType, String action,
                                     HttpServletRequest request) {
        
        boolean authorized = false;
        String reason = "";
        
        try {
            switch (resourceType) {
                case "HEALTH_WALLET":
                    authorized = checkHealthWalletAccess(userId, resourceId);
                    reason = authorized ? "User owns this health wallet" : "User does not own this health wallet";
                    break;
                    
                case "RESERVATION":
                    authorized = checkReservationAccess(userId, resourceId);
                    reason = authorized ? "User owns this reservation" : "User does not own this reservation";
                    break;
                    
                case "MEDICAL_RECORD":
                    authorized = checkMedicalRecordAccess(userId, resourceId);
                    reason = authorized ? "User authorized for this medical record" : "User not authorized for this medical record";
                    break;
                    
                default:
                    authorized = false;
                    reason = "Unknown resource type";
            }
            
        } catch (Exception e) {
            authorized = false;
            reason = "Error during authorization check: " + e.getMessage();
        }
        
        // Log the access attempt
        logAccessAttempt(userId, resourceId, resourceType, action, 
                        request.getRemoteAddr(), request.getHeader("User-Agent"),
                        authorized, reason);
        
        return authorized;
    }
    
    /**
     * Check if user owns the health wallet
     */
    private boolean checkHealthWalletAccess(String userId, String walletId) {
        try {
            String url = healthWalletServiceUrl + "/api/health-wallet/" + walletId + "/verify-owner?userId=" + userId;
            ResponseEntity<Map> response = getRestTemplate().getForEntity(url, Map.class);
            
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return Boolean.TRUE.equals(response.getBody().get("isOwner"));
            }
        } catch (Exception e) {
            // If service is unavailable, deny access (fail-safe)
            return false;
        }
        return false;
    }
    
    /**
     * Check if user owns the reservation
     */
    private boolean checkReservationAccess(String userId, String reservationId) {
        try {
            String url = reservationServiceUrl + "/api/reservations/" + reservationId + "/verify-owner?userId=" + userId;
            ResponseEntity<Map> response = getRestTemplate().getForEntity(url, Map.class);
            
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return Boolean.TRUE.equals(response.getBody().get("isOwner"));
            }
        } catch (Exception e) {
            // If service is unavailable, deny access (fail-safe)
            return false;
        }
        return false;
    }
    
    /**
     * Check if user is authorized to access medical record
     */
    private boolean checkMedicalRecordAccess(String userId, String recordId) {
        // Check via Privacy Compliance Service
        // For now, return false (should be implemented with Privacy Service)
        return false;
    }
    
    /**
     * Log access attempt for audit trail
     */
    private void logAccessAttempt(String userId, String resourceId, String resourceType,
                                  String action, String ipAddress, String userAgent,
                                  boolean authorized, String reason) {
        SecurityAuditLog log = new SecurityAuditLog();
        log.setUserId(userId);
        log.setResourceId(resourceId);
        log.setResourceType(resourceType);
        log.setAction(action);
        log.setIpAddress(ipAddress);
        log.setUserAgent(userAgent != null ? userAgent : "Unknown");
        log.setTimestamp(LocalDateTime.now());
        log.setStatus(authorized ? "ALLOWED" : "DENIED");
        log.setReason(reason);
        log.setAuthorized(authorized);
        
        auditLogRepository.save(log);
        
        // If unauthorized access attempt, mark as suspicious
        if (!authorized) {
            // Could trigger alert here
            System.err.println("SECURITY ALERT: Unauthorized access attempt - User: " + userId + 
                             ", Resource: " + resourceId + ", Reason: " + reason);
        }
    }
    
    /**
     * Get suspicious access attempts
     */
    public long getSuspiciousAttemptsCount(String userId, int hours) {
        LocalDateTime since = LocalDateTime.now().minusHours(hours);
        return auditLogRepository.countByUserIdAndAuthorizedAndTimestampAfter(
            userId, false, since);
    }
}
