package com.healthtourism.vault.controller;

import com.healthtourism.vault.service.VaultSecretService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Vault Controller
 * REST API for secret management
 */
@RestController
@RequestMapping("/api/vault")
public class VaultController {
    
    @Autowired
    private VaultSecretService vaultService;
    
    @GetMapping("/secrets/{key}")
    public ResponseEntity<Map<String, String>> getSecret(@PathVariable String key) {
        String value = vaultService.getSecret(key);
        return ResponseEntity.ok(Map.of("key", key, "value", value));
    }
    
    @GetMapping("/keys/aes")
    public ResponseEntity<Map<String, String>> getAESKey() {
        String key = vaultService.getAESKey();
        return ResponseEntity.ok(Map.of("key", "aes-encryption-key", "value", key));
    }
    
    @GetMapping("/keys/polygon")
    public ResponseEntity<Map<String, String>> getPolygonKey() {
        String key = vaultService.getPolygonPrivateKey();
        return ResponseEntity.ok(Map.of("key", "polygon-private-key", "value", key));
    }
    
    @PostMapping("/keys/aes/rotate")
    public ResponseEntity<Map<String, String>> rotateAESKey() {
        String newKey = vaultService.rotateAESKey();
        return ResponseEntity.ok(Map.of(
            "status", "rotated",
            "newKey", newKey,
            "message", "AES key rotated successfully. Old key preserved for decryption."
        ));
    }
    
    @PostMapping("/secrets")
    public ResponseEntity<Map<String, String>> storeSecret(
            @RequestBody Map<String, String> request) {
        String key = request.get("key");
        String value = request.get("value");
        vaultService.storeSecret(key, value);
        return ResponseEntity.ok(Map.of("status", "stored", "key", key));
    }
}



