package com.healthtourism.reminder.service;

import com.healthtourism.reminder.entity.Reminder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class MessagePersonalizationServiceTest {

    @InjectMocks
    private MessagePersonalizationService personalizationService;

    @Test
    void testGeneratePersonalizedMessage() {
        // Given
        String recipientName = "Ahmet Yılmaz";
        String treatmentType = "İmplant";
        String quoteNumber = "QUOTE-123456";

        // When
        String message = personalizationService.generatePersonalizedMessage(
            Reminder.ReminderType.QUOTE_PENDING,
            recipientName,
            treatmentType,
            quoteNumber,
            "A"
        );

        // Then
        assertNotNull(message);
        assertTrue(message.contains(recipientName));
        assertTrue(message.contains(treatmentType));
    }

    @Test
    void testGenerateEmailSubject() {
        // Given
        String recipientName = "Ahmet Yılmaz";
        String treatmentType = "İmplant";

        // When
        String subject = personalizationService.generateEmailSubject(
            Reminder.ReminderType.QUOTE_PENDING,
            recipientName,
            treatmentType
        );

        // Then
        assertNotNull(subject);
        assertTrue(subject.contains("Ahmet"));
    }
}
