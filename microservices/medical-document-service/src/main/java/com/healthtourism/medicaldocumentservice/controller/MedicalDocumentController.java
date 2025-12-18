package com.healthtourism.medicaldocumentservice.controller;
import com.healthtourism.medicaldocumentservice.dto.MedicalDocumentDTO;
import com.healthtourism.medicaldocumentservice.entity.MedicalDocument;
import com.healthtourism.medicaldocumentservice.service.MedicalDocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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
    public ResponseEntity<MedicalDocumentDTO> getDocumentById(@PathVariable String id) {
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
    
    @PostMapping("/upload")
    public ResponseEntity<MedicalDocumentDTO> uploadDocument(
            @RequestParam("file") MultipartFile file,
            @RequestParam("userId") Long userId,
            @RequestParam(value = "reservationId", required = false) Long reservationId,
            @RequestParam("documentType") String documentType,
            @RequestParam(value = "description", required = false) String description) {
        try {
            MedicalDocumentDTO result = medicalDocumentService.uploadDocument(
                file, userId, reservationId, documentType, description
            );
            return ResponseEntity.ok(result);
        } catch (IOException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/{id}/download")
    public ResponseEntity<byte[]> downloadDocument(@PathVariable String id) {
        try {
            byte[] fileContent = medicalDocumentService.downloadDocument(id);
            MedicalDocumentDTO document = medicalDocumentService.getDocumentById(id);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType(document.getMimeType()));
            headers.setContentDispositionFormData("attachment", document.getFileName());
            
            return ResponseEntity.ok()
                .headers(headers)
                .body(fileContent);
        } catch (IOException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDocument(@PathVariable String id) {
        try {
            medicalDocumentService.deleteDocument(id);
            return ResponseEntity.ok().build();
        } catch (IOException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}

