package com.healthtourism.iotmonitoringservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Post-Op Remote Patient Monitoring Data
 * Collects data from wearable devices (Apple Watch, Fitbit, etc.)
 */
@Entity
@Table(name = "patient_monitoring_data")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PatientMonitoringData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private Long userId;
    
    @Column(nullable = false)
    private Long reservationId; // Associated reservation
    
    @Column(nullable = false)
    private Long doctorId; // Monitoring doctor
    
    // Device Information
    @Column(nullable = false)
    private String deviceType; // APPLE_WATCH, FITBIT, SAMSUNG_HEALTH, MANUAL
    
    @Column(length = 500)
    private String deviceId; // Device identifier
    
    // Vital Signs
    @Column(precision = 5, scale = 2)
    private BigDecimal heartRate; // BPM
    
    @Column(precision = 5, scale = 2)
    private BigDecimal bloodPressureSystolic; // mmHg
    
    @Column(precision = 5, scale = 2)
    private BigDecimal bloodPressureDiastolic; // mmHg
    
    @Column(precision = 5, scale = 2)
    private BigDecimal bodyTemperature; // Celsius
    
    @Column(precision = 5, scale = 2)
    private BigDecimal oxygenSaturation; // SpO2 %
    
    @Column(precision = 5, scale = 2)
    private BigDecimal respiratoryRate; // Breaths per minute
    
    // Activity Data
    @Column(precision = 6, scale = 2)
    private BigDecimal steps; // Daily steps
    
    @Column(precision = 6, scale = 2)
    private BigDecimal distanceKm; // Distance in kilometers
    
    @Column(precision = 5, scale = 2)
    private BigDecimal caloriesBurned; // Calories
    
    // Sleep Data
    @Column(precision = 5, scale = 2)
    private BigDecimal sleepHours; // Hours of sleep
    
    @Column(length = 50)
    private String sleepQuality; // EXCELLENT, GOOD, FAIR, POOR
    
    // Pain & Symptoms
    @Column(precision = 3, scale = 1)
    private BigDecimal painLevel; // 0-10 scale
    
    @Column(length = 1000)
    private String symptoms; // JSON string with symptoms
    
    @Column(length = 2000)
    private String notes; // Additional notes
    
    // Alert Status
    @Column(nullable = false)
    private String alertStatus; // NORMAL, WARNING, CRITICAL
    
    @Column
    private LocalDateTime alertSentAt; // When alert was sent to doctor
    
    // Timestamps
    @Column(nullable = false)
    private LocalDateTime recordedAt; // When data was recorded
    
    @Column(nullable = false)
    private LocalDateTime createdAt; // When record was created in system
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (recordedAt == null) {
            recordedAt = LocalDateTime.now();
        }
        if (alertStatus == null) {
            alertStatus = "NORMAL";
        }
    }
}
