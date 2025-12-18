package com.healthtourism.airecommendation.service;

import com.healthtourism.airecommendation.dto.RecommendationRequest;
import com.healthtourism.airecommendation.dto.RecommendationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class RecommendationServiceTest {

    @InjectMocks
    private RecommendationService recommendationService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(recommendationService, "hospitalServiceUrl", "http://localhost:8002");
        ReflectionTestUtils.setField(recommendationService, "doctorServiceUrl", "http://localhost:8003");
        ReflectionTestUtils.setField(recommendationService, "packageServiceUrl", "http://localhost:8006");
    }

    @Test
    void testGetRecommendations() {
        // Given
        RecommendationRequest request = new RecommendationRequest();
        request.setSymptoms("Diş ağrısı");
        request.setTreatmentType("DENTAL");
        request.setPreferredCity("Istanbul");
        request.setBudgetRange(10000);

        // When
        RecommendationResponse response = recommendationService.getRecommendations(request);

        // Then
        assertNotNull(response);
        assertNotNull(response.getTopDoctors());
        assertNotNull(response.getTopPackages());
        assertEquals(3, response.getTopDoctors().size());
        assertEquals(3, response.getTopPackages().size());
        assertNotNull(response.getReasoning());
        assertTrue(response.getConfidenceScore() >= 0 && response.getConfidenceScore() <= 1);
    }
}
