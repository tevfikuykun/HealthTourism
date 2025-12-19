package com.healthtourism.graphql.service;

import com.healthtourism.graphql.model.PatientData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * GraphQL Data Fetcher
 * Fetches data from multiple microservices
 */
@Service
public class GraphQLDataFetcher {
    
    @Autowired
    private WebClient.Builder webClientBuilder;
    
    @Value("${services.reservation.url:http://reservation-service}")
    private String reservationServiceUrl;
    
    @Value("${services.ai-companion.url:http://ai-health-companion-service}")
    private String aiCompanionServiceUrl;
    
    @Value("${services.iot-monitoring.url:http://iot-monitoring-service}")
    private String iotMonitoringServiceUrl;
    
    @Value("${services.risk-scoring.url:http://patient-risk-scoring-service}")
    private String riskScoringServiceUrl;
    
    /**
     * Fetch reservations from Reservation Service
     */
    public List<PatientData.Reservation> fetchReservations(Long patientId) {
        return webClientBuilder.build()
            .get()
            .uri(reservationServiceUrl + "/api/reservations/patient/" + patientId)
            .retrieve()
            .bodyToMono(new ParameterizedTypeReference<List<PatientData.Reservation>>() {})
            .block();
    }
    
    /**
     * Fetch health records from AI Companion Service
     */
    public List<PatientData.HealthRecord> fetchHealthRecords(Long patientId) {
        return webClientBuilder.build()
            .get()
            .uri(aiCompanionServiceUrl + "/api/health-records/patient/" + patientId)
            .retrieve()
            .bodyToMono(new ParameterizedTypeReference<List<PatientData.HealthRecord>>() {})
            .block();
    }
    
    /**
     * Fetch IoT data from IoT Monitoring Service
     */
    public List<PatientData.IoTData> fetchIoTData(Long patientId) {
        return webClientBuilder.build()
            .get()
            .uri(iotMonitoringServiceUrl + "/api/iot-monitoring/user/" + patientId + "/recent")
            .retrieve()
            .bodyToMono(new ParameterizedTypeReference<List<PatientData.IoTData>>() {})
            .block();
    }
    
    /**
     * Fetch risk score from Risk Scoring Service
     */
    public PatientData.RiskScore fetchRiskScore(Long patientId) {
        return webClientBuilder.build()
            .get()
            .uri(riskScoringServiceUrl + "/api/patient-risk-scoring/user/" + patientId)
            .retrieve()
            .bodyToMono(PatientData.RiskScore.class)
            .block();
    }
}

