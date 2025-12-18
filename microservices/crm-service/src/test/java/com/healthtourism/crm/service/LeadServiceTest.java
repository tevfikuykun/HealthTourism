package com.healthtourism.crm.service;

import com.healthtourism.crm.entity.Lead;
import com.healthtourism.crm.repository.LeadRepository;
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
class LeadServiceTest {

    @Mock
    private LeadRepository leadRepository;

    @InjectMocks
    private LeadService leadService;

    private Lead lead;

    @BeforeEach
    void setUp() {
        lead = new Lead();
        lead.setId(1L);
        lead.setFirstName("John");
        lead.setLastName("Doe");
        lead.setEmail("john@example.com");
        lead.setStatus(Lead.LeadStatus.NEW);
        lead.setSource(Lead.LeadSource.WEBSITE);
    }

    @Test
    void testCreateLead() {
        // Given
        when(leadRepository.save(any(Lead.class))).thenReturn(lead);

        // When
        Lead result = leadService.createLead(lead);

        // Then
        assertNotNull(result);
        assertEquals(Lead.LeadStatus.NEW, result.getStatus());
        verify(leadRepository, times(1)).save(any(Lead.class));
    }

    @Test
    void testUpdateLeadStatus() {
        // Given
        when(leadRepository.findById(1L)).thenReturn(Optional.of(lead));
        when(leadRepository.save(any(Lead.class))).thenReturn(lead);

        // When
        Lead result = leadService.updateLeadStatus(1L, Lead.LeadStatus.CONTACTED);

        // Then
        assertNotNull(result);
        assertEquals(Lead.LeadStatus.CONTACTED, result.getStatus());
        verify(leadRepository, times(1)).save(any(Lead.class));
    }

    @Test
    void testConvertToCustomer() {
        // Given
        when(leadRepository.findById(1L)).thenReturn(Optional.of(lead));
        when(leadRepository.save(any(Lead.class))).thenReturn(lead);

        // When
        Lead result = leadService.convertToCustomer(1L, 100L);

        // Then
        assertNotNull(result);
        assertEquals(Lead.LeadStatus.CONVERTED, result.getStatus());
        assertEquals(100L, result.getConvertedToUserId());
        verify(leadRepository, times(1)).save(any(Lead.class));
    }
}
