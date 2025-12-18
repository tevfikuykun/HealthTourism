package com.healthtourism.fhiradapterservice.service;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import org.hl7.fhir.r4.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * FHIR Conversion Service
 * Converts internal data to FHIR R4 format for EHR interoperability
 */
@Service
public class FhirConversionService {
    
    @Autowired(required = false)
    private RestTemplate restTemplate;
    
    @Value("${health.wallet.service.url:http://localhost:8037}")
    private String healthWalletServiceUrl;
    
    @Value("${iot.monitoring.service.url:http://localhost:8032}")
    private String iotMonitoringServiceUrl;
    
    private final FhirContext fhirContext = FhirContext.forR4();
    private final IParser jsonParser = fhirContext.newJsonParser();
    
    private RestTemplate getRestTemplate() {
        if (restTemplate == null) {
            restTemplate = new RestTemplate();
        }
        return restTemplate;
    }
    
    /**
     * Convert patient data to FHIR Patient resource
     */
    public Patient convertToFhirPatient(Long userId, Map<String, Object> patientData) {
        Patient patient = new Patient();
        
        // Set ID
        patient.setId(userId.toString());
        
        // Add identifier
        patient.addIdentifier()
            .setSystem("http://healthtourism.com/patient-id")
            .setValue("PAT-" + userId);
        
        // Set name
        if (patientData.containsKey("firstName") && patientData.containsKey("lastName")) {
            patient.addName()
                .setFamily(patientData.get("lastName").toString())
                .addGiven(patientData.get("firstName").toString());
        }
        
        // Set birth date
        if (patientData.containsKey("birthDate")) {
            patient.setBirthDate((Date) patientData.get("birthDate"));
        }
        
        // Set gender
        if (patientData.containsKey("gender")) {
            String gender = patientData.get("gender").toString();
            patient.setGender(gender.equalsIgnoreCase("MALE") ? 
                Enumerations.AdministrativeGender.MALE : 
                Enumerations.AdministrativeGender.FEMALE);
        }
        
        // Set address
        if (patientData.containsKey("address")) {
            Address address = new Address();
            address.setText(patientData.get("address").toString());
            patient.addAddress(address);
        }
        
        // Set contact
        if (patientData.containsKey("email")) {
            ContactPoint contactPoint = new ContactPoint();
            contactPoint.setSystem(ContactPoint.ContactPointSystem.EMAIL);
            contactPoint.setValue(patientData.get("email").toString());
            patient.addTelecom(contactPoint);
        }
        
        return patient;
    }
    
    /**
     * Convert IoT monitoring data to FHIR Observation resources
     */
    public Observation convertToFhirObservation(Map<String, Object> iotData) {
        Observation observation = new Observation();
        
        // Set status
        observation.setStatus(Observation.ObservationStatus.FINAL);
        
        // Set code (LOINC codes)
        if (iotData.containsKey("metricType")) {
            String metricType = iotData.get("metricType").toString();
            CodeableConcept code = new CodeableConcept();
            
            switch (metricType.toUpperCase()) {
                case "HEART_RATE":
                    code.addCoding()
                        .setSystem("http://loinc.org")
                        .setCode("8867-4")
                        .setDisplay("Heart rate");
                    break;
                case "BLOOD_PRESSURE":
                    code.addCoding()
                        .setSystem("http://loinc.org")
                        .setCode("85354-9")
                        .setDisplay("Blood pressure");
                    break;
                case "TEMPERATURE":
                    code.addCoding()
                        .setSystem("http://loinc.org")
                        .setCode("8310-5")
                        .setDisplay("Body temperature");
                    break;
                case "OXYGEN_SATURATION":
                    code.addCoding()
                        .setSystem("http://loinc.org")
                        .setCode("2708-6")
                        .setDisplay("Oxygen saturation");
                    break;
            }
            
            observation.setCode(code);
        }
        
        // Set value
        if (iotData.containsKey("value")) {
            Quantity value = new Quantity();
            value.setValue(Double.parseDouble(iotData.get("value").toString()));
            
            if (iotData.containsKey("unit")) {
                value.setUnit(iotData.get("unit").toString());
            }
            
            observation.setValue(value);
        }
        
        // Set effective date
        if (iotData.containsKey("timestamp")) {
            observation.setEffective(new DateTimeType((Date) iotData.get("timestamp")));
        }
        
        return observation;
    }
    
    /**
     * Convert reservation to FHIR Encounter resource
     */
    public Encounter convertToFhirEncounter(Map<String, Object> reservationData) {
        Encounter encounter = new Encounter();
        
        // Set status
        encounter.setStatus(Encounter.EncounterStatus.INPROGRESS);
        
        // Set class
        Coding encounterClass = new Coding();
        encounterClass.setSystem("http://terminology.hl7.org/CodeSystem/v3-ActCode");
        encounterClass.setCode("AMB");
        encounterClass.setDisplay("ambulatory");
        encounter.setClass_(encounterClass);
        
        // Set type (procedure type)
        if (reservationData.containsKey("procedureType")) {
            CodeableConcept type = new CodeableConcept();
            type.setText(reservationData.get("procedureType").toString());
            encounter.addType(type);
        }
        
        // Set period
        if (reservationData.containsKey("appointmentDate")) {
            Period period = new Period();
            period.setStart((Date) reservationData.get("appointmentDate"));
            encounter.setPeriod(period);
        }
        
        return encounter;
    }
    
    /**
     * Get patient data from Health Wallet and convert to FHIR Bundle
     */
    public Bundle getPatientFhirBundle(Long userId) {
        try {
            // Fetch patient data from Health Wallet
            String url = healthWalletServiceUrl + "/api/health-wallet/user/" + userId + "/complete";
            ResponseEntity<Map> response = getRestTemplate().getForEntity(url, Map.class);
            
            if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
                return null;
            }
            
            Map<String, Object> walletData = response.getBody();
            
            // Create FHIR Bundle
            Bundle bundle = new Bundle();
            bundle.setType(Bundle.BundleType.COLLECTION);
            
            // Convert patient
            Map<String, Object> patientData = new HashMap<>();
            patientData.put("userId", userId);
            if (walletData.containsKey("patientInfo")) {
                patientData.putAll((Map<String, Object>) walletData.get("patientInfo"));
            }
            
            Patient patient = convertToFhirPatient(userId, patientData);
            bundle.addEntry()
                .setResource(patient)
                .setFullUrl("Patient/" + userId);
            
            // Convert IoT observations
            if (walletData.containsKey("iotData")) {
                // Fetch IoT data
                String iotUrl = iotMonitoringServiceUrl + "/api/iot/monitoring/patient/" + userId;
                ResponseEntity<Map> iotResponse = getRestTemplate().getForEntity(iotUrl, Map.class);
                
                if (iotResponse.getStatusCode().is2xxSuccessful() && iotResponse.getBody() != null) {
                    // Convert IoT data to observations
                    // Implementation depends on IoT data structure
                }
            }
            
            return bundle;
        } catch (Exception e) {
            System.err.println("Error converting to FHIR: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Convert FHIR Bundle to JSON string
     */
    public String bundleToJson(Bundle bundle) {
        return jsonParser.encodeResourceToString(bundle);
    }
    
    /**
     * Parse FHIR JSON to Bundle
     */
    public Bundle jsonToBundle(String json) {
        return jsonParser.parseResource(Bundle.class, json);
    }
}
