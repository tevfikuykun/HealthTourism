package com.healthtourism.audit.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ArchiveRetrievalServiceTest {

    @Mock
    private com.healthtourism.audit.repository.AuditLogRepository auditLogRepository;

    @InjectMocks
    private ArchiveRetrievalService retrievalService;

    @Test
    void testRequestRetrieval() {
        // Given
        String archiveId = "ARCHIVE-2025-01-01";
        LocalDateTime startDate = LocalDateTime.now().minusDays(30);
        LocalDateTime endDate = LocalDateTime.now();

        // When
        String jobId = retrievalService.requestRetrieval(archiveId, startDate, endDate);

        // Then
        assertNotNull(jobId);
        assertTrue(jobId.startsWith("RETRIEVE-"));
    }

    @Test
    void testGetRetrievalEstimate() {
        // Given
        String archiveId = "ARCHIVE-2025-01-01";
        LocalDateTime startDate = LocalDateTime.now().minusDays(30);
        LocalDateTime endDate = LocalDateTime.now();

        // When
        Map<String, Object> estimate = retrievalService.getRetrievalEstimate(archiveId, startDate, endDate);

        // Then
        assertNotNull(estimate);
        assertTrue(estimate.containsKey("estimatedTimeHours"));
        assertTrue(estimate.containsKey("estimatedCost"));
        assertTrue(estimate.containsKey("retrievalTier"));
    }
}
