package com.healthtourism.reminder.service;

import com.healthtourism.reminder.entity.Reminder;
import com.healthtourism.reminder.repository.ReminderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A/B Testing Service
 * Tracks which message variants perform better
 */
@Service
public class ABTestingService {
    
    @Autowired
    private ReminderRepository reminderRepository;
    
    /**
     * Assign A/B test variant to reminder
     */
    public String assignVariant(Reminder.ReminderType reminderType) {
        // Simple round-robin or random assignment
        // In production, use more sophisticated algorithms
        List<Reminder> recentReminders = reminderRepository.findByReminderTypeAndStatus(
            reminderType, Reminder.ReminderStatus.SENT
        );
        
        int variantCount = recentReminders.size() % 2;
        return variantCount == 0 ? "A" : "B";
    }
    
    /**
     * Track reminder response (when user clicks or responds)
     */
    @Transactional
    public void trackResponse(Long reminderId, String action) {
        Reminder reminder = reminderRepository.findById(reminderId)
            .orElseThrow(() -> new RuntimeException("Reminder not found"));
        
        // In production, store response tracking in separate table
        // For now, we'll use reminder notes
        String trackingNote = String.format("Response tracked: %s at %s", action, LocalDateTime.now());
        reminder.setNotes(reminder.getNotes() != null ? 
            reminder.getNotes() + "\n" + trackingNote : trackingNote);
        reminderRepository.save(reminder);
    }
    
    /**
     * Get A/B test statistics
     */
    public Map<String, Object> getABTestStatistics(Reminder.ReminderType reminderType) {
        List<Reminder> reminders = reminderRepository.findByReminderTypeAndStatus(
            reminderType, Reminder.ReminderStatus.SENT
        );
        
        Map<String, Integer> variantCounts = new HashMap<>();
        Map<String, Integer> variantResponses = new HashMap<>();
        
        for (Reminder reminder : reminders) {
            String variant = reminder.getAbTestVariant() != null ? reminder.getAbTestVariant() : "A";
            variantCounts.put(variant, variantCounts.getOrDefault(variant, 0) + 1);
            
            // Check if reminder has response (in production, use separate tracking)
            if (reminder.getNotes() != null && reminder.getNotes().contains("Response tracked")) {
                variantResponses.put(variant, variantResponses.getOrDefault(variant, 0) + 1);
            }
        }
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("variantCounts", variantCounts);
        stats.put("variantResponses", variantResponses);
        
        // Calculate conversion rates
        Map<String, Double> conversionRates = new HashMap<>();
        for (String variant : variantCounts.keySet()) {
            int sent = variantCounts.get(variant);
            int responded = variantResponses.getOrDefault(variant, 0);
            double rate = sent > 0 ? (double) responded / sent * 100 : 0.0;
            conversionRates.put(variant, rate);
        }
        stats.put("conversionRates", conversionRates);
        
        return stats;
    }
}
