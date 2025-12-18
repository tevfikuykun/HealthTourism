package com.healthtourism.privacycomplianceservice.controller;

import com.healthtourism.privacycomplianceservice.service.IPFSEncryptionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/privacy")
@CrossOrigin(origins = "*")
@Tag(name = "Privacy Compliance", description = "GDPR/HIPAA compliant IPFS encryption and key management")
public class PrivacyComplianceController {
    
    @Autowired
    private IPFSEncryptionService encryptionService;
    
    @PostMapping("/encrypt")
    @Operation(summary = "Encrypt data before IPFS storage",
               description = "GDPR/HIPAA compliant encryption with user-specific keys")
    public ResponseEntity<Map<String, Object>> encryptForIPFS(@RequestBody Map<String, Object> request) {
        try {
            String data = request.get("data").toString();
            Long userId = Long.valueOf(request.get("userId").toString());
            
            IPFSEncryptionService.EncryptionResult result = encryptionService.encryptForIPFS(data, userId);
            
            Map<String, Object> response = Map.of(
                "encryptedData", result.getEncryptedData(),
                "metadata", result.getMetadata()
            );
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PostMapping("/decrypt")
    @Operation(summary = "Decrypt data from IPFS",
               description = "Authorized decryption with GDPR/HIPAA compliance checks")
    public ResponseEntity<Map<String, Object>> decryptFromIPFS(@RequestBody Map<String, Object> request) {
        try {
            String encryptedData = request.get("encryptedData").toString();
            Long userId = Long.valueOf(request.get("userId").toString());
            Long dataOwnerId = Long.valueOf(request.get("dataOwnerId").toString());
            String accessPurpose = request.getOrDefault("accessPurpose", "TREATMENT").toString();
            
            // Verify authorization
            if (!encryptionService.verifyAccessAuthorization(userId, dataOwnerId, accessPurpose)) {
                return ResponseEntity.status(403).build(); // Forbidden
            }
            
            String decryptedData = encryptionService.decryptFromIPFS(encryptedData, userId);
            
            Map<String, Object> response = Map.of(
                "decryptedData", decryptedData
            );
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PostMapping("/verify-access")
    @Operation(summary = "Verify access authorization",
               description = "GDPR/HIPAA compliance check for data access")
    public ResponseEntity<Map<String, Object>> verifyAccess(@RequestBody Map<String, Object> request) {
        try {
            Long userId = Long.valueOf(request.get("userId").toString());
            Long dataOwnerId = Long.valueOf(request.get("dataOwnerId").toString());
            String accessPurpose = request.getOrDefault("accessPurpose", "TREATMENT").toString();
            
            boolean authorized = encryptionService.verifyAccessAuthorization(userId, dataOwnerId, accessPurpose);
            
            Map<String, Object> response = Map.of(
                "authorized", authorized,
                "userId", userId,
                "dataOwnerId", dataOwnerId,
                "accessPurpose", accessPurpose
            );
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
