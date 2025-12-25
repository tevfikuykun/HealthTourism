package com.healthtourism.jpa.config;

import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

/**
 * JPA AuditorAware Implementation
 * 
 * Provides current user information for audit fields (CreatedBy, UpdatedBy)
 * 
 * Retrieves user identifier from Spring Security context:
 * - If authenticated: returns user email, username, or userId as String
 * - If not authenticated: returns "SYSTEM" for automated processes
 * 
 * Supports multiple authentication mechanisms:
 * - JWT tokens (userId from claims)
 * - UserDetails (username/email)
 * - Custom authentication objects
 */
public class JpaAuditorAware implements AuditorAware<String> {
    
    private static final String SYSTEM_USER = "SYSTEM";
    
    @Override
    public Optional<String> getCurrentAuditor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            // Not authenticated - likely a system process
            return Optional.of(SYSTEM_USER);
        }
        
        Object principal = authentication.getPrincipal();
        String username = authentication.getName();
        
        // Try to get userId from authentication details (JWT claims)
        if (authentication.getDetails() instanceof java.util.Map) {
            @SuppressWarnings("unchecked")
            java.util.Map<String, Object> details = (java.util.Map<String, Object>) authentication.getDetails();
            Object userId = details.get("userId");
            if (userId != null) {
                return Optional.of(String.valueOf(userId));
            }
            
            Object userEmail = details.get("userEmail");
            if (userEmail != null) {
                return Optional.of(String.valueOf(userEmail));
            }
        }
        
        // Try to get userId from UserDetails (if custom implementation)
        if (principal instanceof org.springframework.security.core.userdetails.UserDetails) {
            try {
                java.lang.reflect.Method method = principal.getClass().getMethod("getUserId");
                Object userId = method.invoke(principal);
                if (userId != null) {
                    return Optional.of(String.valueOf(userId));
                }
            } catch (Exception e) {
                // Method not available, continue with username
            }
            
            // Try getEmail method
            try {
                java.lang.reflect.Method method = principal.getClass().getMethod("getEmail");
                Object email = method.invoke(principal);
                if (email != null) {
                    return Optional.of(String.valueOf(email));
                }
            } catch (Exception e) {
                // Method not available, continue with username
            }
        }
        
        // Fallback to authentication name (username/email)
        if (username != null && !username.isEmpty()) {
            return Optional.of(username);
        }
        
        // Last resort: system user
        return Optional.of(SYSTEM_USER);
    }
}

