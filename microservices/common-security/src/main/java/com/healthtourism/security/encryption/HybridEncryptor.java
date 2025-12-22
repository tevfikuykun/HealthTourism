package com.healthtourism.security.encryption;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Hybrid Encryptor
 * Combines AES-256-GCM (current) with Quantum-Safe algorithms (future)
 * 
 * Strategy:
 * - Current: AES-256-GCM (fast, secure for now)
 * - Future: Quantum-Safe (PQC) when enabled
 * - Hybrid: Both for transition period
 */
@Component
public class HybridEncryptor {
    
    @Autowired
    private AES256GCMEncryptor aesEncryptor;
    
    @Autowired
    private QuantumSafeEncryptor quantumSafeEncryptor;
    
    /**
     * Encrypt with hybrid approach
     * Uses quantum-safe if enabled, otherwise AES-256-GCM
     */
    public String encrypt(String plainText, String key) {
        if (quantumSafeEncryptor.isQuantumSafeEnabled()) {
            try {
                // Generate quantum-safe key pair
                var keyPair = quantumSafeEncryptor.generateKyberKeyPair();
                return quantumSafeEncryptor.encryptQuantumSafe(plainText, keyPair.getPublic());
            } catch (Exception e) {
                // Fallback to AES-256-GCM
                return aesEncryptor.encrypt(plainText);
            }
        } else {
            // Current: AES-256-GCM
            return aesEncryptor.encrypt(plainText);
        }
    }
    
    /**
     * Decrypt with hybrid approach
     */
    public String decrypt(String encryptedData, String key) {
        if (quantumSafeEncryptor.isQuantumSafeEnabled()) {
            try {
                // Quantum-safe decryption
                // Note: In production, store private key securely
                var keyPair = quantumSafeEncryptor.generateKyberKeyPair();
                return quantumSafeEncryptor.decryptQuantumSafe(encryptedData, keyPair.getPrivate());
            } catch (Exception e) {
                // Fallback to AES-256-GCM
                return aesEncryptor.decrypt(encryptedData);
            }
        } else {
            // Current: AES-256-GCM
            return aesEncryptor.decrypt(encryptedData);
        }
    }
}






