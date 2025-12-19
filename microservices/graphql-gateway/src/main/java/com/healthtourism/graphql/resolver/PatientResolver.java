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
        
        // Fetch from multiple services in parallel
        patientData.setReservations(dataFetcher.fetchReservations(patientId));
        patientData.setHealthRecords(dataFetcher.fetchHealthRecords(patientId));
        patientData.setIoTData(dataFetcher.fetchIoTData(patientId));
        patientData.setRiskScore(dataFetcher.fetchRiskScore(patientId));
        
        return patientData;
    }
}

