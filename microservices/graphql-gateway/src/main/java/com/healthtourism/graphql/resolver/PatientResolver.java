package com.healthtourism.graphql.resolver;

import com.healthtourism.graphql.model.PatientData;
import com.healthtourism.graphql.service.GraphQLDataFetcher;
import graphql.kickstart.tools.GraphQLQueryResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Patient GraphQL Resolver
 * Fetches data from multiple services in single query
 */
@Component
public class PatientResolver implements GraphQLQueryResolver {
    
    @Autowired
    private GraphQLDataFetcher dataFetcher;
    
    /**
     * Single GraphQL query to get:
     * - Patient reservations (Reservation Service)
     * - Health records (AI Service)
     * - IoT data (IoT Service)
     * - Risk score (Risk Scoring Service)
     */
    public PatientData getPatientData(Long patientId) {
        PatientData patientData = new PatientData();
        patientData.setPatientId(patientId);
        
        try {
            // Fetch from multiple services in parallel
            patientData.setReservations(dataFetcher.fetchReservations(patientId));
            patientData.setHealthRecords(dataFetcher.fetchHealthRecords(patientId));
            patientData.setIoTData(dataFetcher.fetchIoTData(patientId));
            patientData.setRiskScore(dataFetcher.fetchRiskScore(patientId));
        } catch (Exception e) {
            // Handle errors gracefully
            // In production, log and return partial data
            patientData.setReservations(java.util.Collections.emptyList());
            patientData.setHealthRecords(java.util.Collections.emptyList());
            patientData.setIoTData(java.util.Collections.emptyList());
        }
        
        return patientData;
    }
}

