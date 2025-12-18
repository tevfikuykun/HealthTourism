package com.healthtourism.audit.service;

import com.healthtourism.audit.entity.AuditLog;
import com.healthtourism.audit.repository.AuditLogRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuditServiceTest {

    @Mock
    private AuditLogRepository auditLogRepository;

    @InjectMocks
    private AuditService auditService;

    @Test
    void testLogAccess() {
        // Given
        AuditLog auditLog = new AuditLog();
        auditLog.setId(1L);
        when(auditLogRepository.save(any(AuditLog.class))).thenReturn(auditLog);

        // When
        AuditLog result = auditService.logAccess(
            1L, "user@example.com", "PATIENT",
            AuditLog.ResourceType.MEDICAL_DOCUMENT, "doc123",
            AuditLog.Action.READ, null, "Document accessed", true, null
        );

        // Then
        assertNotNull(result);
        verify(auditLogRepository, times(1)).save(any(AuditLog.class));
    }
}
