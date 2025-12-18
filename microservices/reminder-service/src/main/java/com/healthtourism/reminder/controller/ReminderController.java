package com.healthtourism.reminder.controller;

import com.healthtourism.reminder.entity.Reminder;
import com.healthtourism.reminder.service.ReminderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/reminders")
@CrossOrigin(origins = "*")
public class ReminderController {
    
    @Autowired
    private ReminderService reminderService;
    
    @PostMapping("/quote/{quoteId}")
    public ResponseEntity<Reminder> createQuoteReminder(
            @PathVariable Long quoteId,
            @RequestParam Long userId,
            @RequestParam String userEmail,
            @RequestParam String userPhone,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime quoteSentAt,
            @RequestParam(required = false) String timezone,
            @RequestParam(required = false) String recipientName,
            @RequestParam(required = false) String treatmentType,
            @RequestParam(required = false) String quoteNumber,
            @RequestParam(required = false, defaultValue = "tr") String language) {
        return ResponseEntity.ok(reminderService.createQuoteReminder(
            quoteId, userId, userEmail, userPhone, quoteSentAt, 
            timezone, recipientName, treatmentType, quoteNumber, language
        ));
    }
    
    @PostMapping("/quote-expiring/{quoteId}")
    public ResponseEntity<Reminder> createQuoteExpiringReminder(
            @PathVariable Long quoteId,
            @RequestParam Long userId,
            @RequestParam String userEmail,
            @RequestParam String userPhone,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime validUntil,
            @RequestParam(required = false) String timezone,
            @RequestParam(required = false) String recipientName,
            @RequestParam(required = false) String treatmentType,
            @RequestParam(required = false) String quoteNumber,
            @RequestParam(required = false, defaultValue = "tr") String language) {
        return ResponseEntity.ok(reminderService.createQuoteExpiringReminder(
            quoteId, userId, userEmail, userPhone, validUntil,
            timezone, recipientName, treatmentType, quoteNumber, language
        ));
    }
    
    @PostMapping("/lead/{leadId}")
    public ResponseEntity<Reminder> createLeadFollowUpReminder(
            @PathVariable Long leadId,
            @RequestParam Long userId,
            @RequestParam String userEmail,
            @RequestParam String userPhone,
            @RequestParam(defaultValue = "3") int daysLater,
            @RequestParam(required = false) String timezone,
            @RequestParam(required = false) String recipientName,
            @RequestParam(required = false, defaultValue = "tr") String language) {
        return ResponseEntity.ok(reminderService.createLeadFollowUpReminder(
            leadId, userId, userEmail, userPhone, daysLater, timezone, recipientName, language
        ));
    }
    
    @PostMapping("/cancel/{reminderId}")
    public ResponseEntity<Reminder> cancelReminder(@PathVariable Long reminderId) {
        try {
            return ResponseEntity.ok(reminderService.cancelReminder(reminderId));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Reminder>> getRemindersByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(reminderService.getRemindersByUser(userId));
    }
    
    @GetMapping("/type/{reminderType}")
    public ResponseEntity<List<Reminder>> getRemindersByType(@PathVariable Reminder.ReminderType reminderType) {
        return ResponseEntity.ok(reminderService.getRemindersByType(reminderType));
    }
}
