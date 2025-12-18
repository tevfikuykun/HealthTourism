package com.healthtourism.reminder.service;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Timezone Service
 * Manages timezone-aware scheduling for reminders
 */
@Service
public class TimezoneService {
    
    /**
     * Adjust scheduled time based on user's timezone
     * Ensures reminders are sent at appropriate hours (not during night)
     */
    public LocalDateTime adjustForTimezone(LocalDateTime scheduledAt, String timezone) {
        if (timezone == null || timezone.isEmpty()) {
            timezone = "UTC"; // Default to UTC
        }
        
        try {
            ZoneId zoneId = ZoneId.of(timezone);
            ZonedDateTime zonedDateTime = scheduledAt.atZone(ZoneId.systemDefault())
                .withZoneSameInstant(zoneId);
            
            int hour = zonedDateTime.getHour();
            
            // Avoid sending reminders between 22:00 and 08:00 in user's timezone
            if (hour >= 22 || hour < 8) {
                // Adjust to 09:00 in user's timezone
                ZonedDateTime adjusted = zonedDateTime.withHour(9).withMinute(0).withSecond(0);
                return adjusted.withZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime();
            }
            
            return zonedDateTime.withZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime();
        } catch (Exception e) {
            // If timezone is invalid, return original time
            return scheduledAt;
        }
    }
    
    /**
     * Get optimal sending time for user's timezone
     * Best times: 09:00-12:00 and 14:00-18:00
     */
    public LocalDateTime getOptimalSendingTime(String timezone, int daysLater) {
        // Fallback to safe zone if timezone is null or empty
        if (timezone == null || timezone.isEmpty()) {
            timezone = getSafeFallbackTimezone();
        }
        
        // Validate timezone, fallback to UTC if invalid
        if (!isValidTimezone(timezone)) {
            timezone = getSafeFallbackTimezone();
        }
        
        try {
            ZoneId zoneId = ZoneId.of(timezone);
            ZonedDateTime now = ZonedDateTime.now(zoneId);
            ZonedDateTime targetDate = now.plusDays(daysLater);
            
            // Set to 10:00 AM in user's timezone (optimal time)
            ZonedDateTime optimalTime = targetDate.withHour(10).withMinute(0).withSecond(0);
            
            // Convert back to system timezone
            return optimalTime.withZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime();
        } catch (Exception e) {
            // Fallback: use safe zone (UTC) with optimal time
            return LocalDateTime.now(ZoneId.of(getSafeFallbackTimezone())).plusDays(daysLater).withHour(10).withMinute(0);
        }
    }
    
    /**
     * Check if current time is appropriate for sending in user's timezone
     */
    public boolean isAppropriateTime(String timezone) {
        // Fallback to safe zone if timezone is null or empty
        if (timezone == null || timezone.isEmpty()) {
            timezone = getSafeFallbackTimezone();
        }
        
        // Validate timezone, fallback to UTC if invalid
        if (!isValidTimezone(timezone)) {
            timezone = getSafeFallbackTimezone();
        }
        
        try {
            ZoneId zoneId = ZoneId.of(timezone);
            ZonedDateTime now = ZonedDateTime.now(zoneId);
            int hour = now.getHour();
            
            // Don't send between 22:00 and 08:00
            return hour >= 8 && hour < 22;
        } catch (Exception e) {
            // If timezone is invalid, use safe fallback and allow sending (safe default)
            return true;
        }
    }
    
    /**
     * Get user's timezone from country code
     * Falls back to UTC (Safe Zone) if country code not found or invalid
     */
    public String getTimezoneFromCountry(String countryCode) {
        if (countryCode == null || countryCode.isEmpty()) {
            return getSafeFallbackTimezone();
        }
        
        Map<String, String> countryTimezones = new HashMap<>();
        countryTimezones.put("TR", "Europe/Istanbul");
        countryTimezones.put("US", "America/New_York");
        countryTimezones.put("GB", "Europe/London");
        countryTimezones.put("DE", "Europe/Berlin");
        countryTimezones.put("FR", "Europe/Paris");
        countryTimezones.put("IT", "Europe/Rome");
        countryTimezones.put("ES", "Europe/Madrid");
        countryTimezones.put("NL", "Europe/Amsterdam");
        countryTimezones.put("BE", "Europe/Brussels");
        countryTimezones.put("CH", "Europe/Zurich");
        countryTimezones.put("AT", "Europe/Vienna");
        countryTimezones.put("SE", "Europe/Stockholm");
        countryTimezones.put("NO", "Europe/Oslo");
        countryTimezones.put("DK", "Europe/Copenhagen");
        countryTimezones.put("FI", "Europe/Helsinki");
        countryTimezones.put("PL", "Europe/Warsaw");
        countryTimezones.put("CZ", "Europe/Prague");
        countryTimezones.put("GR", "Europe/Athens");
        countryTimezones.put("PT", "Europe/Lisbon");
        countryTimezones.put("IE", "Europe/Dublin");
        countryTimezones.put("AU", "Australia/Sydney");
        countryTimezones.put("CA", "America/Toronto");
        countryTimezones.put("MX", "America/Mexico_City");
        countryTimezones.put("BR", "America/Sao_Paulo");
        countryTimezones.put("AR", "America/Buenos_Aires");
        countryTimezones.put("JP", "Asia/Tokyo");
        countryTimezones.put("CN", "Asia/Shanghai");
        countryTimezones.put("IN", "Asia/Kolkata");
        countryTimezones.put("AE", "Asia/Dubai");
        countryTimezones.put("SA", "Asia/Riyadh");
        countryTimezones.put("ZA", "Africa/Johannesburg");
        countryTimezones.put("EG", "Africa/Cairo");
        
        String timezone = countryTimezones.get(countryCode.toUpperCase());
        
        // Validate timezone before returning
        if (timezone != null && isValidTimezone(timezone)) {
            return timezone;
        }
        
        // Fallback to safe zone (UTC)
        return getSafeFallbackTimezone();
    }
    
    /**
     * Get safe fallback timezone (UTC)
     * This is the default "Safe Zone" when timezone cannot be determined
     */
    public String getSafeFallbackTimezone() {
        return "UTC";
    }
    
    /**
     * Validate if timezone is valid
     */
    private boolean isValidTimezone(String timezone) {
        try {
            ZoneId.of(timezone);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
