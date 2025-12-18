package com.healthtourism.iotmonitoringservice.service;

import com.healthtourism.iotmonitoringservice.entity.PatientMonitoringData;
import com.healthtourism.iotmonitoringservice.repository.PatientMonitoringDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class IoTMonitoringService {
    
    @Autowired
    private PatientMonitoringDataRepository monitoringRepository;
    
    @Autowired
    private AlertService alertService;
    
    /**
     * Receive data from IoT device (Apple Watch, Fitbit, etc.)
     */
    @Transactional
    public PatientMonitoringData recordMonitoringData(
            Long userId,
            Long reservationId,
            Long doctorId,
            String deviceType,
            String deviceId,
            Map<String, Object> data) {
        
        PatientMonitoringData monitoringData = new PatientMonitoringData();
        monitoringData.setUserId(userId);
        monitoringData.setReservationId(reservationId);
        monitoringData.setDoctorId(doctorId);
        monitoringData.setDeviceType(deviceType);
        monitoringData.setDeviceId(deviceId);
        
        // Extract vital signs
        if (data.containsKey("heartRate")) {
            monitoringData.setHeartRate(new BigDecimal(data.get("heartRate").toString()));
        }
        if (data.containsKey("bloodPressureSystolic")) {
            monitoringData.setBloodPressureSystolic(new BigDecimal(data.get("bloodPressureSystolic").toString()));
        }
        if (data.containsKey("bloodPressureDiastolic")) {
            monitoringData.setBloodPressureDiastolic(new BigDecimal(data.get("bloodPressureDiastolic").toString()));
        }
        if (data.containsKey("bodyTemperature")) {
            monitoringData.setBodyTemperature(new BigDecimal(data.get("bodyTemperature").toString()));
        }
        if (data.containsKey("oxygenSaturation")) {
            monitoringData.setOxygenSaturation(new BigDecimal(data.get("oxygenSaturation").toString()));
        }
        if (data.containsKey("respiratoryRate")) {
            monitoringData.setRespiratoryRate(new BigDecimal(data.get("respiratoryRate").toString()));
        }
        
        // Extract activity data
        if (data.containsKey("steps")) {
            monitoringData.setSteps(new BigDecimal(data.get("steps").toString()));
        }
        if (data.containsKey("distanceKm")) {
            monitoringData.setDistanceKm(new BigDecimal(data.get("distanceKm").toString()));
        }
        if (data.containsKey("caloriesBurned")) {
            monitoringData.setCaloriesBurned(new BigDecimal(data.get("caloriesBurned").toString()));
        }
        
        // Extract sleep data
        if (data.containsKey("sleepHours")) {
            monitoringData.setSleepHours(new BigDecimal(data.get("sleepHours").toString()));
        }
        if (data.containsKey("sleepQuality")) {
            monitoringData.setSleepQuality(data.get("sleepQuality").toString());
        }
        
        // Extract pain & symptoms
        if (data.containsKey("painLevel")) {
            monitoringData.setPainLevel(new BigDecimal(data.get("painLevel").toString()));
        }
        if (data.containsKey("symptoms")) {
            monitoringData.setSymptoms(data.get("symptoms").toString());
        }
        if (data.containsKey("notes")) {
            monitoringData.setNotes(data.get("notes").toString());
        }
        
        // Analyze and set alert status
        String alertStatus = analyzeVitalSigns(monitoringData);
        monitoringData.setAlertStatus(alertStatus);
        
        // Save data
        monitoringData = monitoringRepository.save(monitoringData);
        
        // Send alert if critical
        if ("CRITICAL".equals(alertStatus) || "WARNING".equals(alertStatus)) {
            alertService.sendAlert(monitoringData);
            monitoringData.setAlertSentAt(LocalDateTime.now());
            monitoringData = monitoringRepository.save(monitoringData);
        }
        
        return monitoringData;
    }
    
    /**
     * Analyze vital signs and determine alert status
     */
    private String analyzeVitalSigns(PatientMonitoringData data) {
        // Critical thresholds
        if (data.getHeartRate() != null) {
            if (data.getHeartRate().compareTo(new BigDecimal("40")) < 0 || 
                data.getHeartRate().compareTo(new BigDecimal("150")) > 0) {
                return "CRITICAL";
            }
            if (data.getHeartRate().compareTo(new BigDecimal("50")) < 0 || 
                data.getHeartRate().compareTo(new BigDecimal("120")) > 0) {
                return "WARNING";
            }
        }
        
        if (data.getOxygenSaturation() != null) {
            if (data.getOxygenSaturation().compareTo(new BigDecimal("90")) < 0) {
                return "CRITICAL";
            }
            if (data.getOxygenSaturation().compareTo(new BigDecimal("95")) < 0) {
                return "WARNING";
            }
        }
        
        if (data.getBodyTemperature() != null) {
            if (data.getBodyTemperature().compareTo(new BigDecimal("38.5")) > 0) {
                return "WARNING"; // Fever
            }
        }
        
        if (data.getPainLevel() != null) {
            if (data.getPainLevel().compareTo(new BigDecimal("7")) > 0) {
                return "WARNING"; // Severe pain
            }
        }
        
        return "NORMAL";
    }
    
    public List<PatientMonitoringData> getMonitoringDataByUser(Long userId) {
        return monitoringRepository.findByUserIdOrderByRecordedAtDesc(userId);
    }
    
    public List<PatientMonitoringData> getMonitoringDataByReservation(Long reservationId) {
        return monitoringRepository.findByReservationIdOrderByRecordedAtDesc(reservationId);
    }
    
    public List<PatientMonitoringData> getMonitoringDataByDoctor(Long doctorId) {
        return monitoringRepository.findByDoctorIdOrderByRecordedAtDesc(doctorId);
    }
    
    public List<PatientMonitoringData> getRecentDataByUser(Long userId, int hours) {
        LocalDateTime since = LocalDateTime.now().minusHours(hours);
        return monitoringRepository.findRecentByUser(userId, since);
    }
    
    public List<PatientMonitoringData> getAlerts(String alertStatus) {
        return monitoringRepository.findByAlertStatus(alertStatus);
    }
    
    public PatientMonitoringData getLatestDataByUser(Long userId) {
        List<PatientMonitoringData> data = monitoringRepository.findByUserIdOrderByRecordedAtDesc(userId);
        return data.isEmpty() ? null : data.get(0);
    }
}
