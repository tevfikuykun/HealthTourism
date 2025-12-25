package com.healthtourism.jpa.util;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Optional;

/**
 * Idempotency Helper
 * 
 * Utility for extracting and validating idempotency keys from HTTP requests.
 * Prevents duplicate operations (e.g., double payment, duplicate reservation).
 * 
 * Usage:
 * - Client sends X-Idempotency-Key header with unique value (UUID recommended)
 * - Server checks if key was used before
 * - If used: return previous result (no duplicate operation)
 * - If not used: process request and store key
 * 
 * Best Practices:
 * - Idempotency key should be UUID (128-bit, globally unique)
 * - Key should be stored with expiration (e.g., 24 hours)
 * - Key should be associated with operation result
 */
@Component
public class IdempotencyHelper {
    
    /**
     * Idempotency key header name
     */
    public static final String IDEMPOTENCY_KEY_HEADER = "X-Idempotency-Key";
    
    /**
     * Extract idempotency key from request header
     * 
     * @param request HTTP request
     * @return Optional idempotency key (empty if not present)
     */
    public Optional<String> extractIdempotencyKey(HttpServletRequest request) {
        if (request == null) {
            return Optional.empty();
        }
        
        String key = request.getHeader(IDEMPOTENCY_KEY_HEADER);
        if (key == null || key.trim().isEmpty()) {
            return Optional.empty();
        }
        
        return Optional.of(key.trim());
    }
    
    /**
     * Extract idempotency key from current request context
     * 
     * @return Optional idempotency key
     */
    public Optional<String> extractIdempotencyKey() {
        ServletRequestAttributes attributes = 
            (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        
        if (attributes != null) {
            return extractIdempotencyKey(attributes.getRequest());
        }
        
        return Optional.empty();
    }
    
    /**
     * Validate idempotency key format
     * 
     * Recommended format: UUID (8-4-4-4-12 hexadecimal digits)
     * 
     * @param key Idempotency key
     * @return true if key format is valid
     */
    public boolean isValidFormat(String key) {
        if (key == null || key.trim().isEmpty()) {
            return false;
        }
        
        // UUID format validation (e.g., "550e8400-e29b-41d4-a716-446655440000")
        return key.matches("^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$");
    }
    
    /**
     * Generate a new idempotency key (UUID)
     * 
     * @return UUID string
     */
    public String generateIdempotencyKey() {
        return java.util.UUID.randomUUID().toString();
    }
}

