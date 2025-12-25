package com.healthtourism.userservice.entity;

/**
 * User Role Enum
 * 
 * Defines user roles in the system.
 * Using enum prevents typos and ensures data consistency.
 * 
 * Roles:
 * - USER: Regular patient/user
 * - DOCTOR: Healthcare provider
 * - ADMIN: System administrator
 * - AGENT: Travel/healthcare agent
 * - MODERATOR: Content moderator (optional)
 */
public enum Role {
    
    /**
     * Regular user/patient
     */
    ROLE_USER("USER", "Kullanıcı"),
    
    /**
     * Healthcare provider (doctor)
     */
    ROLE_DOCTOR("DOCTOR", "Doktor"),
    
    /**
     * System administrator
     */
    ROLE_ADMIN("ADMIN", "Yönetici"),
    
    /**
     * Travel/healthcare agent
     */
    ROLE_AGENT("AGENT", "Acenta"),
    
    /**
     * Content moderator (optional)
     */
    ROLE_MODERATOR("MODERATOR", "Moderatör");
    
    private final String code;
    private final String displayName;
    
    Role(String code, String displayName) {
        this.code = code;
        this.displayName = displayName;
    }
    
    public String getCode() {
        return code;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    /**
     * Find role by code (case-insensitive)
     */
    public static Role fromCode(String code) {
        if (code == null || code.trim().isEmpty()) {
            return null;
        }
        
        // Handle both "ROLE_USER" and "USER" formats
        String normalizedCode = code.trim().toUpperCase();
        if (!normalizedCode.startsWith("ROLE_")) {
            normalizedCode = "ROLE_" + normalizedCode;
        }
        
        for (Role role : values()) {
            if (role.name().equals(normalizedCode) || role.code.equalsIgnoreCase(code)) {
                return role;
            }
        }
        
        return null;
    }
    
    /**
     * Check if role is admin
     */
    public boolean isAdmin() {
        return this == ROLE_ADMIN;
    }
    
    /**
     * Check if role is doctor
     */
    public boolean isDoctor() {
        return this == ROLE_DOCTOR;
    }
    
    /**
     * Check if role is user
     */
    public boolean isUser() {
        return this == ROLE_USER;
    }
    
    /**
     * Check if role has elevated privileges
     */
    public boolean hasElevatedPrivileges() {
        return this == ROLE_ADMIN || this == ROLE_MODERATOR;
    }
}

