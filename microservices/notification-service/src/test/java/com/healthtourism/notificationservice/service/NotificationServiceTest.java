package com.healthtourism.notificationservice.service;

import com.healthtourism.notificationservice.dto.NotificationDTO;
import com.healthtourism.notificationservice.dto.NotificationRequestDTO;
import com.healthtourism.notificationservice.entity.Notification;
import com.healthtourism.notificationservice.repository.NotificationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private EmailTemplateService emailTemplateService;

    @Mock
    private SMSService smsService;

    @InjectMocks
    private NotificationService notificationService;

    private NotificationRequestDTO request;
    private Notification notification;

    @BeforeEach
    void setUp() {
        request = new NotificationRequestDTO();
        request.setUserId(1L);
        request.setType("EMAIL");
        request.setCategory("RESERVATION");
        request.setSubject("Test Subject");
        request.setMessage("Test Message");
        request.setRecipient("test@example.com");

        notification = new Notification();
        notification.setId(1L);
        notification.setUserId(1L);
        notification.setType("EMAIL");
        notification.setStatus("SENT");
        notification.setCreatedAt(LocalDateTime.now());
    }

    @Test
    void testSendNotification_Email() {
        // Given
        when(notificationRepository.save(any(Notification.class))).thenReturn(notification);

        // When
        NotificationDTO result = notificationService.sendNotification(request);

        // Then
        assertNotNull(result);
        verify(notificationRepository, times(1)).save(any(Notification.class));
    }

    @Test
    void testSendNotification_SMS() {
        // Given
        request.setType("SMS");
        request.setRecipient("+1234567890");
        when(notificationRepository.save(any(Notification.class))).thenReturn(notification);

        // When
        NotificationDTO result = notificationService.sendNotification(request);

        // Then
        assertNotNull(result);
        verify(smsService, times(1)).sendNotificationSMS(anyString(), anyString());
    }

    @Test
    void testGetNotificationsByUser() {
        // Given
        when(notificationRepository.findByUserIdOrderByCreatedAtDesc(1L))
            .thenReturn(Arrays.asList(notification));

        // When
        List<NotificationDTO> result = notificationService.getNotificationsByUser(1L);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(notificationRepository, times(1)).findByUserIdOrderByCreatedAtDesc(1L);
    }

    @Test
    void testSendReservationCreatedNotification() {
        // Given
        Map<String, Object> event = new HashMap<>();
        event.put("reservationId", 1L);
        when(notificationRepository.save(any(Notification.class))).thenReturn(notification);

        // When
        notificationService.sendReservationCreatedNotification(1L, event);

        // Then
        verify(notificationRepository, times(1)).save(any(Notification.class));
    }
}
