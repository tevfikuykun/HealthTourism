package com.example.HealthTourism.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * Enterprise-grade EmailService with:
 * - Asynchronous email sending (@Async)
 * - Simple text email support
 * - Reservation confirmation emails
 * - Email verification support
 * 
 * Health Tourism Critical: Reservation confirmations, visa document lists,
 * password reset links are sent via email.
 * 
 * Note: For HTML emails and templates, consider using Thymeleaf or FreeMarker.
 */
@Slf4j
@Service
@RequiredArgsConstructor // Constructor Injection - Spring best practice
public class EmailService {
    
    private final JavaMailSender mailSender;
    
    @Value("${spring.mail.username:noreply@healthtourism.com}")
    private String fromEmail;

    /**
     * Sends a simple text email asynchronously.
     * 
     * @Async: Email sending is non-blocking - doesn't slow down main request flow.
     * 
     * @param to Recipient email address
     * @param subject Email subject
     * @param body Email body (text)
     */
    @Async
    public void sendSimpleEmail(String to, String subject, String body) {
        try {
            log.debug("Sending email to: {} with subject: {}", to, subject);
            
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);
            
            mailSender.send(message);
            log.info("Email sent successfully to: {}", to);
        } catch (Exception e) {
            log.error("Failed to send email to: {}", to, e);
            // Note: In production, consider retry mechanism or dead letter queue
        }
    }

    /**
     * Sends reservation confirmation email.
     * Health Tourism Critical: Patients need confirmation for their medical appointments.
     * 
     * @param to Recipient email address
     * @param reservationNumber Reservation number (PNR)
     * @param patientName Patient name (optional, for personalization)
     */
    @Async
    public void sendReservationConfirmation(String to, String reservationNumber, String patientName) {
        log.info("Sending reservation confirmation email to: {} for reservation: {}", to, reservationNumber);
        
        String name = patientName != null ? patientName : "Sayın Müşterimiz";
        String body = String.format(
            "Merhaba %s,\n\n" +
            "%s numaralı rezervasyonunuz başarıyla alındı.\n\n" +
            "Rezervasyon detaylarınızı sistemden takip edebilirsiniz.\n\n" +
            "Teşekkür ederiz.\n\n" +
            "Health Tourism Ekibi",
            name, reservationNumber
        );
        
        sendSimpleEmail(to, "Rezervasyon Onayı - " + reservationNumber, body);
    }
    
    /**
     * Sends reservation confirmation email (simple version).
     */
    @Async
    public void sendReservationConfirmation(String to, String reservationNumber) {
        sendReservationConfirmation(to, reservationNumber, null);
    }
    
    /**
     * Sends email verification email.
     * 
     * @param to Recipient email address
     * @param verificationToken Email verification token
     */
    @Async
    public void sendVerificationEmail(String to, String verificationToken) {
        log.info("Sending email verification email to: {}", to);
        
        // TODO: In production, use a proper URL
        String verificationUrl = "http://localhost:8080/api/auth/verify?token=" + verificationToken;
        
        String body = String.format(
            "Merhaba,\n\n" +
            "Email adresinizi doğrulamak için aşağıdaki linke tıklayınız:\n\n" +
            "%s\n\n" +
            "Bu link 24 saat geçerlidir.\n\n" +
            "Teşekkür ederiz.\n\n" +
            "Health Tourism Ekibi",
            verificationUrl
        );
        
        sendSimpleEmail(to, "Email Doğrulama - Health Tourism", body);
    }
    
    /**
     * Sends password reset email.
     * 
     * @param to Recipient email address
     * @param resetToken Password reset token
     */
    @Async
    public void sendPasswordResetEmail(String to, String resetToken) {
        log.info("Sending password reset email to: {}", to);
        
        // TODO: In production, use a proper URL
        String resetUrl = "http://localhost:8080/api/auth/reset-password?token=" + resetToken;
        
        String body = String.format(
            "Merhaba,\n\n" +
            "Şifrenizi sıfırlamak için aşağıdaki linke tıklayınız:\n\n" +
            "%s\n\n" +
            "Bu link 1 saat geçerlidir.\n\n" +
            "Eğer bu talebi siz yapmadıysanız, bu emaili görmezden gelebilirsiniz.\n\n" +
            "Teşekkür ederiz.\n\n" +
            "Health Tourism Ekibi",
            resetUrl
        );
        
        sendSimpleEmail(to, "Şifre Sıfırlama - Health Tourism", body);
    }
    
    /**
     * Sends visa document list email.
     * Health Tourism Critical: Patients need to know required documents for visa application.
     * 
     * @param to Recipient email address
     * @param country Target country
     * @param documentsList List of required documents
     */
    @Async
    public void sendVisaDocumentsEmail(String to, String country, String documentsList) {
        log.info("Sending visa documents email to: {} for country: {}", to, country);
        
        String body = String.format(
            "Merhaba,\n\n" +
            "%s için vize başvurunuzda gerekli olan belgeler:\n\n" +
            "%s\n\n" +
            "Belgelerinizi hazırlayarak başvurunuzu tamamlayabilirsiniz.\n\n" +
            "Teşekkür ederiz.\n\n" +
            "Health Tourism Ekibi",
            country, documentsList
        );
        
        sendSimpleEmail(to, "Vize Gerekli Belgeler - " + country, body);
    }
}

