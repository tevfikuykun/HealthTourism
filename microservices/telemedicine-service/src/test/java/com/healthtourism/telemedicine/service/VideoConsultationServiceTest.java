package com.healthtourism.telemedicine.service;

import com.healthtourism.telemedicine.entity.VideoConsultation;
import com.healthtourism.telemedicine.repository.VideoConsultationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VideoConsultationServiceTest {

    @Mock
    private VideoConsultationRepository consultationRepository;

    @InjectMocks
    private VideoConsultationService consultationService;

    @Test
    void testScheduleConsultation() {
        // Given
        VideoConsultation consultation = new VideoConsultation();
        consultation.setId(1L);
        when(consultationRepository.save(any(VideoConsultation.class))).thenReturn(consultation);

        // When
        VideoConsultation result = consultationService.scheduleConsultation(
            1L, 2L, LocalDateTime.now().plusDays(1)
        );

        // Then
        assertNotNull(result);
        assertNotNull(result.getConsultationRoomId());
        assertEquals(VideoConsultation.ConsultationStatus.SCHEDULED, result.getStatus());
        verify(consultationRepository, times(1)).save(any(VideoConsultation.class));
    }

    @Test
    void testStartConsultation() {
        // Given
        VideoConsultation consultation = new VideoConsultation();
        consultation.setStatus(VideoConsultation.ConsultationStatus.SCHEDULED);
        when(consultationRepository.findByConsultationRoomId("room-123")).thenReturn(Optional.of(consultation));
        when(consultationRepository.save(any(VideoConsultation.class))).thenReturn(consultation);

        // When
        VideoConsultation result = consultationService.startConsultation("room-123");

        // Then
        assertNotNull(result);
        assertEquals(VideoConsultation.ConsultationStatus.IN_PROGRESS, result.getStatus());
        verify(consultationRepository, times(1)).save(any(VideoConsultation.class));
    }
}
