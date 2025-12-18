package com.healthtourism.reminder.service;

import com.healthtourism.reminder.entity.Reminder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class MessagePersonalizationServiceLanguageTest {

    @InjectMocks
    private MessagePersonalizationService personalizationService;

    @Test
    void testTurkishMessage() {
        // Given
        String language = "tr";
        String recipientName = "Ahmet Yılmaz";
        String treatmentType = "İmplant";

        // When
        String message = personalizationService.generatePersonalizedMessage(
            Reminder.ReminderType.QUOTE_PENDING,
            recipientName,
            treatmentType,
            null,
            "A",
            language
        );

        // Then
        assertNotNull(message);
        assertTrue(message.contains("Ahmet"));
        assertTrue(message.contains("İmplant"));
    }

    @Test
    void testEnglishMessage() {
        // Given
        String language = "en";
        String recipientName = "John Smith";
        String treatmentType = "Implant";

        // When
        String message = personalizationService.generatePersonalizedMessage(
            Reminder.ReminderType.QUOTE_PENDING,
            recipientName,
            treatmentType,
            null,
            "A",
            language
        );

        // Then
        assertNotNull(message);
        assertTrue(message.contains("John"));
        assertTrue(message.contains("Implant"));
    }

    @Test
    void testArabicMessage() {
        // Given
        String language = "ar";
        String recipientName = "أحمد يلماز";
        String treatmentType = "زراعة";

        // When
        String message = personalizationService.generatePersonalizedMessage(
            Reminder.ReminderType.QUOTE_PENDING,
            recipientName,
            treatmentType,
            null,
            "A",
            language
        );

        // Then
        assertNotNull(message);
        assertTrue(message.contains("أحمد"));
    }

    @Test
    void testGermanMessage() {
        // Given
        String language = "de";
        String recipientName = "Hans Müller";
        String treatmentType = "Implantat";

        // When
        String message = personalizationService.generatePersonalizedMessage(
            Reminder.ReminderType.QUOTE_PENDING,
            recipientName,
            treatmentType,
            null,
            "A",
            language
        );

        // Then
        assertNotNull(message);
        assertTrue(message.contains("Hans"));
    }

    @Test
    void testInvalidLanguageFallback() {
        // Given - Invalid language
        String language = "invalid_lang";

        // When
        String message = personalizationService.generatePersonalizedMessage(
            Reminder.ReminderType.QUOTE_PENDING,
            "Test User",
            "Treatment",
            null,
            "A",
            language
        );

        // Then - Should fallback to default language (Turkish)
        assertNotNull(message);
    }
}
