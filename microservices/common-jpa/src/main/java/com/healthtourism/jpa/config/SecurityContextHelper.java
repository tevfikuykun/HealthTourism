package com.healthtourism.jpa.config;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.UUID;

/**
 * Security Context Helper
 * 
 * Utility class to safely extract user information from SecurityContext.
 * Prevents IDOR (Insecure Direct Object Reference) vulnerabilities by ensuring
 * user information is always retrieved from the authenticated context, not from
 * request parameters.
 */
public class SecurityContextHelper {
    
    /**
     * Get current user ID from SecurityContext
     * 
     * @return Current user ID (UUID)
     * @throws SecurityException if user is not authenticated
     */
    public static UUID getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new SecurityException("User is not authenticated");
        }
        
        Object principal = authentication.getPrincipal();
        
        // Try to get userId from authentication details (JWT claims)
        if (authentication.getDetails() instanceof java.util.Map) {
            @SuppressWarnings("unchecked")
            java.util.Map<String, Object> details = (java.util.Map<String, Object>) authentication.getDetails();
            Object userId = details.get("userId");
            if (userId != null) {
                if (userId instanceof UUID) {
                    return (UUID) userId;
                }
                return UUID.fromString(userId.toString());
            }
        }
        
        // Try to get userId from UserDetails
        if (principal instanceof UserDetails) {
            try {
                java.lang.reflect.Method method = principal.getClass().getMethod("getUserId");
                Object userId = method.invoke(principal);
                if (userId != null) {
                    if (userId instanceof UUID) {
                        return (UUID) userId;
                    }
                    return UUID.fromString(userId.toString());
                }
            } catch (Exception e) {
                // Method not available, continue
            }
        }
        
        // Fallback: try to parse from principal name
        try {
            String userIdString = authentication.getName();
            if (userIdString != null && !userIdString.isEmpty()) {
                return UUID.fromString(userIdString);
            }
        } catch (Exception e) {
            // Cannot parse as UUID
        }
        
        throw new SecurityException("Unable to extract user ID from SecurityContext");
    }
    
    /**
     * Get current user email from SecurityContext
     * 
     * @return Current user email
     */
    public static String getCurrentUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new SecurityException("User is not authenticated");
        }
        
        // Try to get email from authentication details
        if (authentication.getDetails() instanceof java.util.Map) {
            @SuppressWarnings("unchecked")
            java.util.Map<String, Object> details = (java.util.Map<String, Object>) authentication.getDetails();
            Object email = details.get("userEmail");
            if (email != null) {
                return email.toString();
            }
        }
        
        // Fallback to principal name (usually email in JWT)
        return authentication.getName();
    }
    
    /**
     * Get current user roles from SecurityContext
     * 
     * @return Array of role names
     */
    public static String[] getCurrentUserRoles() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            return new String[0];
        }
        
        return authentication.getAuthorities().stream()
            .map(a -> a.getAuthority())
            .toArray(String[]::new);
    }
    
    /**
     * Check if current user has a specific role
     * 
     * @param role Role name (e.g., "ROLE_ADMIN")
     * @return true if user has the role
     */
    public static boolean hasRole(String role) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }
        
        return authentication.getAuthorities().stream()
            .anyMatch(a -> a.getAuthority().equals(role));
    }
    
    /**
     * Check if current user is authenticated
     * 
     * @return true if user is authenticated
     */
    public static boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && authentication.isAuthenticated();
    }
}

