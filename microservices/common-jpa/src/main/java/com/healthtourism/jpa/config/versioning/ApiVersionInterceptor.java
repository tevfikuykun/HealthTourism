package com.healthtourism.jpa.config.versioning;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * API Version Interceptor
 * 
 * Handles API versioning by:
 * 1. Adding version information to response headers
 * 2. Adding deprecation warnings if version is deprecated
 * 3. Validating version format
 */
@Component
public class ApiVersionInterceptor implements HandlerInterceptor {
    
    private static final Logger logger = LoggerFactory.getLogger(ApiVersionInterceptor.class);
    
    private static final String API_VERSION_HEADER = "X-API-Version";
    private static final String API_VERSION_DEPRECATED_HEADER = "X-API-Version-Deprecated";
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }
        
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        
        // Check for @ApiVersion annotation on controller class
        ApiVersion apiVersion = handlerMethod.getBeanType().getAnnotation(ApiVersion.class);
        
        // Check for @ApiVersion annotation on method
        if (apiVersion == null) {
            apiVersion = handlerMethod.getMethodAnnotation(ApiVersion.class);
        }
        
        if (apiVersion != null) {
            String version = apiVersion.value();
            
            // Add version to response header
            response.setHeader(API_VERSION_HEADER, version);
            
            // Add deprecation warning if deprecated
            if (apiVersion.deprecated()) {
                response.setHeader(API_VERSION_DEPRECATED_HEADER, "true");
                response.setHeader("Warning", "299 - " + apiVersion.deprecationMessage());
                logger.warn("Deprecated API version {} accessed: {}", version, request.getRequestURI());
            }
        } else {
            // Try to extract version from path (/api/v1/...)
            String path = request.getRequestURI();
            if (path.matches("/api/v\\d+/.*")) {
                String version = extractVersionFromPath(path);
                if (version != null) {
                    response.setHeader(API_VERSION_HEADER, version);
                }
            }
        }
        
        return true;
    }
    
    /**
     * Extract version from path (e.g., /api/v1/reservations -> v1)
     */
    private String extractVersionFromPath(String path) {
        String[] parts = path.split("/");
        for (int i = 0; i < parts.length - 1; i++) {
            if (parts[i].equals("api") && i + 1 < parts.length && parts[i + 1].matches("v\\d+")) {
                return parts[i + 1];
            }
        }
        return null;
    }
}

