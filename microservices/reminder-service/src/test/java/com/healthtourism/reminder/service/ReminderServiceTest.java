package com.healthtourism.reminder.service;

import com.healthtourism.reminder.entity.Reminder;
import com.healthtourism.reminder.repository.ReminderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReminderServiceTest {

    @Mock
    private ReminderRepository reminderRepository;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private ReminderService reminderService;

    @Test
    void testCreateQuoteReminder() {
        // Given
        Reminder reminder = new Reminder();
        reminder.setId(1L);
        when(reminderRepository.save(any(Reminder.class))).thenReturn(reminder);

        // When
        Reminder result = reminderService.createQuoteReminder(
            1L, 100L, "user@example.com", "+905551234567", LocalDateTime.now()
        );

        // Then
        assertNotNull(result);
        assertEquals(Reminder.ReminderType.QUOTE_PENDING, result.getReminderType());
        assertEquals(Reminder.ReminderStatus.PENDING, result.getStatus());
        assertEquals(Reminder.NotificationChannel.ALL, result.getChannel());
        verify(reminderRepository, times(1)).save(any(Reminder.class));
    }

    @Test
    void testCreateQuoteExpiringReminder() {
        // Given
        Reminder reminder = new Reminder();
        reminder.setId(1L);
        when(reminderRepository.save(any(Reminder.class))).thenReturn(reminder);

        // When
        Reminder result = reminderService.createQuoteExpiringReminder(
            1L, 100L, "user@example.com", "+905551234567", LocalDateTime.now().plusDays(30)
        );

        // Then
        assertNotNull(result);
        assertEquals(Reminder.ReminderType.QUOTE_EXPIRING, result.getReminderType());
        verify(reminderRepository, times(1)).save(any(Reminder.class));
    }
}
