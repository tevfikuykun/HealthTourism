package com.healthtourism.reminder.service;

import com.healthtourism.reminder.entity.Reminder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * Notification Service Integration
 * Sends reminders via email, SMS, and push notifications
 */
@Service
public class NotificationService {
    
    @Value("${notification.service.url:http://localhost:8010}")
    private String notificationServiceUrl;
    
    @Autowired
    private MessagePersonalizationService personalizationService;
    
    private final RestTemplate restTemplate = new RestTemplate();
    
    public void sendEmailReminder(Reminder reminder) {
        try {
            Map<String, Object> emailData = new HashMap<>();
            emailData.put("to", reminder.getUserEmail());
            emailData.put("subject", getEmailSubject(reminder));
            emailData.put("template", "quote-reminder");
            emailData.put("variables", Map.of(
                "message", reminder.getMessage(),
                "userName", "Değerli Müşterimiz",
                "actionUrl", getActionUrl(reminder)
            ));
            
            restTemplate.postForObject(
                notificationServiceUrl + "/api/notifications/email",
                emailData,
                Void.class
            );
        } catch (Exception e) {
            throw new RuntimeException("Failed to send email reminder", e);
        }
    }
    
    public void sendSMSReminder(Reminder reminder) {
        try {
            Map<String, Object> smsData = new HashMap<>();
            smsData.put("to", reminder.getUserPhone());
            smsData.put("message", reminder.getMessage());
            
            restTemplate.postForObject(
                notificationServiceUrl + "/api/notifications/sms",
                smsData,
                Void.class
            );
        } catch (Exception e) {
            throw new RuntimeException("Failed to send SMS reminder", e);
        }
    }
    
    public void sendPushReminder(Reminder reminder) {
        try {
            Map<String, Object> pushData = new HashMap<>();
            pushData.put("userId", reminder.getUserId());
            pushData.put("title", "Hatırlatma");
            pushData.put("message", reminder.getMessage());
            pushData.put("data", Map.of("reminderId", reminder.getId()));
            
            restTemplate.postForObject(
                notificationServiceUrl + "/api/notifications/push",
                pushData,
                Void.class
            );
        } catch (Exception e) {
            throw new RuntimeException("Failed to send push reminder", e);
        }
    }
    
    private String getEmailSubject(Reminder reminder) {
        // Use personalized subject if available
        if (reminder.getIsPersonalized() != null && reminder.getIsPersonalized()) {
            String language = reminder.getLanguage() != null ? reminder.getLanguage() : "tr";
            return personalizationService.generateEmailSubject(
                reminder.getReminderType(),
                reminder.getRecipientName(),
                reminder.getTreatmentType(),
                language
            );
        }
        
        // Fallback to default subjects
        switch (reminder.getReminderType()) {
            case QUOTE_PENDING:
                return "Teklifinizi Değerlendirmeyi Unutmayın";
            case QUOTE_EXPIRING:
                return "Teklifinizin Süresi Dolmak Üzere";
            case LEAD_FOLLOW_UP:
                return "Size Nasıl Yardımcı Olabiliriz?";
            default:
                return "Hatırlatma";
        }
    }
    
    private String getActionUrl(Reminder reminder) {
        switch (reminder.getReminderType()) {
            case QUOTE_PENDING:
            case QUOTE_EXPIRING:
                return "https://healthtourism.com/quotes/" + reminder.getEntityId();
            case LEAD_FOLLOW_UP:
                return "https://healthtourism.com/contact";
            default:
                return "https://healthtourism.com";
        }
    }
}
