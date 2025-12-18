package com.healthtourism.fhiradapterservice.controller;

import ca.uhn.fhir.parser.IParser;
import org.hl7.fhir.r4.model.*;
import com.healthtourism.fhiradapterservice.service.FhirConversionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/fhir")
@Tag(name = "FHIR Adapter", description = "FHIR R4 interoperability endpoints")
public class FhirAdapterController {
    
    @Autowired
    private FhirConversionService fhirConversionService;
    
    @GetMapping(value = "/Patient/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get Patient as FHIR resource", 
               description = "Retrieve patient data in FHIR R4 Patient resource format")
    public ResponseEntity<String> getPatientAsFhir(@PathVariable Long userId) {
        try {
            // Fetch patient data (simplified - should fetch from actual service)
            Map<String, Object> patientData = new HashMap<>();
            Patient patient = fhirConversionService.convertToFhirPatient(userId, patientData);
            
            // Convert to JSON
            String json = fhirConversionService.bundleToJson(
                new Bundle().addEntry().setResource(patient).getBundle());
            
            return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(json);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping(value = "/Patient/{userId}/$everything", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get Patient Everything", 
               description = "Retrieve complete patient data as FHIR Bundle (Patient + Observations + Encounters)")
    public ResponseEntity<String> getPatientEverything(@PathVariable Long userId) {
        try {
            Bundle bundle = fhirConversionService.getPatientFhirBundle(userId);
            
            if (bundle == null) {
                return ResponseEntity.notFound().build();
            }
            
            String json = fhirConversionService.bundleToJson(bundle);
            
            return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(json);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PostMapping(value = "/Observation", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Create FHIR Observation", 
               description = "Create a new observation from IoT data")
    public ResponseEntity<String> createObservation(@RequestBody Map<String, Object> iotData) {
        try {
            Observation observation = fhirConversionService.convertToFhirObservation(iotData);
            
            // Store observation (simplified - should persist)
            String json = fhirConversionService.bundleToJson(
                new Bundle().addEntry().setResource(observation).getBundle());
            
            return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(json);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping(value = "/metadata", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "FHIR CapabilityStatement", 
               description = "Get FHIR server capabilities (CapabilityStatement)")
    public ResponseEntity<String> getCapabilityStatement() {
        CapabilityStatement capabilityStatement = new CapabilityStatement();
        capabilityStatement.setStatus(Enumerations.PublicationStatus.ACTIVE);
        capabilityStatement.setKind(CapabilityStatement.CapabilityStatementKind.INSTANCE);
        capabilityStatement.setFhirVersion(Enumerations.FHIRVersion._4_0_1);
        
        // Add REST endpoint
        CapabilityStatement.CapabilityStatementRestComponent rest = 
            capabilityStatement.addRest();
        rest.setMode(CapabilityStatement.RestCapabilityMode.SERVER);
        
        // Add resource capabilities
        CapabilityStatement.CapabilityStatementRestResourceComponent patientResource = 
            rest.addResource();
        patientResource.setType("Patient");
        patientResource.addInteraction()
            .setCode(CapabilityStatement.TypeRestfulInteraction.READ);
        patientResource.addInteraction()
            .setCode(CapabilityStatement.TypeRestfulInteraction.SEARCH_TYPE);
        
        CapabilityStatement.CapabilityStatementRestResourceComponent observationResource = 
            rest.addResource();
        observationResource.setType("Observation");
        observationResource.addInteraction()
            .setCode(CapabilityStatement.TypeRestfulInteraction.CREATE);
        observationResource.addInteraction()
            .setCode(CapabilityStatement.TypeRestfulInteraction.READ);
        
        String json = fhirConversionService.bundleToJson(
            new Bundle().addEntry().setResource(capabilityStatement).getBundle());
        
        return ResponseEntity.ok()
            .contentType(MediaType.APPLICATION_JSON)
            .body(json);
    }
}
