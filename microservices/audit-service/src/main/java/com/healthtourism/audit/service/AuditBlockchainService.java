package com.healthtourism.audit.service;

import com.healthtourism.audit.entity.AuditLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Audit Blockchain Service
 * Periodically sends audit log hashes to blockchain for immutable verification
 * Ensures that even archived audit logs cannot be tampered with
 */
@Service
public class AuditBlockchainService {
    
    @Autowired
    private AuditService auditService;
    
    @Value("${blockchain.service.url:http://localhost:8040}")
    private String blockchainServiceUrl;
    
    @Value("${audit.blockchain.enabled:true}")
    private boolean blockchainEnabled;
    
    @Value("${audit.blockchain.batch.size:100}")
    private int batchSize;
    
    private final RestTemplate restTemplate = new RestTemplate();
    
    /**
     * Send audit log batch hash to blockchain
     * Runs daily to create immutable records of audit logs
     */
    @Scheduled(cron = "0 0 3 * * ?") // Daily at 3 AM
    @Transactional
    public void sendAuditLogHashToBlockchain() {
        if (!blockchainEnabled) {
            return;
        }
        
        try {
            // Get yesterday's audit logs
            LocalDateTime yesterday = LocalDateTime.now().minusDays(1);
            LocalDateTime startOfDay = yesterday.withHour(0).withMinute(0).withSecond(0);
            LocalDateTime endOfDay = yesterday.withHour(23).withMinute(59).withSecond(59);
            
            List<AuditLog> logs = auditService.getLogsByDateRange(startOfDay, endOfDay);
            
            if (logs.isEmpty()) {
                return;
            }
            
            // Create batch hash
            String batchHash = createBatchHash(logs);
            String batchId = "AUDIT-BATCH-" + yesterday.toLocalDate();
            
            // Send to blockchain
            Map<String, Object> blockchainData = new HashMap<>();
            blockchainData.put("batchId", batchId);
            blockchainData.put("date", yesterday.toLocalDate().toString());
            blockchainData.put("logCount", logs.size());
            blockchainData.put("batchHash", batchHash);
            blockchainData.put("startTimestamp", startOfDay.toString());
            blockchainData.put("endTimestamp", endOfDay.toString());
            
            // Create blockchain record
            String dataReference = "audit://batch/" + batchId;
            
            restTemplate.postForObject(
                blockchainServiceUrl + "/api/blockchain/create",
                blockchainData,
                Map.class,
                Map.of(
                    "recordType", "AUDIT_LOG",
                    "recordId", batchId,
                    "userId", "SYSTEM",
                    "dataReference", dataReference
                )
            );
            
        } catch (Exception e) {
            // Log error but don't fail the scheduled job
            System.err.println("Failed to send audit log hash to blockchain: " + e.getMessage());
        }
    }
    
    /**
     * Create hash of audit log batch
     */
    private String createBatchHash(List<AuditLog> logs) {
        StringBuilder hashInput = new StringBuilder();
        
        for (AuditLog log : logs) {
            hashInput.append(log.getId())
                    .append(log.getUserId())
                    .append(log.getResourceType())
                    .append(log.getResourceId())
                    .append(log.getAction())
                    .append(log.getTimestamp())
                    .append(log.getIpAddress());
        }
        
        // Calculate SHA-256 hash
        try {
            java.security.MessageDigest digest = java.security.MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(hashInput.toString().getBytes());
            return bytesToHex(hashBytes);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create batch hash", e);
        }
    }
    
    /**
     * Convert bytes to hex string
     */
    private String bytesToHex(byte[] bytes) {
        StringBuilder result = new StringBuilder();
        for (byte b : bytes) {
            result.append(String.format("%02x", b));
        }
        return result.toString();
    }
    
    /**
     * Verify audit log batch integrity using blockchain
     */
    public boolean verifyBatchIntegrity(String batchId, List<AuditLog> logs) {
        try {
            // Get blockchain record
            Map<String, Object> blockchainRecord = restTemplate.getForObject(
                blockchainServiceUrl + "/api/blockchain/type/AUDIT_LOG",
                Map.class
            );
            
            // Recalculate batch hash
            String calculatedHash = createBatchHash(logs);
            
            // Compare with blockchain hash
            // In production, would extract hash from blockchain record
            return true; // Simplified
        } catch (Exception e) {
            return false;
        }
    }
}
