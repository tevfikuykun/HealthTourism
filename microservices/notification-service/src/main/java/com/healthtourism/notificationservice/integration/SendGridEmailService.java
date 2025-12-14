package com.healthtourism.notificationservice.integration;

import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.MailSettings;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Map;

@Service
public class SendGridEmailService {
    
    @Value("${sendgrid.api.key}")
    private String apiKey;
    
    @Value("${sendgrid.from.email}")
    private String fromEmail;
    
    @Value("${sendgrid.from.name}")
    private String fromName;
    
    private SendGrid sendGrid;
    
    @jakarta.annotation.PostConstruct
    public void init() {
        sendGrid = new SendGrid(apiKey);
    }
    
    public void sendEmail(String to, String subject, String htmlContent) {
        try {
            Email from = new Email(fromEmail, fromName);
            Email toEmail = new Email(to);
            Content content = new Content("text/html", htmlContent);
            Mail mail = new Mail(from, subject, toEmail, content);
            
            Request request = new Request();
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            
            sendGrid.api(request);
        } catch (IOException e) {
            throw new RuntimeException("Email gönderme hatası: " + e.getMessage(), e);
        }
    }
    
    public void sendWelcomeEmail(String to, String name) {
        String htmlContent = getWelcomeEmailTemplate(name);
        sendEmail(to, "Health Tourism'a Hoş Geldiniz!", htmlContent);
    }
    
    public void sendAppointmentConfirmation(String to, String name, Map<String, String> appointmentDetails) {
        String htmlContent = getAppointmentConfirmationTemplate(name, appointmentDetails);
        sendEmail(to, "Randevu Onayı - Health Tourism", htmlContent);
    }
    
    public void sendPasswordResetEmail(String to, String resetLink) {
        String htmlContent = getPasswordResetTemplate(resetLink);
        sendEmail(to, "Şifre Sıfırlama - Health Tourism", htmlContent);
    }
    
    private String getWelcomeEmailTemplate(String name) {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <style>
                    body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                    .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                    .header { background: #1976d2; color: white; padding: 20px; text-align: center; }
                    .content { padding: 20px; background: #f9f9f9; }
                    .button { display: inline-block; padding: 12px 24px; background: #1976d2; color: white; text-decoration: none; border-radius: 4px; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>Health Tourism'a Hoş Geldiniz!</h1>
                    </div>
                    <div class="content">
                        <p>Merhaba %s,</p>
                        <p>Health Tourism ailesine katıldığınız için teşekkür ederiz!</p>
                        <p>Platformumuzda en iyi hastaneleri, doktorları ve sağlık paketlerini bulabilirsiniz.</p>
                        <p style="text-align: center;">
                            <a href="https://healthtourism.com/dashboard" class="button">Dashboard'a Git</a>
                        </p>
                    </div>
                </div>
            </body>
            </html>
            """.formatted(name);
    }
    
    private String getAppointmentConfirmationTemplate(String name, Map<String, String> details) {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <style>
                    body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                    .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                    .header { background: #1976d2; color: white; padding: 20px; text-align: center; }
                    .content { padding: 20px; background: #f9f9f9; }
                    .details { background: white; padding: 15px; margin: 15px 0; border-radius: 4px; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>Randevu Onayı</h1>
                    </div>
                    <div class="content">
                        <p>Merhaba %s,</p>
                        <p>Randevunuz başarıyla oluşturuldu!</p>
                        <div class="details">
                            <p><strong>Hastane:</strong> %s</p>
                            <p><strong>Doktor:</strong> %s</p>
                            <p><strong>Tarih:</strong> %s</p>
                            <p><strong>Saat:</strong> %s</p>
                        </div>
                    </div>
                </div>
            </body>
            </html>
            """.formatted(name, details.get("hospital"), details.get("doctor"), 
                         details.get("date"), details.get("time"));
    }
    
    private String getPasswordResetTemplate(String resetLink) {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <style>
                    body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                    .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                    .header { background: #1976d2; color: white; padding: 20px; text-align: center; }
                    .content { padding: 20px; background: #f9f9f9; }
                    .button { display: inline-block; padding: 12px 24px; background: #1976d2; color: white; text-decoration: none; border-radius: 4px; }
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
                            <a href="%s" class="button">Şifremi Sıfırla</a>
                        </p>
                        <p><small>Bu bağlantı 1 saat geçerlidir.</small></p>
                    </div>
                </div>
            </body>
            </html>
            """.formatted(resetLink);
    }
}

