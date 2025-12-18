package com.healthtourism.blockchain.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.ResourceAccessException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * IPFS Health Check Service
 * Monitors IPFS Gateway connectivity and availability
 * Supports Pinata, Infura, and public IPFS gateways
 */
@Service
public class IPFSHealthCheckService {
    
    @Autowired
    private IPFSService ipfsService;
    
    @Value("${ipfs.gateway.url:https://ipfs.io/ipfs/}")
    private String ipfsGatewayUrl;
    
    @Value("${ipfs.api.url:http://localhost:5001}")
    private String ipfsApiUrl;
    
    @Value("${ipfs.health.check.enabled:true}")
    private boolean healthCheckEnabled;
    
    @Value("${ipfs.health.check.timeout.seconds:5}")
    private int healthCheckTimeoutSeconds;
    
    private RestTemplate restTemplate;
    
    /**
     * Initialize RestTemplate with timeout configuration
     */
    private RestTemplate getRestTemplate() {
        if (restTemplate == null) {
            SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
            factory.setConnectTimeout(healthCheckTimeoutSeconds * 1000);
            factory.setReadTimeout(healthCheckTimeoutSeconds * 1000);
            restTemplate = new RestTemplate(factory);
        }
        return restTemplate;
    }
    
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
     * Perform comprehensive IPFS health check
     */
    public Map<String, Object> performHealthCheck() {
        Map<String, Object> healthReport = new HashMap<>();
        healthReport.put("timestamp", LocalDateTime.now());
        healthReport.put("gatewayUrl", ipfsGatewayUrl);
        healthReport.put("apiUrl", ipfsApiUrl);
        
        try {
            // Test 1: Gateway connectivity
            boolean gatewayAccessible = checkGatewayConnectivity();
            healthReport.put("gatewayAccessible", gatewayAccessible);
            
            // Test 2: API connectivity (if local node)
            boolean apiAccessible = checkAPIConnectivity();
            healthReport.put("apiAccessible", apiAccessible);
            
            // Test 3: Upload test (small file) - only if IPFS service available
            boolean uploadTest = false;
            if (ipfsService != null) {
                uploadTest = performUploadTest();
            }
            healthReport.put("uploadTest", uploadTest);
            
            // Test 4: Download test (retrieve test file) - only if IPFS service available
            boolean downloadTest = false;
            if (ipfsService != null) {
                downloadTest = performDownloadTest();
            }
            healthReport.put("downloadTest", downloadTest);
            
            // Determine overall health status
            HealthStatus status = determineHealthStatus(gatewayAccessible, apiAccessible, uploadTest, downloadTest);
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
     * Check IPFS Gateway connectivity
     */
    public boolean checkGatewayConnectivity() {
        try {
            // Try to access a known IPFS hash (QmYwAPJzv5CZsnA625s3Xf2nemtYgPpHdWEz79ojWnPbdG - IPFS logo)
            String testHash = "QmYwAPJzv5CZsnA625s3Xf2nemtYgPpHdWEz79ojWnPbdG";
            String testUrl = ipfsGatewayUrl + testHash;
            
            CompletableFuture<Boolean> future = CompletableFuture.supplyAsync(() -> {
                try {
                    getRestTemplate().getForObject(testUrl, String.class);
                    return true;
                } catch (Exception e) {
                    return false;
                }
            });
            
            return future.get(healthCheckTimeoutSeconds, TimeUnit.SECONDS);
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Check IPFS API connectivity (for local node)
     */
    public boolean checkAPIConnectivity() {
        try {
            // Check if local IPFS node is running
            String apiUrl = ipfsApiUrl + "/api/v0/version";
            
            CompletableFuture<Boolean> future = CompletableFuture.supplyAsync(() -> {
                try {
                    Map<String, Object> response = getRestTemplate().getForObject(apiUrl, Map.class);
                    return response != null;
                } catch (ResourceAccessException e) {
                    // Local node not running - this is OK if using public gateway
                    return false;
                } catch (Exception e) {
                    return false;
                }
            });
            
            return future.get(healthCheckTimeoutSeconds, TimeUnit.SECONDS);
        } catch (Exception e) {
            // API not accessible - OK if using public gateway
            return false;
        }
    }
    
    /**
     * Perform upload test
     */
    public boolean performUploadTest() {
        if (ipfsService == null) {
            return false; // IPFS service not available
        }
        
        try {
            String testData = "IPFS Health Check Test - " + System.currentTimeMillis();
            String cid = ipfsService.uploadToIPFS(testData);
            return cid != null && !cid.isEmpty();
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Perform download test
     */
    public boolean performDownloadTest() {
        if (ipfsService == null) {
            return false; // IPFS service not available
        }
        
        try {
            // Use a known test CID or upload test data first
            String testData = "Download Test";
            String cid = ipfsService.uploadToIPFS(testData);
            
            // Try to retrieve it
            String retrieved = ipfsService.retrieveFromIPFS(cid);
            return retrieved != null && !retrieved.isEmpty();
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Determine overall health status
     */
    private HealthStatus determineHealthStatus(boolean gatewayAccessible, boolean apiAccessible, 
                                               boolean uploadTest, boolean downloadTest) {
        // If IPFS service not available, check only gateway
        if (ipfsService == null) {
            return gatewayAccessible ? HealthStatus.DEGRADED : HealthStatus.UNHEALTHY;
        }
        
        if (gatewayAccessible && uploadTest && downloadTest) {
            return HealthStatus.HEALTHY;
        } else if (gatewayAccessible && (uploadTest || downloadTest)) {
            return HealthStatus.DEGRADED;
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
     * Scheduled health check - runs every 5 minutes
     */
    @Scheduled(fixedRate = 300000) // 5 minutes
    public void scheduledHealthCheck() {
        if (healthCheckEnabled) {
            performHealthCheck();
        }
    }
    
    /**
     * Check if IPFS is ready for production use
     */
    public boolean isProductionReady() {
        if (ipfsService == null) {
            return false; // IPFS service not configured
        }
        
        Map<String, Object> healthReport = performHealthCheck();
        return (Boolean) healthReport.getOrDefault("healthy", false);
    }
}
