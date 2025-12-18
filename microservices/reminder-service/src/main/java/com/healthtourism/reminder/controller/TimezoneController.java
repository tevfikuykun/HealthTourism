package com.healthtourism.reminder.controller;

import com.healthtourism.reminder.service.TimezoneService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/reminders/timezone")
@CrossOrigin(origins = "*")
public class TimezoneController {
    
    @Autowired
    private TimezoneService timezoneService;
    
    @PostMapping("/adjust")
    public ResponseEntity<LocalDateTime> adjustForTimezone(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime scheduledAt,
            @RequestParam String timezone) {
        return ResponseEntity.ok(timezoneService.adjustForTimezone(scheduledAt, timezone));
    }
    
    @GetMapping("/optimal/{timezone}")
    public ResponseEntity<LocalDateTime> getOptimalSendingTime(
            @PathVariable String timezone,
            @RequestParam(defaultValue = "2") int daysLater) {
        return ResponseEntity.ok(timezoneService.getOptimalSendingTime(timezone, daysLater));
    }
    
    @GetMapping("/appropriate/{timezone}")
    public ResponseEntity<Boolean> isAppropriateTime(@PathVariable String timezone) {
        return ResponseEntity.ok(timezoneService.isAppropriateTime(timezone));
    }
    
    @GetMapping("/from-country/{countryCode}")
    public ResponseEntity<String> getTimezoneFromCountry(@PathVariable String countryCode) {
        return ResponseEntity.ok(timezoneService.getTimezoneFromCountry(countryCode));
    }
}
