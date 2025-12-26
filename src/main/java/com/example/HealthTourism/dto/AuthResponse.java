package com.example.HealthTourism.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Authentication Response DTO
 * Contains JWT tokens and user information after successful authentication
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
    
    /**
     * JWT Access Token
     * Short-lived token for API authentication
     */
    private String accessToken;
    
    /**
     * JWT Refresh Token
     * Long-lived token for obtaining new access tokens
     */
    private String refreshToken;
    
    /**
     * Token type (typically "Bearer")
     */
    private String tokenType;
    
    /**
     * User ID
     */
    private Long userId;
    
    /**
     * User email
     */
    private String email;
    
    /**
     * User role (USER, ADMIN, DOCTOR)
     */
    private String role;
    
    /**
     * Access token expiration time in milliseconds
     */
    private Long expiresIn;
}

