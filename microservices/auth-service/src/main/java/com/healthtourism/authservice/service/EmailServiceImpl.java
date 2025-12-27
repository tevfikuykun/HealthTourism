package com.healthtourism.authservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 * Email Service Implementation
 * 
 * SMTP-based email service using Spring Mail.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {
    
    private final JavaMailSender mailSender;
    
    @Value("${app.base-url:http://localhost:3000}")
    private String baseUrl;
    
    @Value("${spring.mail.username:your-email@gmail.com}")
    private String fromEmail;
    
    @Override
    public void sendVerificationEmail(String email, String name, String verificationToken) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(email);
            message.setSubject("Email Doğrulama - Health Tourism");
            message.setText(buildVerificationEmailBody(name, verificationToken));
            
            mailSender.send(message);
            log.info("Verification email sent to: {}", email);
        } catch (Exception e) {
            log.error("Failed to send verification email to: {}", email, e);
            throw new RuntimeException("Failed to send verification email", e);
        }
    }
    
    @Override
    public void sendPasswordResetEmail(String email, String resetToken, String clientIp, String userAgent) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(email);
            message.setSubject("Şifre Sıfırlama - Health Tourism");
            message.setText(buildPasswordResetEmailBody(resetToken, clientIp, userAgent));
            
            mailSender.send(message);
            log.info("Password reset email sent to: {}", email);
        } catch (Exception e) {
            log.error("Failed to send password reset email to: {}", email, e);
            throw new RuntimeException("Failed to send password reset email", e);
        }
    }
    
    @Override
    public void sendWelcomeEmail(String email, String name) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(email);
            message.setSubject("Hoş Geldiniz - Health Tourism");
            message.setText(buildWelcomeEmailBody(name));
            
            mailSender.send(message);
            log.info("Welcome email sent to: {}", email);
        } catch (Exception e) {
            log.error("Failed to send welcome email to: {}", email, e);
            throw new RuntimeException("Failed to send welcome email", e);
        }
    }
    
    private String buildVerificationEmailBody(String name, String token) {
        String verificationUrl = baseUrl + "/verify-email?token=" + token;
        return String.format(
            "Merhaba %s,\n\n" +
            "Health Tourism platformuna hoş geldiniz!\n\n" +
            "Email adresinizi doğrulamak için lütfen aşağıdaki bağlantıya tıklayın:\n\n" +
            "%s\n\n" +
            "Bu bağlantı 24 saat geçerlidir.\n\n" +
            "Eğer bu işlemi siz yapmadıysanız, lütfen bu e-postayı görmezden gelin.\n\n" +
            "Saygılarımızla,\n" +
            "Health Tourism Ekibi",
            name, verificationUrl
        );
    }
    
    private String buildPasswordResetEmailBody(String token, String clientIp, String userAgent) {
        String resetUrl = baseUrl + "/reset-password?token=" + token;
        return String.format(
            "Şifre Sıfırlama Talebi\n\n" +
            "Şifrenizi sıfırlamak için aşağıdaki bağlantıya tıklayın:\n\n" +
            "%s\n\n" +
            "Bu bağlantı 1 saat geçerlidir.\n\n" +
            "Talep Detayları:\n" +
            "- IP Adresi: %s\n" +
            "- Tarayıcı: %s\n\n" +
            "Eğer bu işlemi siz yapmadıysanız, lütfen bu e-postayı görmezden gelin ve şifrenizi değiştirin.\n\n" +
            "Saygılarımızla,\n" +
            "Health Tourism Ekibi",
            resetUrl, clientIp != null ? clientIp : "Bilinmiyor", userAgent != null ? userAgent : "Bilinmiyor"
        );
    }
    
    private String buildWelcomeEmailBody(String name) {
        return String.format(
            "Merhaba %s,\n\n" +
            "Email adresiniz başarıyla doğrulandı!\n\n" +
            "Health Tourism platformunda size nasıl yardımcı olabiliriz?\n\n" +
            "Saygılarımızla,\n" +
            "Health Tourism Ekibi",
            name
        );
    }
}

