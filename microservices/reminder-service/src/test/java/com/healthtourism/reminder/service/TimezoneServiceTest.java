package com.healthtourism.reminder.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class TimezoneServiceTest {

    @InjectMocks
    private TimezoneService timezoneService;

    @Test
    void testAdjustForTimezone() {
        // Given
        LocalDateTime scheduledAt = LocalDateTime.now().plusDays(2);
        String timezone = "Europe/Istanbul";

        // When
        LocalDateTime adjusted = timezoneService.adjustForTimezone(scheduledAt, timezone);

        // Then
        assertNotNull(adjusted);
    }

    @Test
    void testGetOptimalSendingTime() {
        // Given
        String timezone = "Europe/Istanbul";
        int daysLater = 2;

        // When
        LocalDateTime optimal = timezoneService.getOptimalSendingTime(timezone, daysLater);

        // Then
        assertNotNull(optimal);
    }

    @Test
    void testGetTimezoneFromCountry() {
        // When
        String timezone = timezoneService.getTimezoneFromCountry("TR");

        // Then
        assertEquals("Europe/Istanbul", timezone);
    }
}
