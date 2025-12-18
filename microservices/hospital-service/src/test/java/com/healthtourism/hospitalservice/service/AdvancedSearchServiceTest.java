package com.healthtourism.hospitalservice.service;

import com.healthtourism.hospitalservice.entity.Hospital;
import com.healthtourism.hospitalservice.repository.HospitalRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdvancedSearchServiceTest {

    @Mock
    private HospitalRepository hospitalRepository;

    @InjectMocks
    private AdvancedSearchService advancedSearchService;

    private Hospital hospital;

    @BeforeEach
    void setUp() {
        hospital = new Hospital();
        hospital.setId(1L);
        hospital.setName("Test Hospital");
        hospital.setCity("Istanbul");
        hospital.setRating(4);
        hospital.setDistanceFromAirport(15.0);
        hospital.setHasVisaSupport(true);
        hospital.setHasTranslationService(true);
    }

    @Test
    void testAdvancedSearch_WithCity() {
        // Given
        when(hospitalRepository.findAll(any())).thenReturn(Arrays.asList(hospital));

        // When
        List<Hospital> result = advancedSearchService.advancedSearch(
            "Istanbul", null, null, null, null, null, null
        );

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(hospitalRepository, times(1)).findAll(any());
    }

    @Test
    void testAdvancedSearch_WithMultipleCriteria() {
        // Given
        when(hospitalRepository.findAll(any())).thenReturn(Arrays.asList(hospital));

        // When
        List<Hospital> result = advancedSearchService.advancedSearch(
            "Istanbul", 4, 20.0, "DENTAL", null, true, true
        );

        // Then
        assertNotNull(result);
        verify(hospitalRepository, times(1)).findAll(any());
    }

    @Test
    void testSearchByMultipleCriteria() {
        // Given
        Map<String, Object> criteria = new HashMap<>();
        criteria.put("city", "Istanbul");
        criteria.put("minRating", 4);
        criteria.put("hasVisaSupport", true);
        
        when(hospitalRepository.findAll(any())).thenReturn(Arrays.asList(hospital));

        // When
        List<Hospital> result = advancedSearchService.searchByMultipleCriteria(criteria);

        // Then
        assertNotNull(result);
        verify(hospitalRepository, times(1)).findAll(any());
    }
}
