package com.healthtourism.healthwalletservice.service;

import com.healthtourism.healthwalletservice.entity.HealthWallet;
import com.healthtourism.healthwalletservice.entity.TemporaryAccessToken;
import com.healthtourism.healthwalletservice.repository.TemporaryAccessTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

/**
 * Privacy Shield Service
 * Provides temporary, session-based access to health wallet data
 * Implements Zero-Knowledge Proof-like access control
 */
@Service
public class PrivacyShieldService {
    
    @Autowired
    private TemporaryAccessTokenRepository tokenRepository;
    
    @Autowired
    private HealthWalletService walletService;
    
    @Value("${privacy.compliance.service.url:http://localhost:8038}")
    private String privacyComplianceServiceUrl;
    
    /**
     * Create temporary access token when QR code is scanned
     * Token is valid for 1 hour and only for the current session
     */
    @Transactional
    public TemporaryAccessToken createTemporaryAccess(
            String qrCodeHash,
            Long authorizedUserId,
            String accessPurpose) {
        
        // Get wallet from QR code
        HealthWallet wallet = walletService.getWalletByQRCodeHash(qrCodeHash);
        
        // Revoke any existing active tokens for this wallet (one session at a time)
        revokeExistingTokens(wallet.getId());
        
        // Create new temporary access token
        TemporaryAccessToken token = new TemporaryAccessToken();
        token.setWalletId(wallet.getId());
        token.setAuthorizedUserId(authorizedUserId);
        token.setPatientUserId(wallet.getUserId());
        token.setAccessPurpose(accessPurpose);
        token.setStatus("ACTIVE");
        
        // Generate pre-signed URLs for IPFS documents (Zero-Knowledge Proof-like)
        String preSignedUrls = generatePreSignedUrls(wallet, authorizedUserId);
        token.setIpfsPreSignedUrls(preSignedUrls);
        
        token = tokenRepository.save(token);
        
        return token;
    }
    
    /**
     * Access wallet data using temporary token
     * Validates token and returns wallet data
     */
    @Transactional
    public Map<String, Object> accessWithToken(String accessToken) {
        TemporaryAccessToken token = tokenRepository.findByToken(accessToken)
                .orElseThrow(() -> new RuntimeException("Invalid access token"));
        
        // Check if token is expired
        if (token.isExpired()) {
            token.setStatus("EXPIRED");
            tokenRepository.save(token);
            throw new RuntimeException("Access token has expired");
        }
        
        // Update access statistics
        token.setAccessCount(token.getAccessCount() + 1);
        token.setLastAccessedAt(LocalDateTime.now());
        tokenRepository.save(token);
        
        // Get wallet data
        HealthWallet wallet = walletService.getWalletByUserId(token.getPatientUserId());
        
        // Return wallet data with pre-signed URLs
        Map<String, Object> walletData = new HashMap<>();
        walletData.put("walletId", wallet.getWalletId());
        walletData.put("documentCount", wallet.getDocumentCount());
        walletData.put("hasInsurance", wallet.getHasInsurance());
        walletData.put("iotDataPointCount", wallet.getIotDataPointCount());
        walletData.put("currentRecoveryScore", wallet.getCurrentRecoveryScore());
        walletData.put("legalDocumentCount", wallet.getLegalDocumentCount());
        
        // Add pre-signed URLs for IPFS documents (time-limited access)
        walletData.put("ipfsPreSignedUrls", parsePreSignedUrls(token.getIpfsPreSignedUrls()));
        walletData.put("tokenExpiresAt", token.getExpiresAt());
        walletData.put("accessPurpose", token.getAccessPurpose());
        
        return walletData;
    }
    
    /**
     * Generate pre-signed URLs for IPFS documents
     * These URLs are valid only for the current session (1 hour)
     */
    private String generatePreSignedUrls(HealthWallet wallet, Long authorizedUserId) {
        List<Map<String, Object>> preSignedUrls = new ArrayList<>();
        
        // Get IPFS document references
        if (wallet.getIpfsDocumentReferences() != null && !wallet.getIpfsDocumentReferences().isEmpty()) {
            String[] references = wallet.getIpfsDocumentReferences().split(",");
            
            for (String ipfsRef : references) {
                // Generate pre-signed URL with expiration
                Map<String, Object> urlData = new HashMap<>();
                urlData.put("ipfsReference", ipfsRef);
                urlData.put("preSignedUrl", generatePreSignedUrl(ipfsRef, authorizedUserId));
                urlData.put("expiresAt", LocalDateTime.now().plusHours(1).toString());
                preSignedUrls.add(urlData);
            }
        }
        
        return convertToJson(preSignedUrls);
    }
    
    /**
     * Generate pre-signed URL for IPFS document
     * In production, integrate with IPFS gateway (Pinata, Infura) for pre-signed URLs
     */
    private String generatePreSignedUrl(String ipfsReference, Long authorizedUserId) {
        // Generate time-limited access URL
        // Format: https://ipfs-gateway.com/ipfs/{cid}?token={temporary-token}&expires={timestamp}
        String token = UUID.randomUUID().toString().replace("-", "");
        long expiresAt = System.currentTimeMillis() + (60 * 60 * 1000); // 1 hour
        
        // In production, use actual IPFS gateway API to generate pre-signed URL
        return String.format("https://ipfs-gateway.healthtourism.com/ipfs/%s?token=%s&expires=%d&userId=%d",
                ipfsReference.replace("ipfs://", ""), token, expiresAt, authorizedUserId);
    }
    
    /**
     * Revoke existing active tokens (one session at a time)
     */
    @Transactional
    private void revokeExistingTokens(Long walletId) {
        List<TemporaryAccessToken> activeTokens = tokenRepository.findByWalletIdAndStatus(walletId, "ACTIVE");
        for (TemporaryAccessToken existingToken : activeTokens) {
            existingToken.setStatus("REVOKED");
            tokenRepository.save(existingToken);
        }
    }
    
    /**
     * Revoke token manually (patient can revoke access)
     */
    @Transactional
    public void revokeToken(String accessToken) {
        TemporaryAccessToken token = tokenRepository.findByToken(accessToken)
                .orElseThrow(() -> new RuntimeException("Token not found"));
        
        token.setStatus("REVOKED");
        tokenRepository.save(token);
    }
    
    /**
     * Parse pre-signed URLs JSON
     */
    private List<Map<String, Object>> parsePreSignedUrls(String json) {
        // Simplified parsing (in production, use ObjectMapper)
        // For now, return empty list
        return new ArrayList<>();
    }
    
    /**
     * Convert to JSON (simplified)
     */
    private String convertToJson(List<Map<String, Object>> data) {
        // Simplified JSON conversion (in production, use ObjectMapper)
        return data.toString();
    }
}
