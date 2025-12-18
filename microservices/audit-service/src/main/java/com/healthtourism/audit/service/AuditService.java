package com.healthtourism.audit.service;

import com.healthtourism.audit.entity.AuditLog;
import com.healthtourism.audit.repository.AuditLogRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * HIPAA/KVKK Compliant Audit Service
 * Logs all access to PHI (Personal Health Information)
 */
@Service
public class AuditService {
    
    @Autowired
    private AuditLogRepository auditLogRepository;
    
    @Transactional
    public AuditLog logAccess(
            Long userId,
            String userEmail,
            String userRole,
            AuditLog.ResourceType resourceType,
            String resourceId,
            AuditLog.Action action,
            HttpServletRequest request,
            String description,
            Boolean success,
            String errorMessage) {
        
        AuditLog auditLog = new AuditLog();
        auditLog.setUserId(userId);
        auditLog.setUserEmail(userEmail);
        auditLog.setUserRole(userRole);
        auditLog.setResourceType(resourceType);
        auditLog.setResourceId(resourceId);
        auditLog.setAction(action);
        auditLog.setTimestamp(LocalDateTime.now());
        auditLog.setSuccess(success);
        auditLog.setDescription(description);
        auditLog.setErrorMessage(errorMessage);
        
        if (request != null) {
            auditLog.setIpAddress(getClientIpAddress(request));
            auditLog.setUserAgent(request.getHeader("User-Agent"));
            auditLog.setSessionId(request.getSession(false) != null ? request.getSession().getId() : null);
        }
        
        return auditLogRepository.save(auditLog);
    }
    
    public List<AuditLog> getAccessHistoryByUser(Long userId) {
        return auditLogRepository.findByUserIdOrderByTimestampDesc(userId);
    }
    
    public List<AuditLog> getAccessHistoryByResource(AuditLog.ResourceType resourceType, String resourceId) {
        return auditLogRepository.findByResourceTypeAndResourceIdOrderByTimestampDesc(resourceType, resourceId);
    }
    
    public List<AuditLog> getAccessHistoryByAction(AuditLog.Action action, LocalDateTime start, LocalDateTime end) {
        return auditLogRepository.findByActionAndTimestampBetween(action, start, end);
    }
    
    public List<AuditLog> getAccessHistoryByTimeRange(LocalDateTime start, LocalDateTime end) {
        return auditLogRepository.findByTimestampBetweenOrderByTimestampDesc(start, end);
    }
    
    public Long getAccessCountByResource(AuditLog.ResourceType resourceType, String resourceId) {
        return auditLogRepository.countAccessesByResource(resourceType, resourceId);
    }
    
    public List<AuditLog> getRecentAccessesByUser(Long userId, AuditLog.ResourceType resourceType, int days) {
        LocalDateTime since = LocalDateTime.now().minusDays(days);
        return auditLogRepository.findRecentAccessesByUser(userId, resourceType, since);
    }
    
    /**
     * Get logs by date range
     */
    public List<AuditLog> getLogsByDateRange(LocalDateTime start, LocalDateTime end) {
        return auditLogRepository.findByTimestampBetweenOrderByTimestampDesc(start, end);
    }
    
    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
