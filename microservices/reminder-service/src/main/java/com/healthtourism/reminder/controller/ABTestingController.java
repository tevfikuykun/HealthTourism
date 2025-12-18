package com.healthtourism.reminder.controller;

import com.healthtourism.reminder.entity.Reminder;
import com.healthtourism.reminder.service.ABTestingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/reminders/ab-testing")
@CrossOrigin(origins = "*")
public class ABTestingController {
    
    @Autowired
    private ABTestingService abTestingService;
    
    @PostMapping("/track-response/{reminderId}")
    public ResponseEntity<Void> trackResponse(
            @PathVariable Long reminderId,
            @RequestParam String action) {
        abTestingService.trackResponse(reminderId, action);
        return ResponseEntity.ok().build();
    }
    
    @GetMapping("/statistics/{reminderType}")
    public ResponseEntity<Map<String, Object>> getABTestStatistics(
            @PathVariable Reminder.ReminderType reminderType) {
        return ResponseEntity.ok(abTestingService.getABTestStatistics(reminderType));
    }
}
