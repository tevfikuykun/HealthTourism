package com.healthtourism.insuranceservice.service;

import com.healthtourism.insuranceservice.entity.Insurance;
import com.healthtourism.insuranceservice.entity.InsurancePolicy;
import com.healthtourism.insuranceservice.repository.InsurancePolicyRepository;
import com.healthtourism.insuranceservice.repository.InsuranceRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Smart Medical Travel Insurance Service
 * Integrates with Blockchain Service for immutable policy storage
 */
@Service
public class SmartInsuranceService {
    
    @Autowired
    private InsurancePolicyRepository policyRepository;
    
    @Autowired
    private InsuranceRepository insuranceRepository;
    
    @Autowired(required = false)
    private RestTemplate restTemplate;
    
    @Value("${blockchain.service.url:http://localhost:8030}")
    private String blockchainServiceUrl;
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    private RestTemplate getRestTemplate() {
        if (restTemplate == null) {
            restTemplate = new RestTemplate();
        }
        return restTemplate;
    }
    
    /**
     * Create a blockchain-backed insurance policy
     */
    @Transactional
    public InsurancePolicy createSmartPolicy(
            Long userId,
            Long insuranceId,
            Long reservationId,
            LocalDateTime startDate,
            LocalDateTime endDate) {
        
        Insurance insurance = insuranceRepository.findByIdAndIsActiveTrue(insuranceId)
                .orElseThrow(() -> new RuntimeException("Insurance not found"));
        
        InsurancePolicy policy = new InsurancePolicy();
        policy.setUserId(userId);
        policy.setInsuranceId(insuranceId);
        policy.setReservationId(reservationId);
        policy.setStartDate(startDate);
        policy.setEndDate(endDate);
        policy.setCoverageAmount(insurance.getCoverageAmount());
        policy.setPremium(insurance.getPremium());
        policy.setStatus("ACTIVE");
        policy.setIsBlockchainVerified(false);
        policy.setCoverageDetails(insurance.getCoverageDetails());
        policy.setExclusions(insurance.getExclusions());
        policy.setCoversPostOpComplications(true);
        policy.setCoversEmergencyEvacuation(true);
        policy.setCoversRepatriation(true);
        policy.setCoversFollowUpCare(true);
        
        // Save policy first
        policy = policyRepository.save(policy);
        
        // Create blockchain record
        try {
            String blockchainHash = createBlockchainRecord(policy);
            policy.setBlockchainHash(blockchainHash);
            policy.setIsBlockchainVerified(true);
            policy = policyRepository.save(policy);
        } catch (Exception e) {
            // Log error but don't fail policy creation
            System.err.println("Failed to create blockchain record: " + e.getMessage());
        }
        
        return policy;
    }
    
    /**
     * Create blockchain record for insurance policy
     */
    private String createBlockchainRecord(InsurancePolicy policy) {
        try {
            Map<String, Object> policyData = new HashMap<>();
            policyData.put("policyId", policy.getId());
            policyData.put("policyNumber", policy.getPolicyNumber());
            policyData.put("userId", policy.getUserId());
            policyData.put("insuranceId", policy.getInsuranceId());
            policyData.put("reservationId", policy.getReservationId());
            policyData.put("startDate", policy.getStartDate().toString());
            policyData.put("endDate", policy.getEndDate().toString());
            policyData.put("coverageAmount", policy.getCoverageAmount().toString());
            policyData.put("premium", policy.getPremium().toString());
            policyData.put("coverageDetails", policy.getCoverageDetails());
            policyData.put("coversPostOpComplications", policy.getCoversPostOpComplications());
            policyData.put("coversEmergencyEvacuation", policy.getCoversEmergencyEvacuation());
            policyData.put("coversRepatriation", policy.getCoversRepatriation());
            policyData.put("coversFollowUpCare", policy.getCoversFollowUpCare());
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("data", policyData);
            requestBody.put("recordType", "INSURANCE_POLICY");
            requestBody.put("recordId", policy.getPolicyNumber());
            requestBody.put("userId", policy.getUserId().toString());
            requestBody.put("useIPFS", true);
            
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
            
            String url = blockchainServiceUrl + "/api/blockchain/create-with-ipfs";
            Map<String, Object> response = getRestTemplate().postForObject(url, request, Map.class);
            
            if (response != null && response.containsKey("dataHash")) {
                String hash = (String) response.get("dataHash");
                if (response.containsKey("dataReference")) {
                    policy.setBlockchainReference((String) response.get("dataReference"));
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
     * Verify policy integrity using blockchain
     */
    public boolean verifyPolicyIntegrity(Long policyId) {
        InsurancePolicy policy = policyRepository.findById(policyId)
                .orElseThrow(() -> new RuntimeException("Policy not found"));
        
        if (policy.getBlockchainHash() == null || policy.getBlockchainHash().isEmpty()) {
            return false;
        }
        
        try {
            String url = blockchainServiceUrl + "/api/blockchain/hash/" + policy.getBlockchainHash();
            Map<String, Object> record = getRestTemplate().getForObject(url, Map.class);
            return record != null;
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Get all policies for a user
     */
    public List<InsurancePolicy> getPoliciesByUser(Long userId) {
        return policyRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }
    
    /**
     * Get policy by reservation ID
     */
    public InsurancePolicy getPolicyByReservation(Long reservationId) {
        return policyRepository.findByReservationId(reservationId)
                .orElseThrow(() -> new RuntimeException("Policy not found for reservation"));
    }
    
    /**
     * Claim insurance (post-op complications)
     */
    @Transactional
    public InsurancePolicy claimInsurance(Long policyId, String claimReason, BigDecimal claimAmount) {
        InsurancePolicy policy = policyRepository.findById(policyId)
                .orElseThrow(() -> new RuntimeException("Policy not found"));
        
        if (!"ACTIVE".equals(policy.getStatus())) {
            throw new RuntimeException("Policy is not active");
        }
        
        if (LocalDateTime.now().isAfter(policy.getEndDate())) {
            throw new RuntimeException("Policy has expired");
        }
        
        // Update policy status
        policy.setStatus("CLAIMED");
        policy = policyRepository.save(policy);
        
        // Create blockchain record for claim
        try {
            Map<String, Object> claimData = new HashMap<>();
            claimData.put("policyId", policy.getId());
            claimData.put("policyNumber", policy.getPolicyNumber());
            claimData.put("claimReason", claimReason);
            claimData.put("claimAmount", claimAmount.toString());
            claimData.put("claimedAt", LocalDateTime.now().toString());
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("data", claimData);
            requestBody.put("recordType", "INSURANCE_CLAIM");
            requestBody.put("recordId", policy.getPolicyNumber() + "-CLAIM-" + System.currentTimeMillis());
            requestBody.put("userId", policy.getUserId().toString());
            requestBody.put("useIPFS", true);
            
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
            String url = blockchainServiceUrl + "/api/blockchain/create-with-ipfs";
            getRestTemplate().postForObject(url, request, Map.class);
        } catch (Exception e) {
            System.err.println("Failed to create claim blockchain record: " + e.getMessage());
        }
        
        return policy;
    }
}
