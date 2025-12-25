package com.healthtourism.authservice.util;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * Client Info Extractor
 * 
 * Extracts client information (IP address, User-Agent) from HTTP requests.
 * Used for security audit logging.
 */
@Component
public class ClientInfoExtractor {
    
    /**
     * Extract client IP address from request
     * Handles proxy headers (X-Forwarded-For, X-Real-IP)
     * 
     * @param request HTTP request
     * @return Client IP address
     */
    public String extractClientIp(HttpServletRequest request) {
        if (request == null) {
            return "unknown";
        }
        
        // Check X-Forwarded-For header (for proxies/load balancers)
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            // X-Forwarded-For can contain multiple IPs, get the first one
            return xForwardedFor.split(",")[0].trim();
        }
        
        // Check X-Real-IP header
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp.trim();
        }
        
        // Fallback to remote address
        return request.getRemoteAddr();
    }
    
    /**
     * Extract client IP address from current request context
     * 
     * @return Client IP address
     */
    public String extractClientIp() {
        ServletRequestAttributes attributes = 
            (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        
        if (attributes != null) {
            return extractClientIp(attributes.getRequest());
        }
        
        return "unknown";
    }
    
    /**
     * Extract User-Agent string from request
     * 
     * @param request HTTP request
     * @return User-Agent string
     */
    public String extractUserAgent(HttpServletRequest request) {
        if (request == null) {
            return "unknown";
        }
        
        String userAgent = request.getHeader("User-Agent");
        return userAgent != null ? userAgent : "unknown";
    }
    
    /**
     * Extract User-Agent string from current request context
     * 
     * @return User-Agent string
     */
    public String extractUserAgent() {
        ServletRequestAttributes attributes = 
            (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        
        if (attributes != null) {
            return extractUserAgent(attributes.getRequest());
        }
        
        return "unknown";
    }
}

