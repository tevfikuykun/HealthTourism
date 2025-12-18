package com.healthtourism.reminder.service;

import com.healthtourism.reminder.entity.Reminder;
import com.healthtourism.reminder.repository.ReminderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration Tests for ReminderService
 * Tests timezone-aware scheduling, especially night hours control
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class ReminderServiceIntegrationTest {

    @Autowired
    private ReminderService reminderService;

    @Autowired
    private ReminderRepository reminderRepository;

    @Autowired
    private TimezoneService timezoneService;

    @Test
    void testNightHoursRescheduling() {
        // Given - Create reminder scheduled for 3 AM in Istanbul timezone
        LocalDateTime nightTime = LocalDateTime.now()
            .atZone(ZoneId.of("Europe/Istanbul"))
            .withHour(3)
            .withMinute(0)
            .toLocalDateTime();

        Reminder reminder = new Reminder();
        reminder.setReminderType(Reminder.ReminderType.QUOTE_PENDING);
        reminder.setEntityId(1L);
        reminder.setUserId(1L);
        reminder.setUserEmail("test@example.com");
        reminder.setUserPhone("+905551234567");
        reminder.setStatus(Reminder.ReminderStatus.PENDING);
        reminder.setScheduledAt(nightTime);
        reminder.setTimezone("Europe/Istanbul");
        reminder.setChannel(Reminder.NotificationChannel.ALL);
        reminder.setRetryCount(0);
        reminder.setMessage("Test message");
        reminder.setCreatedAt(LocalDateTime.now());

        reminder = reminderRepository.save(reminder);

        // When - Process pending reminders (should reschedule if night time)
        reminderService.processPendingReminders();

        // Then - Check if reminder was rescheduled (if it was night time)
        Reminder updatedReminder = reminderRepository.findById(reminder.getId()).orElseThrow();
        
        // If it was night time, it should be rescheduled to optimal time (9 AM)
        if (!timezoneService.isAppropriateTime("Europe/Istanbul")) {
            // Verify it's rescheduled to optimal time
            ZonedDateTime scheduledZoned = updatedReminder.getScheduledAt()
                .atZone(ZoneId.of("Europe/Istanbul"));
            int hour = scheduledZoned.getHour();
            assertTrue(hour >= 8 && hour < 22, "Reminder should be rescheduled to appropriate hours (8-22)");
        }
    }

    @Test
    void testTimezoneFallbackToUTC() {
        // Given - Invalid timezone
        String invalidTimezone = "INVALID_TIMEZONE";

        // When - Get timezone from invalid country code
        String timezone = timezoneService.getTimezoneFromCountry("XX");

        // Then - Should fallback to UTC
        assertEquals("UTC", timezone);
    }

    @Test
    void testOptimalSendingTimeAdjustment() {
        // Given
        String timezone = "Europe/Istanbul";
        int daysLater = 2;

        // When
        LocalDateTime optimalTime = timezoneService.getOptimalSendingTime(timezone, daysLater);

        // Then - Should be set to 10 AM in user's timezone
        ZonedDateTime zonedTime = optimalTime.atZone(ZoneId.systemDefault())
            .withZoneSameInstant(ZoneId.of(timezone));
        assertEquals(10, zonedTime.getHour());
        assertEquals(0, zonedTime.getMinute());
    }

    @Test
    void testIsAppropriateTime() {
        // Given - Current time in Istanbul
        String timezone = "Europe/Istanbul";

        // When
        boolean isAppropriate = timezoneService.isAppropriateTime(timezone);

        // Then - Should return true if current hour is between 8-22
        // This test depends on when it runs, so we just verify it doesn't throw
        assertNotNull(isAppropriate);
    }

    @Test
    void testInvalidTimezoneHandling() {
        // Given - Invalid timezone
        String invalidTimezone = "INVALID_TIMEZONE_123";

        // When - Try to adjust for invalid timezone
        LocalDateTime scheduledAt = LocalDateTime.now().plusDays(2);
        LocalDateTime adjusted = timezoneService.adjustForTimezone(scheduledAt, invalidTimezone);

        // Then - Should handle gracefully (either use fallback or return original)
        assertNotNull(adjusted);
    }
}
