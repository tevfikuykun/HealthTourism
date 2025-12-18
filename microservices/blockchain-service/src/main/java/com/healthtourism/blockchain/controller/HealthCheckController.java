package com.healthtourism.blockchain.controller;

import com.healthtourism.blockchain.service.BlockchainHealthCheckService;
import com.healthtourism.blockchain.service.IPFSHealthCheckService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/health")
@CrossOrigin(origins = "*")
public class HealthCheckController {
    
    @Autowired(required = false)
    private IPFSHealthCheckService ipfsHealthCheckService;
    
    @Autowired
    private BlockchainHealthCheckService blockchainHealthCheckService;
    
    /**
     * IPFS Gateway Health Check
     */
    @GetMapping("/ipfs")
    public ResponseEntity<Map<String, Object>> checkIPFSHealth() {
        if (ipfsHealthCheckService == null) {
            return ResponseEntity.status(503).body(Map.of(
                "error", "IPFS Health Check Service not available",
                "healthy", false
            ));
        }
        
        Map<String, Object> healthReport = ipfsHealthCheckService.performHealthCheck();
        boolean healthy = (Boolean) healthReport.getOrDefault("healthy", false);
        
        if (healthy) {
            return ResponseEntity.ok(healthReport);
        } else {
            return ResponseEntity.status(503).body(healthReport); // Service Unavailable
        }
    }
    
    /**
     * Blockchain Network Health Check
     */
    @GetMapping("/blockchain")
    public ResponseEntity<Map<String, Object>> checkBlockchainHealth() {
        Map<String, Object> healthReport = blockchainHealthCheckService.performHealthCheck();
        boolean healthy = (Boolean) healthReport.getOrDefault("healthy", false);
        
        if (healthy) {
            return ResponseEntity.ok(healthReport);
        } else {
            return ResponseEntity.status(503).body(healthReport); // Service Unavailable
        }
    }
    
    /**
     * Combined Health Check (IPFS + Blockchain)
     */
    @GetMapping("/combined")
    public ResponseEntity<Map<String, Object>> checkCombinedHealth() {
        Map<String, Object> ipfsHealth;
        if (ipfsHealthCheckService != null) {
            ipfsHealth = ipfsHealthCheckService.performHealthCheck();
        } else {
            ipfsHealth = Map.of("healthy", false, "error", "IPFS Health Check Service not available");
        }
        
        Map<String, Object> blockchainHealth = blockchainHealthCheckService.performHealthCheck();
        
        boolean ipfsHealthy = (Boolean) ipfsHealth.getOrDefault("healthy", false);
        boolean blockchainHealthy = (Boolean) blockchainHealth.getOrDefault("healthy", false);
        boolean overallHealthy = ipfsHealthy && blockchainHealthy;
        
        Map<String, Object> combined = Map.of(
            "ipfs", ipfsHealth,
            "blockchain", blockchainHealth,
            "overallHealthy", overallHealthy
        );
        
        if (overallHealthy) {
            return ResponseEntity.ok(combined);
        } else {
            return ResponseEntity.status(503).body(combined);
        }
    }
    
    /**
     * IPFS Gateway Connectivity Test
     */
    @GetMapping("/ipfs/connectivity")
    public ResponseEntity<Map<String, Object>> testIPFSConnectivity() {
        if (ipfsHealthCheckService == null) {
            return ResponseEntity.status(503).body(Map.of(
                "error", "IPFS Health Check Service not available",
                "gatewayAccessible", false,
                "apiAccessible", false
            ));
        }
        
        boolean gatewayAccessible = ipfsHealthCheckService.checkGatewayConnectivity();
        boolean apiAccessible = ipfsHealthCheckService.checkAPIConnectivity();
        
        return ResponseEntity.ok(Map.of(
            "gatewayAccessible", gatewayAccessible,
            "apiAccessible", apiAccessible,
            "timestamp", java.time.LocalDateTime.now()
        ));
    }
    
    /**
     * Blockchain Chain Integrity Check
     */
    @GetMapping("/blockchain/integrity")
    public ResponseEntity<Map<String, Object>> checkChainIntegrity() {
        boolean integrity = blockchainHealthCheckService.checkChainIntegrity();
        long chainLength = blockchainHealthCheckService.getChainLength();
        
        return ResponseEntity.ok(Map.of(
            "integrity", integrity,
            "chainLength", chainLength,
            "timestamp", java.time.LocalDateTime.now()
        ));
    }
    
    /**
     * Production Readiness Check
     */
    @GetMapping("/production-ready")
    public ResponseEntity<Map<String, Object>> checkProductionReadiness() {
        boolean ipfsReady = ipfsHealthCheckService != null && ipfsHealthCheckService.isProductionReady();
        boolean blockchainReady = blockchainHealthCheckService.isProductionReady();
        Map<String, Object> networkConfig = blockchainHealthCheckService.validateNetworkConfiguration();
        
        Map<String, Object> readiness = Map.of(
            "ipfsReady", ipfsReady,
            "blockchainReady", blockchainReady,
            "overallReady", ipfsReady && blockchainReady,
            "networkConfiguration", networkConfig,
            "recommendations", getProductionRecommendations(ipfsReady, blockchainReady, networkConfig)
        );
        
        return ResponseEntity.ok(readiness);
    }
    
    private String getProductionRecommendations(boolean ipfsReady, boolean blockchainReady, 
                                                 Map<String, Object> networkConfig) {
        StringBuilder recommendations = new StringBuilder();
        
        if (!ipfsReady) {
            recommendations.append("IPFS Gateway is not ready. Check connectivity and configuration. ");
        }
        
        if (!blockchainReady) {
            recommendations.append("Blockchain network is not ready. Verify chain integrity and database connectivity. ");
        }
        
        if ("mainnet".equals(networkConfig.get("networkType"))) {
            recommendations.append("Mainnet configuration detected. Ensure all security measures are in place before production deployment. ");
        }
        
        if (ipfsReady && blockchainReady) {
            recommendations.append("All systems are ready for production deployment.");
        }
        
        return recommendations.toString();
    }
}
