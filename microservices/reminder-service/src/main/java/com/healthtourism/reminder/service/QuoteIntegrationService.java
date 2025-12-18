package com.healthtourism.reminder.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Quote Integration Service
 * Automatically creates reminders when quotes are sent
 */
@Service
public class QuoteIntegrationService {
    
    @Autowired
    private ReminderService reminderService;
    
    @Value("${quote.service.url:http://localhost:8035}")
    private String quoteServiceUrl;
    
    private final RestTemplate restTemplate = new RestTemplate();
    
    /**
     * Check for new quotes and create reminders
     * Runs every hour
     */
    @Scheduled(fixedRate = 3600000) // 1 hour
    @Transactional
    public void checkAndCreateQuoteReminders() {
        try {
            // Get all SENT quotes from quote service
            List<Map<String, Object>> sentQuotes = getSentQuotes();
            
            for (Map<String, Object> quote : sentQuotes) {
                Long quoteId = Long.parseLong(quote.get("id").toString());
                Long userId = Long.parseLong(quote.get("userId").toString());
                
                // Check if reminder already exists
                if (!reminderExists(Reminder.ReminderType.QUOTE_PENDING, quoteId)) {
                    // Get user details (in production, call user service)
                    String userEmail = quote.get("userEmail") != null ? quote.get("userEmail").toString() : "user@example.com";
                    String userPhone = quote.get("userPhone") != null ? quote.get("userPhone").toString() : "+905551234567";
                    
                    // Parse sent date
                    LocalDateTime sentAt = LocalDateTime.parse(quote.get("updatedAt").toString());
                    
                    // Create reminder for 2 days later
                    reminderService.createQuoteReminder(quoteId, userId, userEmail, userPhone, sentAt);
                    
                    // Also create expiring reminder
                    LocalDateTime validUntil = LocalDateTime.parse(quote.get("validUntil").toString());
                    reminderService.createQuoteExpiringReminder(quoteId, userId, userEmail, userPhone, validUntil);
                }
            }
        } catch (Exception e) {
            // Log error but don't fail
            System.err.println("Error checking quotes: " + e.getMessage());
        }
    }
    
    private List<Map<String, Object>> getSentQuotes() {
        try {
            // In production, call actual quote service
            // For now, return empty list (simulation)
            return List.of();
        } catch (Exception e) {
            return List.of();
        }
    }
    
    private boolean reminderExists(Reminder.ReminderType reminderType, Long entityId) {
        // Check if reminder already exists
        return false; // Simplified
    }
}
