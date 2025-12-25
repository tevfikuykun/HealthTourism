package com.healthtourism.jpa.config.logging;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import java.time.LocalDateTime;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * Request Logging Interceptor
 * 
 * Logs all incoming HTTP requests for security audit and debugging.
 * 
 * Logged Information:
 * - Request method, URI, query parameters
 * - Client IP address
 * - User-Agent
 * - Request headers (sensitive headers excluded)
 * - Response status code
 * - Request duration
 * 
 * Security:
 * - Excludes sensitive headers (Authorization, X-Idempotency-Key)
 * - Logs IP address for security audit
 * - Logs User-Agent for fraud detection
 */
@Component
@Slf4j
public class RequestLoggingInterceptor implements HandlerInterceptor {
    
    private static final String START_TIME_ATTRIBUTE = "requestStartTime";
    
    // Sensitive headers to exclude from logging
    private static final String[] SENSITIVE_HEADERS = {
        "Authorization",
        "X-Idempotency-Key",
        "Cookie",
        "X-API-Key"
    };
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // Record start time
        request.setAttribute(START_TIME_ATTRIBUTE, System.currentTimeMillis());
        
        // Log request
        logRequest(request);
        
        return true;
    }
    
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, 
                          ModelAndView modelAndView) {
        // Log response
        Long startTime = (Long) request.getAttribute(START_TIME_ATTRIBUTE);
        if (startTime != null) {
            long duration = System.currentTimeMillis() - startTime;
            logResponse(request, response, duration);
        }
    }
    
    /**
     * Log incoming request
     */
    private void logRequest(HttpServletRequest request) {
        try {
            String method = request.getMethod();
            String uri = request.getRequestURI();
            String queryString = request.getQueryString();
            String fullUri = queryString != null ? uri + "?" + queryString : uri;
            
            String clientIp = extractClientIp(request);
            String userAgent = request.getHeader("User-Agent");
            
            Map<String, String> headers = extractHeaders(request);
            
            log.info("Incoming Request: {} {} | IP: {} | User-Agent: {} | Headers: {}",
                method, fullUri, clientIp, userAgent, headers);
        } catch (Exception e) {
            log.error("Failed to log request", e);
        }
    }
    
    /**
     * Log response
     */
    private void logResponse(HttpServletRequest request, HttpServletResponse response, long duration) {
        try {
            String method = request.getMethod();
            String uri = request.getRequestURI();
            int statusCode = response.getStatus();
            
            log.info("Response: {} {} | Status: {} | Duration: {}ms",
                method, uri, statusCode, duration);
        } catch (Exception e) {
            log.error("Failed to log response", e);
        }
    }
    
    /**
     * Extract client IP address (handles proxy headers)
     */
    private String extractClientIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp.trim();
        }
        
        return request.getRemoteAddr();
    }
    
    /**
     * Extract request headers (excluding sensitive ones)
     */
    private Map<String, String> extractHeaders(HttpServletRequest request) {
        Map<String, String> headers = new HashMap<>();
        
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            
            // Skip sensitive headers
            if (isSensitiveHeader(headerName)) {
                headers.put(headerName, "***REDACTED***");
            } else {
                headers.put(headerName, request.getHeader(headerName));
            }
        }
        
        return headers;
    }
    
    /**
     * Check if header is sensitive
     */
    private boolean isSensitiveHeader(String headerName) {
        for (String sensitive : SENSITIVE_HEADERS) {
            if (sensitive.equalsIgnoreCase(headerName)) {
                return true;
            }
        }
        return false;
    }
}

