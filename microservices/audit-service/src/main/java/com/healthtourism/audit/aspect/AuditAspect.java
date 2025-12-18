package com.healthtourism.audit.aspect;

import com.healthtourism.audit.annotation.Auditable;
import com.healthtourism.audit.entity.AuditLog;
import com.healthtourism.audit.service.AuditService;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * AOP Aspect for automatic audit logging
 */
@Aspect
@Component
public class AuditAspect {
    
    @Autowired
    private AuditService auditService;
    
    @Around("@annotation(auditable)")
    public Object audit(ProceedingJoinPoint joinPoint, Auditable auditable) throws Throwable {
        HttpServletRequest request = null;
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            request = attributes.getRequest();
        }
        
        // Extract user info from request (should be set by security filter)
        Long userId = extractUserId(request);
        String userEmail = extractUserEmail(request);
        String userRole = extractUserRole(request);
        
        // Extract resource ID from method arguments
        String resourceId = extractResourceId(joinPoint.getArgs());
        
        Boolean success = false;
        String errorMessage = null;
        
        try {
            Object result = joinPoint.proceed();
            success = true;
            
            // Log successful access
            auditService.logAccess(
                userId,
                userEmail,
                userRole,
                auditable.resourceType(),
                resourceId,
                auditable.action(),
                request,
                auditable.description(),
                success,
                null
            );
            
            return result;
        } catch (Exception e) {
            errorMessage = e.getMessage();
            auditService.logAccess(
                userId,
                userEmail,
                userRole,
                auditable.resourceType(),
                resourceId,
                auditable.action(),
                request,
                auditable.description(),
                success,
                errorMessage
            );
            throw e;
        }
    }
    
    private Long extractUserId(HttpServletRequest request) {
        if (request != null && request.getAttribute("userId") != null) {
            return Long.parseLong(request.getAttribute("userId").toString());
        }
        return null;
    }
    
    private String extractUserEmail(HttpServletRequest request) {
        if (request != null && request.getAttribute("userEmail") != null) {
            return request.getAttribute("userEmail").toString();
        }
        return "anonymous";
    }
    
    private String extractUserRole(HttpServletRequest request) {
        if (request != null && request.getAttribute("userRole") != null) {
            return request.getAttribute("userRole").toString();
        }
        return "GUEST";
    }
    
    private String extractResourceId(Object[] args) {
        // Try to find resource ID in method arguments
        for (Object arg : args) {
            if (arg instanceof String) {
                return (String) arg;
            } else if (arg instanceof Long) {
                return arg.toString();
            }
        }
        return "unknown";
    }
}
