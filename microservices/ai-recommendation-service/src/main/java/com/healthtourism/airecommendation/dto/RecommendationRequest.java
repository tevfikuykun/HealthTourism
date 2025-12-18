package com.healthtourism.airecommendation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecommendationRequest {
    private Long userId;
    private String symptoms; // Belirtiler
    private String treatmentType; // Tedavi tipi
    private String preferredCity;
    private String preferredLanguage;
    private Integer budgetRange;
    private String urgency; // LOW, MEDIUM, HIGH
    private List<String> preferences; // Özel tercihler
    private Map<String, Object> medicalHistory; // Tıbbi geçmiş
}
