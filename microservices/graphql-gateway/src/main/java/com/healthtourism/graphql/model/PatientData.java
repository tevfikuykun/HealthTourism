package com.healthtourism.graphql.model;

import lombok.Data;
import java.util.List;

/**
 * Patient Data Model for GraphQL
 * Combines data from multiple services
 */
@Data
public class PatientData {
    private Long patientId;
    private String name;
    private String email;
    
    // From Reservation Service
    private List<Reservation> reservations;
    
    // From AI Health Companion Service
    private List<HealthRecord> healthRecords;
    
    // From IoT Monitoring Service
    private List<IoTData> iotData;
    
    // From Patient Risk Scoring Service
    private RiskScore riskScore;
    
    @lombok.Data
    public static class Reservation {
        private Long id;
        private String hospitalName;
        private String procedureType;
        private String status;
        private String reservationDate;
    }
    
    @lombok.Data
    public static class HealthRecord {
        private Long id;
        private String recordType;
        private String diagnosis;
        private String treatment;
        private String recordDate;
    }
    
    @lombok.Data
    public static class IoTData {
        private Long id;
        private Double heartRate;
        private Double temperature;
        private String timestamp;
    }
    
    @lombok.Data
    public static class RiskScore {
        private Double recoveryScore;
        private String trend;
        private String lastCalculated;
    }
}



