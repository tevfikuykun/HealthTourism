package com.healthtourism.authservice.service;

import com.healthtourism.authservice.event.OnPasswordResetRequestEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * Password Reset Listener
 * 
 * Listens to password reset request events and sends reset emails asynchronously.
 * Includes IP and User-Agent tracking for security audit.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class PasswordResetListener {
    
    private final EmailService emailService;
    
    /**
     * Handle password reset request event
     * Sends password reset email asynchronously
     */
    @Async
    @EventListener
    public void handlePasswordResetRequest(OnPasswordResetRequestEvent event) {
        try {
            log.info("Sending password reset email to: {} (IP: {}, User-Agent: {})", 
                event.getEmail(), event.getClientIp(), event.getUserAgent());
            
            emailService.sendPasswordResetEmail(
                event.getEmail(),
                event.getResetToken(),
                event.getClientIp(),
                event.getUserAgent()
            );
            
            log.info("Password reset email sent successfully to: {}", event.getEmail());
        } catch (Exception e) {
            log.error("Failed to send password reset email to: {}", event.getEmail(), e);
            // Don't throw exception - email failure shouldn't fail password reset request
        }
    }
}

