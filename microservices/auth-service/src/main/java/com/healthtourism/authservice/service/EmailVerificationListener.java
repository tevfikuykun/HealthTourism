package com.healthtourism.authservice.service;

import com.healthtourism.authservice.event.OnUserRegistrationEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * Email Verification Listener
 * 
 * Listens to user registration events and sends verification emails asynchronously.
 * Prevents blocking the registration process.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class EmailVerificationListener {
    
    private final EmailService emailService;
    
    /**
     * Handle user registration event
     * Sends verification email asynchronously
     */
    @Async
    @EventListener
    public void handleUserRegistration(OnUserRegistrationEvent event) {
        try {
            log.info("Sending verification email to: {}", event.getUser().getEmail());
            
            emailService.sendVerificationEmail(
                event.getUser().getEmail(),
                event.getUser().getFirstName(),
                event.getVerificationToken()
            );
            
            log.info("Verification email sent successfully to: {}", event.getUser().getEmail());
        } catch (Exception e) {
            log.error("Failed to send verification email to: {}", event.getUser().getEmail(), e);
            // Don't throw exception - email failure shouldn't fail registration
        }
    }
}

