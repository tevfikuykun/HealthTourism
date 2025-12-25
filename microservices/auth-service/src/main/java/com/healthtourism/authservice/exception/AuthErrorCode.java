package com.healthtourism.authservice.exception;

/**
 * Authentication Error Codes
 * 
 * Centralized error codes for authentication operations.
 * Used by frontend for internationalization (i18n).
 * 
 * Format: AUTH_XXXX
 */
public enum AuthErrorCode {
    
    // Registration Errors
    EMAIL_ALREADY_IN_USE("AUTH_4001", "Email already registered"),
    INVALID_EMAIL_FORMAT("AUTH_4002", "Invalid email format"),
    WEAK_PASSWORD("AUTH_4003", "Password does not meet security requirements"),
    
    // Login Errors
    INVALID_CREDENTIALS("AUTH_4004", "Invalid email or password"),
    ACCOUNT_INACTIVE("AUTH_4005", "Account is inactive"),
    ACCOUNT_LOCKED("AUTH_4006", "Account is locked"),
    EMAIL_NOT_VERIFIED("AUTH_4007", "Email not verified. Please verify your email first"),
    
    // Token Errors
    INVALID_TOKEN("AUTH_4008", "Invalid token"),
    TOKEN_EXPIRED("AUTH_4009", "Token has expired"),
    TOKEN_ALREADY_USED("AUTH_4010", "Token has already been used"),
    REFRESH_TOKEN_INVALID("AUTH_4011", "Invalid refresh token"),
    REFRESH_TOKEN_EXPIRED("AUTH_4012", "Refresh token has expired"),
    REFRESH_TOKEN_REVOKED("AUTH_4013", "Refresh token has been revoked"),
    
    // Password Reset Errors
    PASSWORD_RESET_REQUEST_LIMIT_EXCEEDED("AUTH_4014", "Too many password reset requests. Please try again later"),
    INVALID_RESET_TOKEN("AUTH_4015", "Invalid or expired password reset token"),
    RESET_TOKEN_ALREADY_USED("AUTH_4016", "Password reset token has already been used"),
    SAME_PASSWORD("AUTH_4017", "New password must be different from current password"),
    
    // Email Verification Errors
    INVALID_VERIFICATION_TOKEN("AUTH_4018", "Invalid or expired verification token"),
    EMAIL_ALREADY_VERIFIED("AUTH_4019", "Email is already verified"),
    VERIFICATION_TOKEN_EXPIRED("AUTH_4020", "Verification token has expired"),
    
    // General Errors
    UNAUTHORIZED("AUTH_401", "Unauthorized access"),
    FORBIDDEN("AUTH_403", "Access forbidden"),
    INTERNAL_ERROR("AUTH_500", "Internal server error");
    
    private final String code;
    private final String defaultMessage;
    
    AuthErrorCode(String code, String defaultMessage) {
        this.code = code;
        this.defaultMessage = defaultMessage;
    }
    
    public String getCode() {
        return code;
    }
    
    public String getDefaultMessage() {
        return defaultMessage;
    }
    
    /**
     * Find error code by code string
     */
    public static AuthErrorCode fromCode(String code) {
        if (code == null || code.trim().isEmpty()) {
            return null;
        }
        for (AuthErrorCode errorCode : values()) {
            if (errorCode.code.equals(code)) {
                return errorCode;
            }
        }
        return null;
    }
}

