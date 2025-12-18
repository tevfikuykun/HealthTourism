package com.healthtourism.reminder.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class TimezoneServiceFallbackTest {

    @InjectMocks
    private TimezoneService timezoneService;

    @Test
    void testGetSafeFallbackTimezone() {
        // When
        String fallback = timezoneService.getSafeFallbackTimezone();

        // Then
        assertEquals("UTC", fallback);
    }

    @Test
    void testInvalidCountryCodeFallback() {
        // Given - Invalid country code
        String invalidCountryCode = "XX";

        // When
        String timezone = timezoneService.getTimezoneFromCountry(invalidCountryCode);

        // Then - Should fallback to UTC
        assertEquals("UTC", timezone);
    }

    @Test
    void testNullCountryCodeFallback() {
        // Given - Null country code
        String nullCountryCode = null;

        // When
        String timezone = timezoneService.getTimezoneFromCountry(nullCountryCode);

        // Then - Should fallback to UTC
        assertEquals("UTC", timezone);
    }

    @Test
    void testEmptyCountryCodeFallback() {
        // Given - Empty country code
        String emptyCountryCode = "";

        // When
        String timezone = timezoneService.getTimezoneFromCountry(emptyCountryCode);

        // Then - Should fallback to UTC
        assertEquals("UTC", timezone);
    }

    @Test
    void testInvalidTimezoneAdjustment() {
        // Given - Invalid timezone
        String invalidTimezone = "INVALID_TIMEZONE_123";
        LocalDateTime scheduledAt = LocalDateTime.now().plusDays(2);

        // When
        LocalDateTime adjusted = timezoneService.adjustForTimezone(scheduledAt, invalidTimezone);

        // Then - Should handle gracefully (return original or use UTC)
        assertNotNull(adjusted);
    }

    @Test
    void testNullTimezoneAdjustment() {
        // Given - Null timezone
        String nullTimezone = null;
        LocalDateTime scheduledAt = LocalDateTime.now().plusDays(2);

        // When
        LocalDateTime adjusted = timezoneService.adjustForTimezone(scheduledAt, nullTimezone);

        // Then - Should use UTC fallback
        assertNotNull(adjusted);
    }
}
