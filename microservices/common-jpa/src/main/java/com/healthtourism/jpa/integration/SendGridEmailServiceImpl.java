package com.healthtourism.jpa.integration;

import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Attachments;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.Map;

/**
 * SendGrid Email Service Implementation
 * 
 * Professional email service using SendGrid API for transactional emails.
 * Handles appointment confirmations, reminders, and other notifications.
 */
@Service
public class SendGridEmailServiceImpl implements EmailService {
    
    private static final Logger logger = LoggerFactory.getLogger(SendGridEmailServiceImpl.class);
    
    @Value("${sendgrid.api.key:}")
    private String apiKey;
    
    @Value("${sendgrid.from.email:noreply@healthtourism.com}")
    private String fromEmail;
    
    @Value("${sendgrid.from.name:Health Tourism}")
    private String fromName;
    
    @Value("${sendgrid.enabled:true}")
    private boolean enabled;
    
    private SendGrid sendGrid;
    
    @jakarta.annotation.PostConstruct
    public void init() {
        if (enabled && apiKey != null && !apiKey.isEmpty() && !apiKey.equals("your_sendgrid_api_key")) {
            sendGrid = new SendGrid(apiKey);
            logger.info("SendGrid email service initialized");
        } else {
            logger.warn("SendGrid email service disabled or not configured");
        }
    }
    
    @Override
    public void sendEmail(String to, String subject, String htmlContent) {
        sendEmail(to, null, null, subject, htmlContent);
    }
    
    @Override
    public void sendEmail(String to, String cc, String bcc, String subject, String htmlContent) {
        if (!enabled || sendGrid == null) {
            logger.warn("SendGrid not configured, email not sent to: {}", to);
            return;
        }
        
        try {
            Email from = new Email(fromEmail, fromName);
            Email toEmail = new Email(to);
            
            Content content = new Content("text/html", htmlContent);
            Mail mail = new Mail(from, subject, toEmail, content);
            
            if (cc != null && !cc.isEmpty()) {
                mail.addCc(new Email(cc));
            }
            
            if (bcc != null && !bcc.isEmpty()) {
                mail.addBcc(new Email(bcc));
            }
            
            Request request = new Request();
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            
            sendGrid.api(request);
            logger.info("Email sent successfully to: {}", to);
        } catch (IOException e) {
            logger.error("Failed to send email to {}: {}", to, e.getMessage(), e);
            throw new RuntimeException("Email gönderme hatası: " + e.getMessage(), e);
        }
    }
    
    @Override
    public void sendEmailWithAttachments(String to, String subject, String htmlContent, Map<String, Object> attachments) {
        if (!enabled || sendGrid == null) {
            logger.warn("SendGrid not configured, email with attachments not sent to: {}", to);
            return;
        }
        
        try {
            Email from = new Email(fromEmail, fromName);
            Email toEmail = new Email(to);
            Content content = new Content("text/html", htmlContent);
            Mail mail = new Mail(from, subject, toEmail, content);
            
            // Add attachments
            if (attachments != null) {
                for (Map.Entry<String, Object> entry : attachments.entrySet()) {
                    Attachments attachment = new Attachments();
                    attachment.setFilename(entry.getKey());
                    
                    byte[] fileBytes;
                    if (entry.getValue() instanceof String) {
                        // Assume it's a file path
                        fileBytes = Files.readAllBytes(Paths.get((String) entry.getValue()));
                    } else if (entry.getValue() instanceof byte[]) {
                        fileBytes = (byte[]) entry.getValue();
                    } else {
                        logger.warn("Unsupported attachment type for: {}", entry.getKey());
                        continue;
                    }
                    
                    String base64Content = Base64.getEncoder().encodeToString(fileBytes);
                    attachment.setContent(base64Content);
                    
                    // Detect content type from filename
                    String contentType = getContentType(entry.getKey());
                    attachment.setType(contentType);
                    
                    mail.addAttachments(attachment);
                }
            }
            
            Request request = new Request();
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            
            sendGrid.api(request);
            logger.info("Email with attachments sent successfully to: {}", to);
        } catch (IOException e) {
            logger.error("Failed to send email with attachments to {}: {}", to, e.getMessage(), e);
            throw new RuntimeException("Email gönderme hatası: " + e.getMessage(), e);
        }
    }
    
    @Override
    public void sendEmailWithTemplate(String to, String subject, String templateName, Map<String, Object> templateVariables) {
        // For SendGrid dynamic templates, use template ID
        // This is a simplified version - in production, map templateName to SendGrid template ID
        String htmlContent = generateHtmlFromTemplate(templateName, templateVariables);
        sendEmail(to, subject, htmlContent);
    }
    
    @Override
    public void sendAppointmentConfirmation(String to, String patientName, Map<String, String> appointmentDetails) {
        String htmlContent = generateAppointmentConfirmationEmail(patientName, appointmentDetails);
        sendEmail(to, "Randevu Onayı - Health Tourism", htmlContent);
    }
    
    @Override
    public void sendWelcomeEmail(String to, String name) {
        String htmlContent = generateWelcomeEmail(name);
        sendEmail(to, "Health Tourism'a Hoş Geldiniz!", htmlContent);
    }
    
    @Override
    public void sendPasswordResetEmail(String to, String resetToken, int expiryMinutes) {
        String htmlContent = generatePasswordResetEmail(resetToken, expiryMinutes);
        sendEmail(to, "Şifre Sıfırlama - Health Tourism", htmlContent);
    }
    
    @Override
    public void sendAppointmentReminder(String to, String patientName, Map<String, String> appointmentDetails, int reminderHours) {
        String htmlContent = generateAppointmentReminderEmail(patientName, appointmentDetails, reminderHours);
        sendEmail(to, "Randevu Hatırlatıcı - Health Tourism", htmlContent);
    }
    
    // Helper methods for email templates
    
    private String generateWelcomeEmail(String name) {
        return String.format("""
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <style>
                    body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                    .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                    .header { background-color: #4CAF50; color: white; padding: 20px; text-align: center; }
                    .content { padding: 20px; background-color: #f9f9f9; }
                    .footer { text-align: center; padding: 20px; font-size: 12px; color: #666; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>Health Tourism'a Hoş Geldiniz!</h1>
                    </div>
                    <div class="content">
                        <p>Sayın %s,</p>
                        <p>Health Tourism ailesine katıldığınız için teşekkür ederiz. Sağlık turizmi ihtiyaçlarınızda yanınızdayız.</p>
                        <p>Hesabınızı oluşturdunuz ve artık tüm hizmetlerimizden faydalanabilirsiniz.</p>
                    </div>
                    <div class="footer">
                        <p>Health Tourism Ekibi</p>
                        <p>Bu bir otomatik e-postadır, lütfen yanıtlamayın.</p>
                    </div>
                </div>
            </body>
            </html>
            """, name);
    }
    
    private String generateAppointmentConfirmationEmail(String patientName, Map<String, String> appointmentDetails) {
        String date = appointmentDetails.getOrDefault("date", "N/A");
        String time = appointmentDetails.getOrDefault("time", "N/A");
        String doctor = appointmentDetails.getOrDefault("doctor", "N/A");
        String hospital = appointmentDetails.getOrDefault("hospital", "N/A");
        
        return String.format("""
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <style>
                    body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                    .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                    .header { background-color: #2196F3; color: white; padding: 20px; text-align: center; }
                    .content { padding: 20px; background-color: #f9f9f9; }
                    .details { background-color: white; padding: 15px; margin: 15px 0; border-left: 4px solid #2196F3; }
                    .footer { text-align: center; padding: 20px; font-size: 12px; color: #666; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>Randevu Onayı</h1>
                    </div>
                    <div class="content">
                        <p>Sayın %s,</p>
                        <p>Randevunuz başarıyla oluşturulmuştur.</p>
                        <div class="details">
                            <p><strong>Tarih:</strong> %s</p>
                            <p><strong>Saat:</strong> %s</p>
                            <p><strong>Doktor:</strong> %s</p>
                            <p><strong>Hastane:</strong> %s</p>
                        </div>
                        <p>Lütfen randevu saatinde hazır bulunun.</p>
                    </div>
                    <div class="footer">
                        <p>Health Tourism</p>
                        <p>Bu bir otomatik e-postadır, lütfen yanıtlamayın.</p>
                    </div>
                </div>
            </body>
            </html>
            """, patientName, date, time, doctor, hospital);
    }
    
    private String generatePasswordResetEmail(String resetToken, int expiryMinutes) {
        return String.format("""
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <style>
                    body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                    .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                    .header { background-color: #FF9800; color: white; padding: 20px; text-align: center; }
                    .content { padding: 20px; background-color: #f9f9f9; }
                    .button { display: inline-block; padding: 12px 24px; background-color: #4CAF50; color: white; text-decoration: none; border-radius: 4px; margin: 15px 0; }
                    .footer { text-align: center; padding: 20px; font-size: 12px; color: #666; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>Şifre Sıfırlama</h1>
                    </div>
                    <div class="content">
                        <p>Şifrenizi sıfırlamak için aşağıdaki bağlantıya tıklayın:</p>
                        <p style="text-align: center;">
                            <a href="https://healthtourism.com/reset-password?token=%s" class="button">Şifremi Sıfırla</a>
                        </p>
                        <p>Bu bağlantı %d dakika içinde geçerliliğini yitirecektir.</p>
                        <p>Eğer bu işlemi siz yapmadıysanız, bu e-postayı görmezden gelebilirsiniz.</p>
                    </div>
                    <div class="footer">
                        <p>Health Tourism</p>
                        <p>Bu bir otomatik e-postadır, lütfen yanıtlamayın.</p>
                    </div>
                </div>
            </body>
            </html>
            """, resetToken, expiryMinutes);
    }
    
    private String generateAppointmentReminderEmail(String patientName, Map<String, String> appointmentDetails, int reminderHours) {
        String date = appointmentDetails.getOrDefault("date", "N/A");
        String time = appointmentDetails.getOrDefault("time", "N/A");
        String doctor = appointmentDetails.getOrDefault("doctor", "N/A");
        String hospital = appointmentDetails.getOrDefault("hospital", "N/A");
        
        return String.format("""
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <style>
                    body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                    .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                    .header { background-color: #9C27B0; color: white; padding: 20px; text-align: center; }
                    .content { padding: 20px; background-color: #f9f9f9; }
                    .details { background-color: white; padding: 15px; margin: 15px 0; border-left: 4px solid #9C27B0; }
                    .footer { text-align: center; padding: 20px; font-size: 12px; color: #666; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>Randevu Hatırlatıcı</h1>
                    </div>
                    <div class="content">
                        <p>Sayın %s,</p>
                        <p>Randevunuz %d saat sonra gerçekleşecektir.</p>
                        <div class="details">
                            <p><strong>Tarih:</strong> %s</p>
                            <p><strong>Saat:</strong> %s</p>
                            <p><strong>Doktor:</strong> %s</p>
                            <p><strong>Hastane:</strong> %s</p>
                        </div>
                        <p>Lütfen zamanında gelmeyi unutmayın.</p>
                    </div>
                    <div class="footer">
                        <p>Health Tourism</p>
                        <p>Bu bir otomatik e-postadır, lütfen yanıtlamayın.</p>
                    </div>
                </div>
            </body>
            </html>
            """, patientName, reminderHours, date, time, doctor, hospital);
    }
    
    private String generateHtmlFromTemplate(String templateName, Map<String, Object> variables) {
        // Simplified template generation - in production, use a proper template engine
        // or SendGrid dynamic templates with template IDs
        return "<html><body><h1>Email Template: " + templateName + "</h1></body></html>";
    }
    
    private String getContentType(String filename) {
        String extension = filename.substring(filename.lastIndexOf('.') + 1).toLowerCase();
        switch (extension) {
            case "pdf": return "application/pdf";
            case "jpg":
            case "jpeg": return "image/jpeg";
            case "png": return "image/png";
            case "doc": return "application/msword";
            case "docx": return "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
            default: return "application/octet-stream";
        }
    }
}

