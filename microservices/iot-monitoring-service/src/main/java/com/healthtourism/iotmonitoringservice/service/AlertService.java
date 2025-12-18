package com.healthtourism.iotmonitoringservice.service;

import com.healthtourism.iotmonitoringservice.entity.PatientMonitoringData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * Service for sending alerts to doctors when critical values are detected
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
    
    public void sendAlert(PatientMonitoringData data) {
        try {
            // Create alert notification
            Map<String, Object> notification = new HashMap<>();
            notification.put("userId", data.getDoctorId()); // Send to doctor
            notification.put("type", "PATIENT_ALERT");
            notification.put("title", "Patient Monitoring Alert");
            notification.put("message", buildAlertMessage(data));
            notification.put("priority", data.getAlertStatus());
            notification.put("metadata", buildAlertMetadata(data));
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(notification, headers);
            String url = notificationServiceUrl + "/api/notifications";
            getRestTemplate().postForObject(url, request, Map.class);
            
        } catch (Exception e) {
            System.err.println("Failed to send alert: " + e.getMessage());
        }
    }
    
    private String buildAlertMessage(PatientMonitoringData data) {
        StringBuilder message = new StringBuilder();
        message.append("Patient ID: ").append(data.getUserId()).append("\n");
        message.append("Alert Level: ").append(data.getAlertStatus()).append("\n");
        
        if (data.getHeartRate() != null) {
            message.append("Heart Rate: ").append(data.getHeartRate()).append(" BPM\n");
        }
        if (data.getOxygenSaturation() != null) {
            message.append("Oxygen Saturation: ").append(data.getOxygenSaturation()).append("%\n");
        }
        if (data.getBodyTemperature() != null) {
            message.append("Temperature: ").append(data.getBodyTemperature()).append("°C\n");
        }
        if (data.getPainLevel() != null) {
            message.append("Pain Level: ").append(data.getPainLevel()).append("/10\n");
        }
        
        return message.toString();
    }
    
    private Map<String, Object> buildAlertMetadata(PatientMonitoringData data) {
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("reservationId", data.getReservationId());
        metadata.put("patientId", data.getUserId());
        metadata.put("deviceType", data.getDeviceType());
        metadata.put("recordedAt", data.getRecordedAt().toString());
        return metadata;
    }
}
