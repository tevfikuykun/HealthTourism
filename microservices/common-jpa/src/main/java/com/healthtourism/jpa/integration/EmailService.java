package com.healthtourism.jpa.integration;

import java.util.Map;

/**
 * Email Service Interface
 * 
 * Professional email service abstraction for sending emails via various providers
 * (SendGrid, AWS SES, SMTP, etc.)
 */
public interface EmailService {
    
    /**
     * Send email with HTML content
     * 
     * @param to Recipient email address
     * @param subject Email subject
     * @param htmlContent HTML content of the email
     */
    void sendEmail(String to, String subject, String htmlContent);
    
    /**
     * Send email with HTML content and CC/BCC
     * 
     * @param to Recipient email address
     * @param cc CC email addresses (comma-separated, optional)
     * @param bcc BCC email addresses (comma-separated, optional)
     * @param subject Email subject
     * @param htmlContent HTML content of the email
     */
    void sendEmail(String to, String cc, String bcc, String subject, String htmlContent);
    
    /**
     * Send email with attachments
     * 
     * @param to Recipient email address
     * @param subject Email subject
     * @param htmlContent HTML content
     * @param attachments Map of attachment name to file path or byte array
     */
    void sendEmailWithAttachments(String to, String subject, String htmlContent, Map<String, Object> attachments);
    
    /**
     * Send email using template
     * 
     * @param to Recipient email address
     * @param subject Email subject
     * @param templateName Template name (e.g., "appointment-confirmation")
     * @param templateVariables Variables for template substitution
     */
    void sendEmailWithTemplate(String to, String subject, String templateName, Map<String, Object> templateVariables);
    
    /**
     * Send appointment confirmation email
     * 
     * @param to Recipient email address
     * @param patientName Patient name
     * @param appointmentDetails Appointment details (date, time, doctor, hospital, etc.)
     */
    void sendAppointmentConfirmation(String to, String patientName, Map<String, String> appointmentDetails);
    
    /**
     * Send welcome email
     * 
     * @param to Recipient email address
     * @param name User name
     */
    void sendWelcomeEmail(String to, String name);
    
    /**
     * Send password reset email
     * 
     * @param to Recipient email address
     * @param resetToken Password reset token
     * @param expiryMinutes Token expiry time in minutes
     */
    void sendPasswordResetEmail(String to, String resetToken, int expiryMinutes);
    
    /**
     * Send appointment reminder email
     * 
     * @param to Recipient email address
     * @param patientName Patient name
     * @param appointmentDetails Appointment details
     * @param reminderHours Hours before appointment
     */
    void sendAppointmentReminder(String to, String patientName, Map<String, String> appointmentDetails, int reminderHours);
}

