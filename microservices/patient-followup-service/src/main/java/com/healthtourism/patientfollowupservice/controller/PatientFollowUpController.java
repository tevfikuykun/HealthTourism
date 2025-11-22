package com.healthtourism.patientfollowupservice.controller;
import com.healthtourism.patientfollowupservice.dto.PatientFollowUpDTO;
import com.healthtourism.patientfollowupservice.entity.PatientFollowUp;
import com.healthtourism.patientfollowupservice.service.PatientFollowUpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/patient-followup")
@CrossOrigin(origins = "*")
public class PatientFollowUpController {
    @Autowired
    private PatientFollowUpService patientFollowUpService;
    
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<PatientFollowUpDTO>> getFollowUpsByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(patientFollowUpService.getFollowUpsByUser(userId));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<PatientFollowUpDTO> getFollowUpById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(patientFollowUpService.getFollowUpById(id));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @PostMapping
    public ResponseEntity<PatientFollowUpDTO> createFollowUp(@RequestBody PatientFollowUp followUp) {
        return ResponseEntity.ok(patientFollowUpService.createFollowUp(followUp));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<?> updateFollowUp(@PathVariable Long id, @RequestParam(required = false) String recoveryProgress, @RequestParam(required = false) String notes) {
        try {
            return ResponseEntity.ok(patientFollowUpService.updateFollowUp(id, recoveryProgress, notes));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}

