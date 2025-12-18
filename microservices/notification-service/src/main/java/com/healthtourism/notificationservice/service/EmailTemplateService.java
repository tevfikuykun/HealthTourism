package com.healthtourism.notificationservice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import jakarta.mail.internet.MimeMessage;
import java.util.Map;

/**
 * Email Template Service using Thymeleaf
 */
@Service
public class EmailTemplateService {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private TemplateEngine templateEngine;

    /**
     * Send HTML email using template
     */
    public void sendHtmlEmail(String to, String subject, String templateName, Map<String, Object> variables) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            Context context = new Context();
            if (variables != null) {
                variables.forEach(context::setVariable);
            }

            String htmlContent = templateEngine.process("emails/" + templateName, context);

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);

            mailSender.send(message);
        } catch (Exception e) {
            throw new RuntimeException("Failed to send email: " + e.getMessage(), e);
        }
    }

    /**
     * Send reservation confirmation email
     */
    public void sendReservationConfirmation(String to, Map<String, Object> reservationData) {
        sendHtmlEmail(to, "Rezervasyon Onayı", "reservation-confirmation", reservationData);
    }

    /**
     * Send payment confirmation email
     */
    public void sendPaymentConfirmation(String to, Map<String, Object> paymentData) {
        sendHtmlEmail(to, "Ödeme Onayı", "payment-confirmation", paymentData);
    }

    /**
     * Send welcome email
     */
    public void sendWelcomeEmail(String to, Map<String, Object> userData) {
        sendHtmlEmail(to, "Hoş Geldiniz!", "welcome", userData);
    }
}
