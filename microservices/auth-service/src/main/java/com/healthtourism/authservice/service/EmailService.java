package com.healthtourism.authservice.service;

/**
 * Email Service Interface
 * 
 * Abstraction for email sending operations.
 * Implementations can use SendGrid, SMTP, etc.
 */
public interface EmailService {
    
    /**
     * Send email verification email
     * 
     * @param email User email
     * @param name User name
     * @param verificationToken Verification token
     */
    void sendVerificationEmail(String email, String name, String verificationToken);
    
    /**
     * Send password reset email
     * 
     * @param email User email
     * @param resetToken Reset token
     * @param clientIp Client IP address (for security audit)
     * @param userAgent User-Agent string (for security audit)
     */
    void sendPasswordResetEmail(String email, String resetToken, String clientIp, String userAgent);
    
    /**
     * Send welcome email (after email verification)
     * 
     * @param email User email
     * @param name User name
     */
    void sendWelcomeEmail(String email, String name);
}

