package com.healthtourism.healthwalletservice.service;

import com.healthtourism.healthwalletservice.entity.HealthWallet;
import com.healthtourism.healthwalletservice.repository.HealthWalletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.*;

/**
 * Health Wallet Service
 * Unified wallet containing all patient health data
 */
@Service
public class HealthWalletService {
    
    @Autowired
    private HealthWalletRepository walletRepository;
    
    @Autowired
    private QRCodeService qrCodeService;
    
    @Autowired
    private PrivacyShieldService privacyShieldService;
    
    @Autowired(required = false)
    private RestTemplate restTemplate;
    
    @Value("${blockchain.service.url:http://localhost:8030}")
    private String blockchainServiceUrl;
    
    @Value("${medical.document.service.url:http://localhost:8012}")
    private String medicalDocumentServiceUrl;
    
    @Value("${iot.monitoring.service.url:http://localhost:8032}")
    private String iotMonitoringServiceUrl;
    
    @Value("${insurance.service.url:http://localhost:8018}")
    private String insuranceServiceUrl;
    
    @Value("${legal.ledger.service.url:http://localhost:8034}")
    private String legalLedgerServiceUrl;
    
    @Value("${patient.risk.scoring.service.url:http://localhost:8036}")
    private String patientRiskScoringServiceUrl;
    
    private RestTemplate getRestTemplate() {
        if (restTemplate == null) {
            restTemplate = new RestTemplate();
        }
        return restTemplate;
    }
    
    /**
     * Create or update health wallet for a user
     */
    @Transactional
    public HealthWallet createOrUpdateWallet(Long userId) {
        HealthWallet wallet = walletRepository.findByUserId(userId).orElse(null);
        
        if (wallet == null) {
            wallet = new HealthWallet();
            wallet.setUserId(userId);
            wallet.setWalletId(UUID.randomUUID().toString());
            wallet.setQrCodeHash(generateQRCodeHash(userId));
        }
        
        // Aggregate data from all services
        aggregateWalletData(wallet, userId);
        
        wallet.setLastUpdatedAt(LocalDateTime.now());
        return walletRepository.save(wallet);
    }
    
    /**
     * Aggregate data from all services
     */
    private void aggregateWalletData(HealthWallet wallet, Long userId) {
        // Get IPFS document references
        List<String> ipfsReferences = getIPFSDocumentReferences(userId);
        wallet.setIpfsDocumentReferences(String.join(",", ipfsReferences));
        wallet.setDocumentCount(ipfsReferences.size());
        
        // Get blockchain insurance policy
        Map<String, Object> insurancePolicy = getInsurancePolicy(userId);
        if (insurancePolicy != null) {
            wallet.setInsurancePolicyId(Long.valueOf(insurancePolicy.get("id").toString()));
            wallet.setInsurancePolicyHash((String) insurancePolicy.get("blockchainHash"));
            wallet.setHasInsurance(true);
        } else {
            wallet.setHasInsurance(false);
        }
        
        // Get IoT monitoring summary
        Map<String, Object> iotSummary = getIoTMonitoringSummary(userId);
        if (iotSummary != null) {
            wallet.setLatestIotDataId(Long.valueOf(iotSummary.get("latestDataId").toString()));
            wallet.setIotDataSummary(iotSummary.toString());
            wallet.setIotDataPointCount((Integer) iotSummary.get("dataPointCount"));
        }
        
        // Get latest recovery score
        Map<String, Object> recoveryScore = getLatestRecoveryScore(userId);
        if (recoveryScore != null) {
            wallet.setLatestRecoveryScoreId(Long.valueOf(recoveryScore.get("id").toString()));
            wallet.setCurrentRecoveryScore(recoveryScore.get("recoveryScore").toString());
        }
        
        // Get legal document references
        List<String> legalDocIds = getLegalDocumentReferences(userId);
        wallet.setLegalDocumentReferences(String.join(",", legalDocIds));
        wallet.setLegalDocumentCount(legalDocIds.size());
    }
    
    /**
     * Get IPFS document references
     */
    private List<String> getIPFSDocumentReferences(Long userId) {
        try {
            String url = medicalDocumentServiceUrl + "/api/medical-documents/user/" + userId;
            List<Map<String, Object>> documents = getRestTemplate().getForObject(url, List.class);
            
            List<String> references = new ArrayList<>();
            if (documents != null) {
                for (Map<String, Object> doc : documents) {
                    if (doc.containsKey("ipfsReference")) {
                        references.add((String) doc.get("ipfsReference"));
                    }
                }
            }
            return references;
        } catch (Exception e) {
            System.err.println("Failed to get IPFS document references: " + e.getMessage());
            return new ArrayList<>();
        }
    }
    
    /**
     * Get insurance policy
     */
    private Map<String, Object> getInsurancePolicy(Long userId) {
        try {
            String url = insuranceServiceUrl + "/api/insurance/user/" + userId;
            List<Map<String, Object>> policies = getRestTemplate().getForObject(url, List.class);
            
            if (policies != null && !policies.isEmpty()) {
                return policies.get(0); // Get latest policy
            }
        } catch (Exception e) {
            System.err.println("Failed to get insurance policy: " + e.getMessage());
        }
        return null;
    }
    
    /**
     * Get IoT monitoring summary
     */
    private Map<String, Object> getIoTMonitoringSummary(Long userId) {
        try {
            String url = iotMonitoringServiceUrl + "/api/iot-monitoring/user/" + userId;
            List<Map<String, Object>> iotData = getRestTemplate().getForObject(url, List.class);
            
            Map<String, Object> summary = new HashMap<>();
            if (iotData != null && !iotData.isEmpty()) {
                summary.put("latestDataId", iotData.get(0).get("id"));
                summary.put("dataPointCount", iotData.size());
                summary.put("latestTimestamp", iotData.get(0).get("recordedAt"));
            } else {
                summary.put("dataPointCount", 0);
            }
            return summary;
        } catch (Exception e) {
            System.err.println("Failed to get IoT monitoring summary: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Get latest recovery score
     */
    private Map<String, Object> getLatestRecoveryScore(Long userId) {
        try {
            // This would need reservationId, for now return null
            // In production, would query by userId and get latest reservation
            return null;
        } catch (Exception e) {
            System.err.println("Failed to get recovery score: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Get legal document references
     */
    private List<String> getLegalDocumentReferences(Long userId) {
        try {
            String url = legalLedgerServiceUrl + "/api/legal-ledger/user/" + userId;
            List<Map<String, Object>> documents = getRestTemplate().getForObject(url, List.class);
            
            List<String> references = new ArrayList<>();
            if (documents != null) {
                for (Map<String, Object> doc : documents) {
                    references.add(doc.get("id").toString());
                }
            }
            return references;
        } catch (Exception e) {
            System.err.println("Failed to get legal document references: " + e.getMessage());
            return new ArrayList<>();
        }
    }
    
    /**
     * Generate QR code hash
     */
    private String generateQRCodeHash(Long userId) {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 32);
    }
    
    /**
     * Get wallet by user ID
     */
    public HealthWallet getWalletByUserId(Long userId) {
        return walletRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Health wallet not found for user: " + userId));
    }
    
    /**
     * Get wallet by wallet ID
     */
    public HealthWallet getWalletByWalletId(String walletId) {
        return walletRepository.findByWalletId(walletId)
                .orElseThrow(() -> new RuntimeException("Health wallet not found: " + walletId));
    }
    
    /**
     * Get wallet by QR code hash
     */
    public HealthWallet getWalletByQRCodeHash(String qrCodeHash) {
        return walletRepository.findByQrCodeHash(qrCodeHash)
                .orElseThrow(() -> new RuntimeException("Health wallet not found for QR code: " + qrCodeHash));
    }
    
    /**
     * Access wallet (increment access count)
     */
    @Transactional
    public HealthWallet accessWallet(String qrCodeHash) {
        HealthWallet wallet = getWalletByQRCodeHash(qrCodeHash);
        wallet.setAccessCount(wallet.getAccessCount() + 1);
        wallet.setLastAccessedAt(LocalDateTime.now());
        return walletRepository.save(wallet);
    }
    
    /**
     * Get complete wallet data (for display)
     */
    public Map<String, Object> getCompleteWalletData(Long userId) {
        HealthWallet wallet = getWalletByUserId(userId);
        Map<String, Object> data = new HashMap<>();
        
        data.put("walletId", wallet.getWalletId());
        data.put("qrCodeHash", wallet.getQrCodeHash());
        data.put("qrCodeImage", qrCodeService.generateQRCodeBase64(wallet.getQrCodeHash()));
        data.put("documentCount", wallet.getDocumentCount());
        data.put("hasInsurance", wallet.getHasInsurance());
        data.put("insurancePolicyHash", wallet.getInsurancePolicyHash());
        data.put("iotDataPointCount", wallet.getIotDataPointCount());
        data.put("currentRecoveryScore", wallet.getCurrentRecoveryScore());
        data.put("legalDocumentCount", wallet.getLegalDocumentCount());
        data.put("lastUpdatedAt", wallet.getLastUpdatedAt());
        
        return data;
    }
    
    public PrivacyShieldService getPrivacyShieldService() {
        return privacyShieldService;
    }
}
