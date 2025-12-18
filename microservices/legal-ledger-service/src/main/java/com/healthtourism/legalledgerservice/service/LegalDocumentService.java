package com.healthtourism.legalledgerservice.service;

import com.healthtourism.legalledgerservice.entity.LegalDocument;
import com.healthtourism.legalledgerservice.repository.LegalDocumentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Legal Document Service
 * Manages blockchain time-stamped legal documents
 */
@Service
public class LegalDocumentService {
    
    @Autowired
    private LegalDocumentRepository legalDocumentRepository;
    
    @Autowired(required = false)
    private RestTemplate restTemplate;
    
    @Value("${blockchain.service.url:http://localhost:8030}")
    private String blockchainServiceUrl;
    
    private RestTemplate getRestTemplate() {
        if (restTemplate == null) {
            restTemplate = new RestTemplate();
        }
        return restTemplate;
    }
    
    /**
     * Create and timestamp a legal document on blockchain
     */
    @Transactional
    public LegalDocument createAndTimestampDocument(
            Long userId,
            Long doctorId,
            Long hospitalId,
            Long reservationId,
            String documentType,
            String title,
            String description,
            String documentContent,
            String documentUrl) {
        
        LegalDocument document = new LegalDocument();
        document.setUserId(userId);
        document.setDoctorId(doctorId);
        document.setHospitalId(hospitalId);
        document.setReservationId(reservationId);
        document.setDocumentType(documentType);
        document.setTitle(title);
        document.setDescription(description);
        document.setDocumentContent(documentContent);
        document.setDocumentUrl(documentUrl);
        document.setStatus("DRAFT");
        
        // Save document first
        document = legalDocumentRepository.save(document);
        
        // Create blockchain record with timestamp
        try {
            String blockchainHash = createBlockchainRecord(document);
            document.setBlockchainHash(blockchainHash);
            document.setTimestampedAt(LocalDateTime.now());
            document.setIsBlockchainVerified(true);
            document.setStatus("PENDING_SIGNATURE");
            document = legalDocumentRepository.save(document);
        } catch (Exception e) {
            System.err.println("Failed to create blockchain record: " + e.getMessage());
        }
        
        return document;
    }
    
    /**
     * Sign document (patient, doctor, or hospital)
     */
    @Transactional
    public LegalDocument signDocument(
            Long documentId,
            String signerType, // PATIENT, DOCTOR, HOSPITAL
            String signatureHash) {
        
        LegalDocument document = legalDocumentRepository.findById(documentId)
                .orElseThrow(() -> new RuntimeException("Legal document not found"));
        
        LocalDateTime now = LocalDateTime.now();
        
        switch (signerType) {
            case "PATIENT":
                document.setPatientSigned(true);
                document.setPatientSignedAt(now);
                document.setPatientSignatureHash(signatureHash);
                break;
            case "DOCTOR":
                document.setDoctorSigned(true);
                document.setDoctorSignedAt(now);
                document.setDoctorSignatureHash(signatureHash);
                break;
            case "HOSPITAL":
                document.setHospitalSigned(true);
                document.setHospitalSignedAt(now);
                document.setHospitalSignatureHash(signatureHash);
                break;
        }
        
        // Update status if all parties signed
        if (document.getPatientSigned() && document.getDoctorSigned() && document.getHospitalSigned()) {
            document.setStatus("SIGNED");
            // Create blockchain record for signature
            createSignatureBlockchainRecord(document);
        }
        
        return legalDocumentRepository.save(document);
    }
    
    /**
     * Verify document integrity using blockchain
     */
    public boolean verifyDocumentIntegrity(Long documentId) {
        LegalDocument document = legalDocumentRepository.findById(documentId)
                .orElseThrow(() -> new RuntimeException("Legal document not found"));
        
        if (document.getBlockchainHash() == null || document.getBlockchainHash().isEmpty()) {
            return false;
        }
        
        try {
            String url = blockchainServiceUrl + "/api/blockchain/hash/" + document.getBlockchainHash();
            Map<String, Object> record = getRestTemplate().getForObject(url, Map.class);
            
            if (record != null) {
                document.setIsVerified(true);
                document.setVerifiedAt(LocalDateTime.now());
                legalDocumentRepository.save(document);
                return true;
            }
        } catch (Exception e) {
            System.err.println("Failed to verify document: " + e.getMessage());
        }
        
        return false;
    }
    
    /**
     * Create blockchain record for document
     */
    private String createBlockchainRecord(LegalDocument document) {
        try {
            Map<String, Object> documentData = new HashMap<>();
            documentData.put("documentId", document.getId());
            documentData.put("userId", document.getUserId());
            documentData.put("doctorId", document.getDoctorId());
            documentData.put("hospitalId", document.getHospitalId());
            documentData.put("reservationId", document.getReservationId());
            documentData.put("documentType", document.getDocumentType());
            documentData.put("title", document.getTitle());
            documentData.put("description", document.getDescription());
            documentData.put("documentContent", document.getDocumentContent());
            documentData.put("documentUrl", document.getDocumentUrl());
            documentData.put("timestampedAt", LocalDateTime.now().toString());
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("data", documentData);
            requestBody.put("recordType", "LEGAL_DOCUMENT");
            requestBody.put("recordId", "DOC-" + document.getId());
            requestBody.put("userId", document.getUserId().toString());
            requestBody.put("useIPFS", true);
            
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
            String url = blockchainServiceUrl + "/api/blockchain/create-with-ipfs";
            Map<String, Object> response = getRestTemplate().postForObject(url, request, Map.class);
            
            if (response != null && response.containsKey("dataHash")) {
                String hash = (String) response.get("dataHash");
                if (response.containsKey("dataReference")) {
                    document.setBlockchainReference((String) response.get("dataReference"));
                }
                return hash;
            }
        } catch (Exception e) {
            System.err.println("Error creating blockchain record: " + e.getMessage());
            throw new RuntimeException("Failed to create blockchain record", e);
        }
        
        return null;
    }
    
    /**
     * Create blockchain record for signatures
     */
    private void createSignatureBlockchainRecord(LegalDocument document) {
        try {
            Map<String, Object> signatureData = new HashMap<>();
            signatureData.put("documentId", document.getId());
            signatureData.put("blockchainHash", document.getBlockchainHash());
            signatureData.put("patientSigned", document.getPatientSigned());
            signatureData.put("patientSignedAt", document.getPatientSignedAt() != null ? document.getPatientSignedAt().toString() : null);
            signatureData.put("doctorSigned", document.getDoctorSigned());
            signatureData.put("doctorSignedAt", document.getDoctorSignedAt() != null ? document.getDoctorSignedAt().toString() : null);
            signatureData.put("hospitalSigned", document.getHospitalSigned());
            signatureData.put("hospitalSignedAt", document.getHospitalSignedAt() != null ? document.getHospitalSignedAt().toString() : null);
            signatureData.put("signedAt", LocalDateTime.now().toString());
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("data", signatureData);
            requestBody.put("recordType", "LEGAL_DOCUMENT_SIGNATURE");
            requestBody.put("recordId", "DOC-SIG-" + document.getId());
            requestBody.put("userId", document.getUserId().toString());
            requestBody.put("useIPFS", true);
            
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
            String url = blockchainServiceUrl + "/api/blockchain/create-with-ipfs";
            getRestTemplate().postForObject(url, request, Map.class);
        } catch (Exception e) {
            System.err.println("Failed to create signature blockchain record: " + e.getMessage());
        }
    }
    
    public List<LegalDocument> getDocumentsByUser(Long userId) {
        return legalDocumentRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }
    
    public List<LegalDocument> getDocumentsByReservation(Long reservationId) {
        return legalDocumentRepository.findByReservationIdOrderByCreatedAtDesc(reservationId);
    }
    
    public LegalDocument getDocumentById(Long id) {
        return legalDocumentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Legal document not found"));
    }
}
