package com.healthtourism.jpa.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.UUID;

/**
 * Security Context Helper
 * 
 * Utility class for extracting user information from Spring Security context.
 * Prevents IDOR (Insecure Direct Object Reference) vulnerabilities by ensuring
 * user IDs are always retrieved from the authenticated token, not from URL parameters.
 */
public class SecurityContextHelper {
    
    /**
     * Get current authenticated user ID from Security Context
     * 
     * @return User ID (UUID)
     * @throws IllegalStateException if user is not authenticated
     */
    public static UUID getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated() || 
            "anonymousUser".equals(authentication.getPrincipal())) {
            throw new IllegalStateException("User is not authenticated");
        }
        
        // Extract user ID from principal
        Object principal = authentication.getPrincipal();
        
        if (principal instanceof UserDetails) {
            // If principal is UserDetails, extract ID from username or custom field
            String username = ((UserDetails) principal).getUsername();
            // Assuming username is email or UUID string
            try {
                return UUID.fromString(username);
            } catch (IllegalArgumentException e) {
                throw new IllegalStateException("Unable to extract user ID from principal");
            }
        } else if (principal instanceof String) {
            // If principal is String (typically email or UUID)
            try {
                return UUID.fromString((String) principal);
            } catch (IllegalArgumentException e) {
                throw new IllegalStateException("Unable to extract user ID from principal");
            }
        } else {
            // Custom principal object - extract ID from it
            // This depends on your JWT token structure
            // Example: if JWT contains userId claim
            throw new IllegalStateException("Unsupported principal type: " + principal.getClass().getName());
        }
    }
    
    /**
     * Get current authenticated user ID as Long
     * (for backward compatibility with Long-based user IDs)
     * 
     * @return User ID (Long)
     * @throws IllegalStateException if user is not authenticated
     */
    public static Long getCurrentUserIdAsLong() {
        UUID userId = getCurrentUserId();
        // Convert UUID to Long (not recommended, but for backward compatibility)
        // This assumes UUID is stored as Long in database (first 8 bytes)
        return userId.getMostSignificantBits();
    }
    
    /**
     * Get current authenticated username (email) from Security Context
     * 
     * @return Username/Email
     * @throws IllegalStateException if user is not authenticated
     */
    public static String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated() || 
            "anonymousUser".equals(authentication.getPrincipal())) {
            throw new IllegalStateException("User is not authenticated");
        }
        
        return authentication.getName();
    }
    
    /**
     * Get current authentication object
     * 
     * @return Authentication object
     */
    public static Authentication getCurrentAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }
    
    /**
     * Check if current user is authenticated
     * 
     * @return true if user is authenticated
     */
    public static boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && 
               authentication.isAuthenticated() && 
               !"anonymousUser".equals(authentication.getPrincipal());
    }
    
    /**
     * Check if current user has specific role
     * 
     * @param role Role to check
     * @return true if user has the role
     */
    public static boolean hasRole(String role) {
        if (!isAuthenticated()) {
            return false;
        }
        
        Authentication authentication = getCurrentAuthentication();
        return authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_" + role) || 
                                     authority.getAuthority().equals(role));
    }
    
    /**
     * Get current user email (alias for getCurrentUsername)
     * 
     * @return User email
     */
    public static String getCurrentUserEmail() {
        return getCurrentUsername();
    }
}

