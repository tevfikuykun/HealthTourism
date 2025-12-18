package com.healthtourism.patientriskscoringservice.service;

import com.healthtourism.patientriskscoringservice.entity.PatientRiskScore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * Alert Service for Recovery Score alerts
 */
@Service
public class AlertService {
    
    @Autowired(required = false)
    private RestTemplate restTemplate;
    
    @Value("${notification.service.url:http://localhost:8011}")
    private String notificationServiceUrl;
    
    private RestTemplate getRestTemplate() {
        if (restTemplate == null) {
            restTemplate = new RestTemplate();
        }
        return restTemplate;
    }
    
    public void sendRecoveryScoreAlert(PatientRiskScore riskScore) {
        try {
            Map<String, Object> notification = new HashMap<>();
            notification.put("userId", riskScore.getDoctorId()); // Send to doctor
            notification.put("type", "RECOVERY_SCORE_ALERT");
            notification.put("title", "Patient Recovery Score Alert");
            notification.put("message", buildAlertMessage(riskScore));
            notification.put("priority", "HIGH");
            notification.put("metadata", buildAlertMetadata(riskScore));
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(notification, headers);
            String url = notificationServiceUrl + "/api/notifications";
            getRestTemplate().postForObject(url, request, Map.class);
        } catch (Exception e) {
            System.err.println("Failed to send recovery score alert: " + e.getMessage());
        }
    }
    
    public void sendPositiveNotification(Long userId, BigDecimal recoveryScore) {
        try {
            Map<String, Object> notification = new HashMap<>();
            notification.put("userId", userId);
            notification.put("type", "RECOVERY_SCORE_POSITIVE");
            notification.put("title", "Great Progress! 🎉");
            notification.put("message", "Your recovery score is " + recoveryScore + 
                    "/100. You're doing great! Keep up the excellent work!");
            notification.put("priority", "LOW");
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(notification, headers);
            String url = notificationServiceUrl + "/api/notifications";
            getRestTemplate().postForObject(url, request, Map.class);
        } catch (Exception e) {
            System.err.println("Failed to send positive notification: " + e.getMessage());
        }
    }
    
    private String buildAlertMessage(PatientRiskScore riskScore) {
        StringBuilder message = new StringBuilder();
        message.append("Patient ID: ").append(riskScore.getUserId()).append("\n");
        message.append("Recovery Score: ").append(riskScore.getRecoveryScore()).append("/100\n");
        message.append("Score Category: ").append(riskScore.getScoreCategory()).append("\n");
        message.append("Trend: ").append(riskScore.getTrend()).append("\n");
        message.append("Score Change: ").append(riskScore.getScoreChange()).append("\n");
        message.append("\nPlease review patient's condition.");
        return message.toString();
    }
    
    private Map<String, Object> buildAlertMetadata(PatientRiskScore riskScore) {
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("reservationId", riskScore.getReservationId());
        metadata.put("patientId", riskScore.getUserId());
        metadata.put("recoveryScore", riskScore.getRecoveryScore().toString());
        metadata.put("scoreCategory", riskScore.getScoreCategory());
        metadata.put("trend", riskScore.getTrend());
        metadata.put("calculatedAt", riskScore.getCalculatedAt().toString());
        return metadata;
    }
}
