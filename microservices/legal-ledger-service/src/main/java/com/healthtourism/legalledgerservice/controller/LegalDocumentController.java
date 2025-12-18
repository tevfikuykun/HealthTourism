package com.healthtourism.legalledgerservice.controller;

import com.healthtourism.legalledgerservice.entity.LegalDocument;
import com.healthtourism.legalledgerservice.service.LegalDocumentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/legal-ledger")
@CrossOrigin(origins = "*")
@Tag(name = "Legal Ledger", description = "Blockchain time-stamped legal documents (Informed Consent, Treatment Plans, etc.)")
public class LegalDocumentController {
    
    @Autowired
    private LegalDocumentService legalDocumentService;
    
    @PostMapping("/document")
    @Operation(summary = "Create and timestamp a legal document on blockchain",
               description = "Creates a time-stamped, immutable legal document")
    public ResponseEntity<LegalDocument> createDocument(@RequestBody Map<String, Object> request) {
        try {
            Long userId = Long.valueOf(request.get("userId").toString());
            Long doctorId = Long.valueOf(request.get("doctorId").toString());
            Long hospitalId = Long.valueOf(request.get("hospitalId").toString());
            Long reservationId = Long.valueOf(request.get("reservationId").toString());
            String documentType = request.get("documentType").toString();
            String title = request.get("title").toString();
            String description = request.getOrDefault("description", "").toString();
            String documentContent = request.get("documentContent").toString();
            String documentUrl = request.getOrDefault("documentUrl", "").toString();
            
            LegalDocument document = legalDocumentService.createAndTimestampDocument(
                    userId, doctorId, hospitalId, reservationId, documentType,
                    title, description, documentContent, documentUrl);
            
            return ResponseEntity.ok(document);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PostMapping("/document/{documentId}/sign")
    @Operation(summary = "Sign a legal document",
               description = "Digital signature by patient, doctor, or hospital")
    public ResponseEntity<LegalDocument> signDocument(
            @PathVariable Long documentId,
            @RequestBody Map<String, Object> request) {
        try {
            String signerType = request.get("signerType").toString(); // PATIENT, DOCTOR, HOSPITAL
            String signatureHash = request.get("signatureHash").toString();
            
            LegalDocument document = legalDocumentService.signDocument(documentId, signerType, signatureHash);
            return ResponseEntity.ok(document);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PostMapping("/document/{documentId}/verify")
    @Operation(summary = "Verify document integrity using blockchain")
    public ResponseEntity<Map<String, Object>> verifyDocument(@PathVariable Long documentId) {
        boolean isValid = legalDocumentService.verifyDocumentIntegrity(documentId);
        return ResponseEntity.ok(Map.of("documentId", documentId, "isValid", isValid));
    }
    
    @GetMapping("/document/user/{userId}")
    @Operation(summary = "Get all documents for a user")
    public ResponseEntity<List<LegalDocument>> getDocumentsByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(legalDocumentService.getDocumentsByUser(userId));
    }
    
    @GetMapping("/document/reservation/{reservationId}")
    @Operation(summary = "Get all documents for a reservation")
    public ResponseEntity<List<LegalDocument>> getDocumentsByReservation(@PathVariable Long reservationId) {
        return ResponseEntity.ok(legalDocumentService.getDocumentsByReservation(reservationId));
    }
    
    @GetMapping("/document/{id}")
    @Operation(summary = "Get document by ID")
    public ResponseEntity<LegalDocument> getDocumentById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(legalDocumentService.getDocumentById(id));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
