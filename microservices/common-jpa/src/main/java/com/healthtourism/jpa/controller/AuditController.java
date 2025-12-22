package com.healthtourism.jpa.controller;

import com.healthtourism.jpa.service.PatientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Audit Controller
 * Hibernate Envers audit history endpoints
 */
@RestController
@RequestMapping("/api/audit")
public class AuditController {
    
    @Autowired
    private PatientService patientService;
    
    /**
     * Get audit history for patient
     * Shows who changed what and when
     */
    @GetMapping("/patients/{patientId}/history")
    public ResponseEntity<List<Object[]>> getPatientAuditHistory(
            @PathVariable Long patientId) {
        
        List<Object[]> history = patientService.getAuditHistory(patientId);
        return ResponseEntity.ok(history);
    }
    
    /**
     * Get patient at specific revision
     * Time-travel to see patient data at any point in time
     */
    @GetMapping("/patients/{patientId}/revision/{revision}")
    public ResponseEntity<Map<String, Object>> getPatientAtRevision(
            @PathVariable Long patientId,
            @PathVariable Number revision) {
        
        var patient = patientService.getPatientAtRevision(patientId, revision);
        return ResponseEntity.ok(Map.of(
            "patient", patient,
            "revision", revision
        ));
    }
}






