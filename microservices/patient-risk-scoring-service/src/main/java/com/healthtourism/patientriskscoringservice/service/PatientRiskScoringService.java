package com.healthtourism.patientriskscoringservice.service;

import com.healthtourism.patientriskscoringservice.entity.PatientRiskScore;
import com.healthtourism.patientriskscoringservice.repository.PatientRiskScoreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Patient Risk Scoring Service
 * AI-driven recovery score calculation combining IoT data, medical history, and procedure complexity
 */
@Service
public class PatientRiskScoringService {
    
    @Autowired
    private PatientRiskScoreRepository riskScoreRepository;
    
    @Autowired
    private AlertService alertService;
    
    @Autowired(required = false)
    private RestTemplate restTemplate;
    
    @Value("${iot.monitoring.service.url:http://localhost:8032}")
    private String iotMonitoringServiceUrl;
    
    @Value("${medical.document.service.url:http://localhost:8012}")
    private String medicalDocumentServiceUrl;
    
    @Value("${reservation.service.url:http://localhost:8009}")
    private String reservationServiceUrl;
    
    private RestTemplate getRestTemplate() {
        if (restTemplate == null) {
            restTemplate = new RestTemplate();
        }
        return restTemplate;
    }
    
    /**
     * Calculate recovery score for a patient
     */
    @Transactional
    public PatientRiskScore calculateRecoveryScore(Long userId, Long reservationId) {
        // Retrieve IoT data
        BigDecimal iotDataScore = calculateIoTDataScore(userId, reservationId);
        
        // Retrieve medical history
        BigDecimal medicalHistoryScore = calculateMedicalHistoryScore(userId);
        
        // Get procedure complexity
        BigDecimal procedureComplexityScore = calculateProcedureComplexityScore(reservationId);
        
        // Calculate compliance score
        BigDecimal complianceScore = calculateComplianceScore(userId, reservationId);
        
        // Calculate overall recovery score (weighted average)
        BigDecimal recoveryScore = calculateWeightedScore(
                iotDataScore, medicalHistoryScore, procedureComplexityScore, complianceScore);
        
        // Determine score category
        String scoreCategory = determineScoreCategory(recoveryScore);
        
        // Get previous score for trend analysis
        PatientRiskScore previousScore = getLatestScore(userId, reservationId);
        BigDecimal previousScoreValue = previousScore != null ? previousScore.getRecoveryScore() : recoveryScore;
        BigDecimal scoreChange = recoveryScore.subtract(previousScoreValue);
        String trend = determineTrend(scoreChange);
        
        // Check if alert is needed
        boolean requiresAlert = shouldAlertDoctor(recoveryScore, previousScoreValue, scoreChange);
        
        // Generate AI explanation for score change
        String scoreExplanation = generateScoreExplanation(
                recoveryScore, previousScoreValue, scoreChange, 
                iotDataScore, medicalHistoryScore, procedureComplexityScore, complianceScore,
                userId, reservationId);
        
        // Identify contributing factors
        String contributingFactors = identifyContributingFactors(
                iotDataScore, medicalHistoryScore, procedureComplexityScore, complianceScore,
                scoreChange, userId, reservationId);
        
        // Create risk score
        PatientRiskScore riskScore = new PatientRiskScore();
        riskScore.setUserId(userId);
        riskScore.setReservationId(reservationId);
        riskScore.setRecoveryScore(recoveryScore);
        riskScore.setScoreCategory(scoreCategory);
        riskScore.setIotDataScore(iotDataScore);
        riskScore.setMedicalHistoryScore(medicalHistoryScore);
        riskScore.setProcedureComplexityScore(procedureComplexityScore);
        riskScore.setComplianceScore(complianceScore);
        riskScore.setTrend(trend);
        riskScore.setPreviousScore(previousScoreValue);
        riskScore.setScoreChange(scoreChange);
        riskScore.setRequiresDoctorAlert(requiresAlert);
        riskScore.setAiConfidence(new BigDecimal("0.85"));
        riskScore.setScoreExplanation(scoreExplanation);
        riskScore.setContributingFactors(contributingFactors);
        riskScore.setAnalysisDetails(buildAnalysisDetails(iotDataScore, medicalHistoryScore, 
                procedureComplexityScore, complianceScore, recoveryScore));
        
        // Save score
        riskScore = riskScoreRepository.save(riskScore);
        
        // Send alerts if needed
        if (requiresAlert) {
            alertService.sendRecoveryScoreAlert(riskScore);
            riskScore.setLastAlertSentAt(LocalDateTime.now());
            riskScore.setAlertReason("Recovery score dropped significantly or below threshold");
            riskScore = riskScoreRepository.save(riskScore);
        } else if (recoveryScore.compareTo(new BigDecimal("80")) > 0) {
            // Send positive notification
            alertService.sendPositiveNotification(userId, recoveryScore);
        }
        
        return riskScore;
    }
    
    /**
     * Calculate IoT data score (0-100)
     */
    private BigDecimal calculateIoTDataScore(Long userId, Long reservationId) {
        try {
            String url = iotMonitoringServiceUrl + "/api/iot-monitoring/reservation/" + reservationId;
            List<Map<String, Object>> iotData = getRestTemplate().getForObject(url, List.class);
            
            if (iotData == null || iotData.isEmpty()) {
                return new BigDecimal("50.0"); // Neutral score if no data
            }
            
            // Analyze latest IoT data
            Map<String, Object> latestData = iotData.get(0);
            BigDecimal score = new BigDecimal("70.0"); // Base score
            
            // Heart rate analysis
            if (latestData.containsKey("heartRate")) {
                BigDecimal heartRate = new BigDecimal(latestData.get("heartRate").toString());
                if (heartRate.compareTo(new BigDecimal("60")) >= 0 && heartRate.compareTo(new BigDecimal("100")) <= 0) {
                    score = score.add(new BigDecimal("10")); // Normal heart rate
                } else {
                    score = score.subtract(new BigDecimal("20")); // Abnormal heart rate
                }
            }
            
            // Oxygen saturation analysis
            if (latestData.containsKey("oxygenSaturation")) {
                BigDecimal oxygenSat = new BigDecimal(latestData.get("oxygenSaturation").toString());
                if (oxygenSat.compareTo(new BigDecimal("95")) >= 0) {
                    score = score.add(new BigDecimal("10")); // Good oxygen levels
                } else {
                    score = score.subtract(new BigDecimal("30")); // Low oxygen
                }
            }
            
            // Activity analysis
            if (latestData.containsKey("steps")) {
                BigDecimal steps = new BigDecimal(latestData.get("steps").toString());
                if (steps.compareTo(new BigDecimal("5000")) >= 0) {
                    score = score.add(new BigDecimal("5")); // Good activity
                }
            }
            
            // Pain level analysis
            if (latestData.containsKey("painLevel")) {
                BigDecimal painLevel = new BigDecimal(latestData.get("painLevel").toString());
                if (painLevel.compareTo(new BigDecimal("3")) <= 0) {
                    score = score.add(new BigDecimal("5")); // Low pain
                } else if (painLevel.compareTo(new BigDecimal("7")) > 0) {
                    score = score.subtract(new BigDecimal("15")); // High pain
                }
            }
            
            return score.max(new BigDecimal("0")).min(new BigDecimal("100"));
        } catch (Exception e) {
            System.err.println("Failed to calculate IoT data score: " + e.getMessage());
            return new BigDecimal("50.0");
        }
    }
    
    /**
     * Calculate medical history score
     */
    private BigDecimal calculateMedicalHistoryScore(Long userId) {
        try {
            String url = medicalDocumentServiceUrl + "/api/medical-documents/user/" + userId;
            List<Map<String, Object>> medicalDocs = getRestTemplate().getForObject(url, List.class);
            
            if (medicalDocs == null || medicalDocs.isEmpty()) {
                return new BigDecimal("70.0"); // Neutral if no history
            }
            
            // Analyze medical history complexity
            // More documents might indicate more complex history
            int docCount = medicalDocs.size();
            BigDecimal score = new BigDecimal("80.0");
            
            if (docCount > 10) {
                score = score.subtract(new BigDecimal("10")); // Complex history
            }
            
            return score.max(new BigDecimal("0")).min(new BigDecimal("100"));
        } catch (Exception e) {
            System.err.println("Failed to calculate medical history score: " + e.getMessage());
            return new BigDecimal("70.0");
        }
    }
    
    /**
     * Calculate procedure complexity score
     */
    private BigDecimal calculateProcedureComplexityScore(Long reservationId) {
        try {
            String url = reservationServiceUrl + "/api/reservations/" + reservationId;
            Map<String, Object> reservation = getRestTemplate().getForObject(url, Map.class);
            
            if (reservation == null) {
                return new BigDecimal("70.0");
            }
            
            // Procedure complexity affects recovery
            // More complex procedures = lower initial score, but can improve
            String procedureType = (String) reservation.getOrDefault("procedureType", "CONSULTATION");
            
            BigDecimal score = new BigDecimal("80.0");
            
            if (procedureType.contains("SURGERY") || procedureType.contains("SURGICAL")) {
                score = score.subtract(new BigDecimal("20")); // Surgery is more complex
            }
            
            return score.max(new BigDecimal("0")).min(new BigDecimal("100"));
        } catch (Exception e) {
            System.err.println("Failed to calculate procedure complexity score: " + e.getMessage());
            return new BigDecimal("70.0");
        }
    }
    
    /**
     * Calculate compliance score (medication, exercise, etc.)
     */
    private BigDecimal calculateComplianceScore(Long userId, Long reservationId) {
        // This would integrate with Health Token service or IoT data
        // For now, return a base score
        return new BigDecimal("75.0");
    }
    
    /**
     * Calculate weighted recovery score
     */
    private BigDecimal calculateWeightedScore(
            BigDecimal iotDataScore,
            BigDecimal medicalHistoryScore,
            BigDecimal procedureComplexityScore,
            BigDecimal complianceScore) {
        
        // Weights: IoT data is most important (40%), then compliance (30%), medical history (20%), procedure (10%)
        BigDecimal weightedScore = iotDataScore.multiply(new BigDecimal("0.40"))
                .add(complianceScore.multiply(new BigDecimal("0.30")))
                .add(medicalHistoryScore.multiply(new BigDecimal("0.20")))
                .add(procedureComplexityScore.multiply(new BigDecimal("0.10")));
        
        return weightedScore.setScale(2, RoundingMode.HALF_UP);
    }
    
    private String determineScoreCategory(BigDecimal score) {
        if (score.compareTo(new BigDecimal("80")) >= 0) {
            return "EXCELLENT";
        } else if (score.compareTo(new BigDecimal("60")) >= 0) {
            return "GOOD";
        } else if (score.compareTo(new BigDecimal("40")) >= 0) {
            return "FAIR";
        } else {
            return "POOR";
        }
    }
    
    private String determineTrend(BigDecimal scoreChange) {
        if (scoreChange.compareTo(new BigDecimal("5")) > 0) {
            return "IMPROVING";
        } else if (scoreChange.compareTo(new BigDecimal("-5")) < 0) {
            return "DECLINING";
        } else {
            return "STABLE";
        }
    }
    
    private boolean shouldAlertDoctor(BigDecimal currentScore, BigDecimal previousScore, BigDecimal scoreChange) {
        // Alert if score drops significantly or falls below threshold
        if (currentScore.compareTo(new BigDecimal("40")) < 0) {
            return true; // Critical threshold
        }
        if (scoreChange.compareTo(new BigDecimal("-10")) < 0) {
            return true; // Significant drop
        }
        return false;
    }
    
    private String buildAnalysisDetails(
            BigDecimal iotScore, BigDecimal medicalScore, 
            BigDecimal procedureScore, BigDecimal complianceScore, BigDecimal recoveryScore) {
        Map<String, Object> details = new HashMap<>();
        details.put("iotDataScore", iotScore);
        details.put("medicalHistoryScore", medicalScore);
        details.put("procedureComplexityScore", procedureScore);
        details.put("complianceScore", complianceScore);
        details.put("recoveryScore", recoveryScore);
        details.put("calculatedAt", LocalDateTime.now().toString());
        return details.toString();
    }
    
    /**
     * Periodic score recalculation (runs every hour)
     */
    @Scheduled(fixedRate = 3600000) // 1 hour
    public void recalculateActivePatientScores() {
        // Get all active reservations and recalculate scores
        // This would query reservation service for active post-op patients
        // For now, this is a placeholder
    }
    
    public PatientRiskScore getLatestScore(Long userId, Long reservationId) {
        List<PatientRiskScore> scores = riskScoreRepository.findByUserIdAndReservationIdOrderByCalculatedAtDesc(
                userId, reservationId);
        return scores.isEmpty() ? null : scores.get(0);
    }
    
    public List<PatientRiskScore> getScoreHistory(Long userId, Long reservationId) {
        return riskScoreRepository.findByUserIdAndReservationIdOrderByCalculatedAtDesc(userId, reservationId);
    }
    
    public List<PatientRiskScore> getScoresRequiringAlert() {
        return riskScoreRepository.findByRequiresDoctorAlertTrue();
    }
    
    /**
     * Generate human-readable explanation for score change (AI Explainability)
     * Example: "Düşüş Nedeni: IoT üzerinden gelen düşük oksijen satürasyonu (%92) ve 
     * hastanın 2. gün hareketliliğinin azalması (1,200 adım/gün)"
     */
    private String generateScoreExplanation(
            BigDecimal currentScore,
            BigDecimal previousScore,
            BigDecimal scoreChange,
            BigDecimal iotDataScore,
            BigDecimal medicalHistoryScore,
            BigDecimal procedureComplexityScore,
            BigDecimal complianceScore,
            Long userId,
            Long reservationId) {
        
        StringBuilder explanation = new StringBuilder();
        
        // Determine if score improved or declined
        if (scoreChange.compareTo(BigDecimal.ZERO) > 0) {
            explanation.append("Skor ").append(previousScore).append("'den ").append(currentScore)
                     .append("'e yükseldi (+").append(scoreChange).append("). ");
        } else if (scoreChange.compareTo(BigDecimal.ZERO) < 0) {
            explanation.append("Skor ").append(previousScore).append("'den ").append(currentScore)
                     .append("'e düştü (").append(scoreChange).append("). ");
        } else {
            explanation.append("Skor ").append(currentScore).append(" olarak sabit kaldı. ");
        }
        
        // Explain contributing factors
        List<String> factors = new ArrayList<>();
        
        // IoT Data factors
        if (iotDataScore.compareTo(new BigDecimal("60")) < 0) {
            factors.add("IoT verilerinden gelen düşük vital bulgular");
        } else if (iotDataScore.compareTo(new BigDecimal("80")) >= 0) {
            factors.add("IoT verilerinden gelen iyi vital bulgular");
        }
        
        // Get specific IoT data for detailed explanation
        try {
            String url = iotMonitoringServiceUrl + "/api/iot-monitoring/reservation/" + reservationId;
            List<Map<String, Object>> iotData = getRestTemplate().getForObject(url, List.class);
            
            if (iotData != null && !iotData.isEmpty()) {
                Map<String, Object> latestData = iotData.get(0);
                
                // Oxygen saturation
                if (latestData.containsKey("oxygenSaturation")) {
                    BigDecimal oxygenSat = new BigDecimal(latestData.get("oxygenSaturation").toString());
                    if (oxygenSat.compareTo(new BigDecimal("95")) < 0) {
                        factors.add("düşük oksijen satürasyonu (%" + oxygenSat + ")");
                    }
                }
                
                // Steps/Activity
                if (latestData.containsKey("steps")) {
                    BigDecimal steps = new BigDecimal(latestData.get("steps").toString());
                    if (steps.compareTo(new BigDecimal("5000")) < 0) {
                        factors.add("hareketliliğin azalması (" + steps + " adım/gün)");
                    }
                }
                
                // Pain level
                if (latestData.containsKey("painLevel")) {
                    BigDecimal painLevel = new BigDecimal(latestData.get("painLevel").toString());
                    if (painLevel.compareTo(new BigDecimal("5")) > 0) {
                        factors.add("yüksek ağrı seviyesi (" + painLevel + "/10)");
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Failed to get IoT data for explanation: " + e.getMessage());
        }
        
        // Compliance factors
        if (complianceScore.compareTo(new BigDecimal("60")) < 0) {
            factors.add("ilaç uyumunun düşük olması");
        }
        
        // Medical history factors
        if (medicalHistoryScore.compareTo(new BigDecimal("60")) < 0) {
            factors.add("karmaşık tıbbi geçmiş");
        }
        
        // Build explanation
        if (!factors.isEmpty()) {
            explanation.append("Düşüş Nedeni: ");
            for (int i = 0; i < factors.size(); i++) {
                explanation.append(factors.get(i));
                if (i < factors.size() - 1) {
                    explanation.append(" ve ");
                }
            }
            explanation.append(".");
        } else {
            explanation.append("Skor değişikliği beklenen aralıkta.");
        }
        
        return explanation.toString();
    }
    
    /**
     * Identify contributing factors (JSON format for structured data)
     */
    private String identifyContributingFactors(
            BigDecimal iotDataScore,
            BigDecimal medicalHistoryScore,
            BigDecimal procedureComplexityScore,
            BigDecimal complianceScore,
            BigDecimal scoreChange,
            Long userId,
            Long reservationId) {
        
        Map<String, Object> factors = new HashMap<>();
        
        factors.put("iotDataScore", iotDataScore);
        factors.put("medicalHistoryScore", medicalHistoryScore);
        factors.put("procedureComplexityScore", procedureComplexityScore);
        factors.put("complianceScore", complianceScore);
        factors.put("scoreChange", scoreChange);
        
        // Get specific IoT metrics
        try {
            String url = iotMonitoringServiceUrl + "/api/iot-monitoring/reservation/" + reservationId;
            List<Map<String, Object>> iotData = getRestTemplate().getForObject(url, List.class);
            
            if (iotData != null && !iotData.isEmpty()) {
                Map<String, Object> latestData = iotData.get(0);
                Map<String, Object> iotMetrics = new HashMap<>();
                
                if (latestData.containsKey("oxygenSaturation")) {
                    iotMetrics.put("oxygenSaturation", latestData.get("oxygenSaturation"));
                }
                if (latestData.containsKey("heartRate")) {
                    iotMetrics.put("heartRate", latestData.get("heartRate"));
                }
                if (latestData.containsKey("steps")) {
                    iotMetrics.put("steps", latestData.get("steps"));
                }
                if (latestData.containsKey("painLevel")) {
                    iotMetrics.put("painLevel", latestData.get("painLevel"));
                }
                
                factors.put("iotMetrics", iotMetrics);
            }
        } catch (Exception e) {
            System.err.println("Failed to get IoT metrics: " + e.getMessage());
        }
        
        return factors.toString(); // In production, use ObjectMapper for proper JSON
    }
}
