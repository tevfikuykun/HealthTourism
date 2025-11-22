package com.healthtourism.telemedicineservice.controller;
import com.healthtourism.telemedicineservice.dto.TelemedicineConsultationDTO;
import com.healthtourism.telemedicineservice.entity.TelemedicineConsultation;
import com.healthtourism.telemedicineservice.service.TelemedicineConsultationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/telemedicine")
@CrossOrigin(origins = "*")
public class TelemedicineConsultationController {
    @Autowired
    private TelemedicineConsultationService telemedicineConsultationService;
    
    @PostMapping
    public ResponseEntity<?> createConsultation(@RequestBody TelemedicineConsultation consultation) {
        try {
            return ResponseEntity.ok(telemedicineConsultationService.createConsultation(consultation));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<TelemedicineConsultationDTO>> getConsultationsByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(telemedicineConsultationService.getConsultationsByUser(userId));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<TelemedicineConsultationDTO> getConsultationById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(telemedicineConsultationService.getConsultationById(id));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateStatus(@PathVariable Long id, @RequestParam String status) {
        try {
            return ResponseEntity.ok(telemedicineConsultationService.updateStatus(id, status));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}

