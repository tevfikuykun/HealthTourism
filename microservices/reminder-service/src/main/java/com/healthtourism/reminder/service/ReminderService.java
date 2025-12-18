package com.healthtourism.reminder.service;

import com.healthtourism.reminder.entity.Reminder;
import com.healthtourism.reminder.repository.ReminderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Reminder Service
 * Manages automatic reminders for quotes, leads, and other business events
 */
@Service
public class ReminderService {
    
    @Autowired
    private ReminderRepository reminderRepository;
    
    @Autowired
    private NotificationService notificationService;
    
    @Autowired
    private ABTestingService abTestingService;
    
    @Autowired
    private TimezoneService timezoneService;
    
    @Autowired
    private MessagePersonalizationService personalizationService;
    
    @Value("${quote.service.url:http://localhost:8035}")
    private String quoteServiceUrl;
    
    @Value("${crm.service.url:http://localhost:8036}")
    private String crmServiceUrl;
    
    private final RestTemplate restTemplate = new RestTemplate();
    
    /**
     * Create a reminder for a pending quote
     * Sends reminder 2 days after quote is sent
     * Includes A/B testing, timezone awareness, and personalization
     */
    @Transactional
    public Reminder createQuoteReminder(
            Long quoteId, 
            Long userId, 
            String userEmail, 
            String userPhone, 
            LocalDateTime quoteSentAt,
            String timezone,
            String recipientName,
            String treatmentType,
            String quoteNumber,
            String language) {
        
        Reminder reminder = new Reminder();
        reminder.setReminderType(Reminder.ReminderType.QUOTE_PENDING);
        reminder.setEntityId(quoteId);
        reminder.setUserId(userId);
        reminder.setUserEmail(userEmail);
        reminder.setUserPhone(userPhone);
        reminder.setStatus(Reminder.ReminderStatus.PENDING);
        reminder.setCreatedAt(LocalDateTime.now());
        reminder.setChannel(Reminder.NotificationChannel.ALL);
        reminder.setRetryCount(0);
        
        // A/B Testing: Assign variant
        String abVariant = abTestingService.assignVariant(Reminder.ReminderType.QUOTE_PENDING);
        reminder.setAbTestVariant(abVariant);
        
        // Timezone awareness: Adjust scheduled time
        LocalDateTime baseScheduledAt = quoteSentAt.plusDays(2);
        if (timezone != null && !timezone.isEmpty()) {
            reminder.setTimezone(timezone);
            LocalDateTime adjustedTime = timezoneService.getOptimalSendingTime(timezone, 2);
            reminder.setScheduledAt(adjustedTime);
        } else {
            reminder.setScheduledAt(baseScheduledAt);
        }
        
        // Personalization: Generate personalized message
        reminder.setIsPersonalized(true);
        reminder.setRecipientName(recipientName);
        reminder.setTreatmentType(treatmentType);
        reminder.setLanguage(language != null ? language : "tr"); // Default to Turkish
        String personalizedMessage = personalizationService.generatePersonalizedMessage(
            Reminder.ReminderType.QUOTE_PENDING,
            recipientName,
            treatmentType,
            quoteNumber,
            abVariant,
            language != null ? language : "tr"
        );
        reminder.setMessage(personalizedMessage);
        
        return reminderRepository.save(reminder);
    }
    
    /**
     * Create a reminder for expiring quote
     * Includes A/B testing, timezone awareness, and personalization
     */
    @Transactional
    public Reminder createQuoteExpiringReminder(
            Long quoteId, 
            Long userId, 
            String userEmail, 
            String userPhone, 
            LocalDateTime validUntil,
            String timezone,
            String recipientName,
            String treatmentType,
            String quoteNumber,
            String language) {
        
        Reminder reminder = new Reminder();
        reminder.setReminderType(Reminder.ReminderType.QUOTE_EXPIRING);
        reminder.setEntityId(quoteId);
        reminder.setUserId(userId);
        reminder.setUserEmail(userEmail);
        reminder.setUserPhone(userPhone);
        reminder.setStatus(Reminder.ReminderStatus.PENDING);
        reminder.setCreatedAt(LocalDateTime.now());
        reminder.setChannel(Reminder.NotificationChannel.ALL);
        reminder.setRetryCount(0);
        
        // A/B Testing: Assign variant
        String abVariant = abTestingService.assignVariant(Reminder.ReminderType.QUOTE_EXPIRING);
        reminder.setAbTestVariant(abVariant);
        
        // Timezone awareness: Adjust scheduled time (1 day before expiration)
        LocalDateTime baseScheduledAt = validUntil.minusDays(1);
        if (timezone != null && !timezone.isEmpty()) {
            reminder.setTimezone(timezone);
            LocalDateTime adjustedTime = timezoneService.adjustForTimezone(baseScheduledAt, timezone);
            reminder.setScheduledAt(adjustedTime);
        } else {
            reminder.setScheduledAt(baseScheduledAt);
        }
        
        // Personalization: Generate personalized message
        reminder.setIsPersonalized(true);
        reminder.setRecipientName(recipientName);
        reminder.setTreatmentType(treatmentType);
        reminder.setLanguage(language != null ? language : "tr"); // Default to Turkish
        String personalizedMessage = personalizationService.generatePersonalizedMessage(
            Reminder.ReminderType.QUOTE_EXPIRING,
            recipientName,
            treatmentType,
            quoteNumber,
            abVariant,
            language != null ? language : "tr"
        );
        reminder.setMessage(personalizedMessage);
        
        return reminderRepository.save(reminder);
    }
    
    /**
     * Create a follow-up reminder for a lead
     * Includes timezone awareness and personalization
     */
    @Transactional
    public Reminder createLeadFollowUpReminder(
            Long leadId, 
            Long userId, 
            String userEmail, 
            String userPhone, 
            int daysLater,
            String timezone,
            String recipientName,
            String language) {
        
        Reminder reminder = new Reminder();
        reminder.setReminderType(Reminder.ReminderType.LEAD_FOLLOW_UP);
        reminder.setEntityId(leadId);
        reminder.setUserId(userId);
        reminder.setUserEmail(userEmail);
        reminder.setUserPhone(userPhone);
        reminder.setStatus(Reminder.ReminderStatus.PENDING);
        reminder.setCreatedAt(LocalDateTime.now());
        reminder.setChannel(Reminder.NotificationChannel.ALL);
        reminder.setRetryCount(0);
        
        // Timezone awareness
        LocalDateTime baseScheduledAt = LocalDateTime.now().plusDays(daysLater);
        if (timezone != null && !timezone.isEmpty()) {
            reminder.setTimezone(timezone);
            LocalDateTime adjustedTime = timezoneService.getOptimalSendingTime(timezone, daysLater);
            reminder.setScheduledAt(adjustedTime);
        } else {
            reminder.setScheduledAt(baseScheduledAt);
        }
        
        // Personalization
        reminder.setIsPersonalized(true);
        reminder.setRecipientName(recipientName);
        reminder.setLanguage(language != null ? language : "tr"); // Default to Turkish
        String personalizedMessage = personalizationService.generatePersonalizedMessage(
            Reminder.ReminderType.LEAD_FOLLOW_UP,
            recipientName,
            null,
            null,
            "A",
            language != null ? language : "tr"
        );
        reminder.setMessage(personalizedMessage);
        
        return reminderRepository.save(reminder);
    }
    
    /**
     * Scheduled job - runs every 5 minutes to check for pending reminders
     * Includes timezone-aware sending
     */
    @Scheduled(fixedRate = 300000) // 5 minutes
    @Transactional
    public void processPendingReminders() {
        LocalDateTime now = LocalDateTime.now();
        List<Reminder> pendingReminders = reminderRepository.findPendingRemindersToSend(now);
        
        for (Reminder reminder : pendingReminders) {
            try {
                // Check if it's appropriate time in user's timezone
                if (reminder.getTimezone() != null && !reminder.getTimezone().isEmpty()) {
                    if (!timezoneService.isAppropriateTime(reminder.getTimezone())) {
                        // Reschedule for next appropriate time
                        LocalDateTime nextAppropriate = timezoneService.getOptimalSendingTime(
                            reminder.getTimezone(), 0
                        );
                        reminder.setScheduledAt(nextAppropriate);
                        reminderRepository.save(reminder);
                        continue;
                    }
                }
                
                sendReminder(reminder);
                reminder.setStatus(Reminder.ReminderStatus.SENT);
                reminder.setSentAt(LocalDateTime.now());
                reminderRepository.save(reminder);
            } catch (Exception e) {
                handleReminderFailure(reminder, e);
            }
        }
    }
    
    /**
     * Send reminder notification
     */
    private void sendReminder(Reminder reminder) {
        if (reminder.getChannel() == Reminder.NotificationChannel.EMAIL || 
            reminder.getChannel() == Reminder.NotificationChannel.ALL) {
            notificationService.sendEmailReminder(reminder);
        }
        
        if (reminder.getChannel() == Reminder.NotificationChannel.SMS || 
            reminder.getChannel() == Reminder.NotificationChannel.ALL) {
            notificationService.sendSMSReminder(reminder);
        }
        
        if (reminder.getChannel() == Reminder.NotificationChannel.PUSH || 
            reminder.getChannel() == Reminder.NotificationChannel.ALL) {
            notificationService.sendPushReminder(reminder);
        }
    }
    
    /**
     * Handle reminder failure with retry logic
     */
    private void handleReminderFailure(Reminder reminder, Exception e) {
        reminder.setRetryCount(reminder.getRetryCount() != null ? reminder.getRetryCount() + 1 : 1);
        reminder.setErrorMessage(e.getMessage());
        
        // Retry up to 3 times
        if (reminder.getRetryCount() < 3) {
            // Reschedule for 1 hour later
            reminder.setScheduledAt(LocalDateTime.now().plusHours(1));
            reminderRepository.save(reminder);
        } else {
            // Mark as failed after 3 retries
            reminder.setStatus(Reminder.ReminderStatus.FAILED);
            reminderRepository.save(reminder);
        }
    }
    
    /**
     * Cancel reminder
     */
    @Transactional
    public Reminder cancelReminder(Long reminderId) {
        Reminder reminder = reminderRepository.findById(reminderId)
            .orElseThrow(() -> new RuntimeException("Reminder not found"));
        
        if (reminder.getStatus() == Reminder.ReminderStatus.PENDING) {
            reminder.setStatus(Reminder.ReminderStatus.CANCELLED);
            return reminderRepository.save(reminder);
        }
        
        return reminder;
    }
    
    public List<Reminder> getRemindersByUser(Long userId) {
        return reminderRepository.findByUserIdOrderByScheduledAtDesc(userId);
    }
    
    public List<Reminder> getRemindersByType(Reminder.ReminderType reminderType) {
        return reminderRepository.findByReminderTypeAndStatus(reminderType, Reminder.ReminderStatus.PENDING);
    }
}
