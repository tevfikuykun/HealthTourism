package com.healthtourism.reminder.service;

import com.healthtourism.reminder.entity.Reminder;
import com.healthtourism.reminder.repository.ReminderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ABTestingServiceTest {

    @Mock
    private ReminderRepository reminderRepository;

    @InjectMocks
    private ABTestingService abTestingService;

    @Test
    void testAssignVariant() {
        // Given
        when(reminderRepository.findByReminderTypeAndStatus(
            Reminder.ReminderType.QUOTE_PENDING, Reminder.ReminderStatus.SENT
        )).thenReturn(new ArrayList<>());

        // When
        String variant = abTestingService.assignVariant(Reminder.ReminderType.QUOTE_PENDING);

        // Then
        assertNotNull(variant);
        assertTrue(variant.equals("A") || variant.equals("B"));
    }

    @Test
    void testGetABTestStatistics() {
        // Given
        List<Reminder> reminders = new ArrayList<>();
        Reminder reminder1 = new Reminder();
        reminder1.setAbTestVariant("A");
        reminder1.setStatus(Reminder.ReminderStatus.SENT);
        reminder1.setNotes("Response tracked: clicked");
        reminders.add(reminder1);

        Reminder reminder2 = new Reminder();
        reminder2.setAbTestVariant("B");
        reminder2.setStatus(Reminder.ReminderStatus.SENT);
        reminders.add(reminder2);

        when(reminderRepository.findByReminderTypeAndStatus(
            Reminder.ReminderType.QUOTE_PENDING, Reminder.ReminderStatus.SENT
        )).thenReturn(reminders);

        // When
        Map<String, Object> stats = abTestingService.getABTestStatistics(Reminder.ReminderType.QUOTE_PENDING);

        // Then
        assertNotNull(stats);
        assertTrue(stats.containsKey("variantCounts"));
        assertTrue(stats.containsKey("conversionRates"));
    }
}
