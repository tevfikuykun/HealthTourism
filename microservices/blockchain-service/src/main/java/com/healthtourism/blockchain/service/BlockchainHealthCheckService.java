package com.healthtourism.blockchain.service;

import com.healthtourism.blockchain.entity.BlockchainRecord;
import com.healthtourism.blockchain.repository.BlockchainRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Blockchain Health Check Service
 * Monitors blockchain network connectivity and integrity
 * Supports testnet and mainnet validation
 */
@Service
public class BlockchainHealthCheckService {
    
    @Autowired
    private BlockchainService blockchainService;
    
    @Autowired
    private BlockchainRecordRepository recordRepository;
    
    @Value("${blockchain.network.type:testnet}")
    private String networkType; // testnet, mainnet
    
    @Value("${blockchain.health.check.enabled:true}")
    private boolean healthCheckEnabled;
    
    @Value("${blockchain.health.check.test.record:true}")
    private boolean createTestRecord;
    
    private HealthStatus lastHealthStatus = HealthStatus.UNKNOWN;
    private LocalDateTime lastCheckTime;
    private String lastErrorMessage;
    
    public enum HealthStatus {
        HEALTHY,
        DEGRADED,
        UNHEALTHY,
        UNKNOWN
    }
    
    /**
     * Perform comprehensive blockchain health check
     */
    public Map<String, Object> performHealthCheck() {
        Map<String, Object> healthReport = new HashMap<>();
        healthReport.put("timestamp", LocalDateTime.now());
        healthReport.put("networkType", networkType);
        
        try {
            // Test 1: Chain integrity
            boolean chainIntegrity = checkChainIntegrity();
            healthReport.put("chainIntegrity", chainIntegrity);
            
            // Test 2: Database connectivity
            boolean dbConnectivity = checkDatabaseConnectivity();
            healthReport.put("databaseConnectivity", dbConnectivity);
            
            // Test 3: Block creation test
            boolean blockCreationTest = performBlockCreationTest();
            healthReport.put("blockCreationTest", blockCreationTest);
            
            // Test 4: Hash verification test
            boolean hashVerificationTest = performHashVerificationTest();
            healthReport.put("hashVerificationTest", hashVerificationTest);
            
            // Test 5: Chain length check
            long chainLength = getChainLength();
            healthReport.put("chainLength", chainLength);
            
            // Test 6: Latest block check
            BlockchainRecord latestBlock = getLatestBlock();
            healthReport.put("latestBlockExists", latestBlock != null);
            if (latestBlock != null) {
                healthReport.put("latestBlockHash", latestBlock.getBlockHash());
                healthReport.put("latestBlockIndex", latestBlock.getBlockIndex());
            }
            
            // Determine overall health status
            HealthStatus status = determineHealthStatus(
                chainIntegrity, dbConnectivity, blockCreationTest, hashVerificationTest
            );
            healthReport.put("status", status.toString());
            healthReport.put("healthy", status == HealthStatus.HEALTHY);
            
            lastHealthStatus = status;
            lastCheckTime = LocalDateTime.now();
            lastErrorMessage = null;
            
        } catch (Exception e) {
            healthReport.put("status", HealthStatus.UNHEALTHY.toString());
            healthReport.put("healthy", false);
            healthReport.put("error", e.getMessage());
            lastHealthStatus = HealthStatus.UNHEALTHY;
            lastErrorMessage = e.getMessage();
        }
        
        return healthReport;
    }
    
    /**
     * Check blockchain chain integrity
     */
    public boolean checkChainIntegrity() {
        try {
            return blockchainService.verifyChainIntegrity();
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Check database connectivity
     */
    public boolean checkDatabaseConnectivity() {
        try {
            recordRepository.count();
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Perform block creation test
     */
    @Transactional
    public boolean performBlockCreationTest() {
        if (!createTestRecord) {
            return true; // Skip if test records disabled
        }
        
        try {
            Map<String, Object> testData = new HashMap<>();
            testData.put("test", true);
            testData.put("timestamp", LocalDateTime.now().toString());
            testData.put("purpose", "health_check");
            
            String testRecordId = "HEALTH_CHECK_" + System.currentTimeMillis();
            BlockchainRecord testBlock = blockchainService.createBlock(
                testData,
                BlockchainRecord.RecordType.MEDICAL_TREATMENT,
                testRecordId,
                "SYSTEM",
                "test://health-check"
            );
            
            // Verify block was created
            return testBlock != null && testBlock.getBlockHash() != null;
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Perform hash verification test
     */
    public boolean performHashVerificationTest() {
        try {
            // Verify hash calculation works correctly
            String testData = "health_check_test_" + System.currentTimeMillis();
            String calculatedHash = blockchainService.calculateDataHash(testData);
            return calculatedHash != null && calculatedHash.length() == 64; // SHA-256 produces 64 char hex
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Get chain length
     */
    public long getChainLength() {
        try {
            return recordRepository.count();
        } catch (Exception e) {
            return 0;
        }
    }
    
    /**
     * Get latest block
     */
    private BlockchainRecord getLatestBlock() {
        try {
            return recordRepository.findTopByOrderByBlockIndexDesc().orElse(null);
        } catch (Exception e) {
            return null;
        }
    }
    
    /**
     * Determine overall health status
     */
    private HealthStatus determineHealthStatus(boolean chainIntegrity, boolean dbConnectivity,
                                               boolean blockCreationTest, boolean hashVerificationTest) {
        if (chainIntegrity && dbConnectivity && hashVerificationTest) {
            if (blockCreationTest) {
                return HealthStatus.HEALTHY;
            } else {
                return HealthStatus.DEGRADED; // Can read but can't write
            }
        } else {
            return HealthStatus.UNHEALTHY;
        }
    }
    
    /**
     * Get last health status
     */
    public HealthStatus getLastHealthStatus() {
        return lastHealthStatus;
    }
    
    /**
     * Get last check time
     */
    public LocalDateTime getLastCheckTime() {
        return lastCheckTime;
    }
    
    /**
     * Get last error message
     */
    public String getLastErrorMessage() {
        return lastErrorMessage;
    }
    
    /**
     * Scheduled health check - runs every 10 minutes
     */
    @Scheduled(fixedRate = 600000) // 10 minutes
    public void scheduledHealthCheck() {
        if (healthCheckEnabled) {
            performHealthCheck();
        }
    }
    
    /**
     * Check if blockchain is ready for production use
     */
    public boolean isProductionReady() {
        Map<String, Object> healthReport = performHealthCheck();
        return (Boolean) healthReport.getOrDefault("healthy", false);
    }
    
    /**
     * Validate network configuration
     */
    public Map<String, Object> validateNetworkConfiguration() {
        Map<String, Object> validation = new HashMap<>();
        validation.put("networkType", networkType);
        validation.put("isTestnet", "testnet".equalsIgnoreCase(networkType));
        validation.put("isMainnet", "mainnet".equalsIgnoreCase(networkType));
        validation.put("recommendation", getNetworkRecommendation());
        return validation;
    }
    
    private String getNetworkRecommendation() {
        if ("mainnet".equalsIgnoreCase(networkType)) {
            return "Mainnet configuration detected. Ensure all security measures are in place.";
        } else {
            return "Testnet configuration detected. Safe for development and testing.";
        }
    }
}
