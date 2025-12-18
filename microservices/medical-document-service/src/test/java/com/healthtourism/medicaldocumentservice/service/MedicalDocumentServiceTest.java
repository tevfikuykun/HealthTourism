package com.healthtourism.medicaldocumentservice.service;

import com.healthtourism.medicaldocumentservice.dto.MedicalDocumentDTO;
import com.healthtourism.medicaldocumentservice.entity.MedicalDocument;
import com.healthtourism.medicaldocumentservice.repository.MedicalDocumentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MedicalDocumentServiceTest {

    @Mock
    private MedicalDocumentRepository medicalDocumentRepository;

    @InjectMocks
    private MedicalDocumentService medicalDocumentService;

    private MedicalDocument document;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(medicalDocumentService, "uploadDir", "./test-uploads");
        
        document = new MedicalDocument();
        document.setId("1");
        document.setUserId(1L);
        document.setFileName("test.pdf");
        document.setFilePath("./test-uploads/test.pdf");
        document.setDocumentType("REPORT");
        document.setIsActive(true);
        document.setCreatedAt(LocalDateTime.now());
    }

    @Test
    void testUploadDocument() throws IOException {
        // Given
        MultipartFile file = new MockMultipartFile(
            "file", "test.pdf", "application/pdf", "test content".getBytes()
        );
        
        when(medicalDocumentRepository.save(any(MedicalDocument.class))).thenReturn(document);

        // When
        MedicalDocumentDTO result = medicalDocumentService.uploadDocument(
            file, 1L, 1L, "REPORT", "Test document"
        );

        // Then
        assertNotNull(result);
        assertEquals("test.pdf", result.getFileName());
        verify(medicalDocumentRepository, times(1)).save(any(MedicalDocument.class));
    }

    @Test
    void testGetDocumentsByUser() {
        // Given
        when(medicalDocumentRepository.findByUserIdAndIsActiveTrueOrderByUploadedAtDesc(1L))
            .thenReturn(Arrays.asList(document));

        // When
        List<MedicalDocumentDTO> result = medicalDocumentService.getDocumentsByUser(1L);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(medicalDocumentRepository, times(1)).findByUserIdAndIsActiveTrueOrderByUploadedAtDesc(1L);
    }

    @Test
    void testGetDocumentById_Success() {
        // Given
        when(medicalDocumentRepository.findById("1")).thenReturn(Optional.of(document));

        // When
        MedicalDocumentDTO result = medicalDocumentService.getDocumentById("1");

        // Then
        assertNotNull(result);
        assertEquals("1", result.getId());
    }

    @Test
    void testGetDocumentById_NotFound() {
        // Given
        when(medicalDocumentRepository.findById("1")).thenReturn(Optional.empty());

        // When & Then
        assertThrows(RuntimeException.class, () -> {
            medicalDocumentService.getDocumentById("1");
        });
    }
}
