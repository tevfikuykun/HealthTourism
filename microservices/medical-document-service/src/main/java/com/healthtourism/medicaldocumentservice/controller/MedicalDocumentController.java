package com.healthtourism.medicaldocumentservice.controller;
import com.healthtourism.medicaldocumentservice.dto.MedicalDocumentDTO;
import com.healthtourism.medicaldocumentservice.entity.MedicalDocument;
import com.healthtourism.medicaldocumentservice.service.MedicalDocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/medical-documents")
@CrossOrigin(origins = "*")
public class MedicalDocumentController {
    @Autowired
    private MedicalDocumentService medicalDocumentService;
    
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<MedicalDocumentDTO>> getDocumentsByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(medicalDocumentService.getDocumentsByUser(userId));
    }
    
    @GetMapping("/reservation/{reservationId}")
    public ResponseEntity<List<MedicalDocumentDTO>> getDocumentsByReservation(@PathVariable Long reservationId) {
        return ResponseEntity.ok(medicalDocumentService.getDocumentsByReservation(reservationId));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<MedicalDocumentDTO> getDocumentById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(medicalDocumentService.getDocumentById(id));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @PostMapping
    public ResponseEntity<MedicalDocumentDTO> createDocument(@RequestBody MedicalDocument document) {
        return ResponseEntity.ok(medicalDocumentService.createDocument(document));
    }
}

