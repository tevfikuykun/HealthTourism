package com.healthtourism.healthwalletservice.controller;

import com.healthtourism.healthwalletservice.entity.HealthWallet;
import com.healthtourism.healthwalletservice.entity.TemporaryAccessToken;
import com.healthtourism.healthwalletservice.service.HealthWalletService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/health-wallet")
@CrossOrigin(origins = "*")
@Tag(name = "Health Wallet", description = "Unified health wallet with QR code access")
public class HealthWalletController {
    
    @Autowired
    private HealthWalletService walletService;
    
    @PostMapping("/create")
    @Operation(summary = "Create or update health wallet",
               description = "Aggregates all patient health data from IPFS, blockchain, IoT, etc.")
    public ResponseEntity<HealthWallet> createOrUpdateWallet(@RequestBody Map<String, Object> request) {
        try {
            Long userId = Long.valueOf(request.get("userId").toString());
            HealthWallet wallet = walletService.createOrUpdateWallet(userId);
            return ResponseEntity.ok(wallet);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/user/{userId}")
    @Operation(summary = "Get wallet by user ID")
    public ResponseEntity<HealthWallet> getWalletByUserId(@PathVariable Long userId) {
        try {
            return ResponseEntity.ok(walletService.getWalletByUserId(userId));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/wallet/{walletId}")
    @Operation(summary = "Get wallet by wallet ID")
    public ResponseEntity<HealthWallet> getWalletByWalletId(@PathVariable String walletId) {
        try {
            return ResponseEntity.ok(walletService.getWalletByWalletId(walletId));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @PostMapping("/qr/{qrCodeHash}/access")
    @Operation(summary = "Create temporary access token via QR code (Privacy Shield)",
               description = "Scan QR code to create time-limited (1 hour) access token. Only one active session at a time.")
    public ResponseEntity<Map<String, Object>> createTemporaryAccess(
            @PathVariable String qrCodeHash,
            @RequestBody Map<String, Object> request) {
        try {
            Long authorizedUserId = Long.valueOf(request.get("authorizedUserId").toString());
            String accessPurpose = request.getOrDefault("accessPurpose", "TREATMENT").toString();
            
            TemporaryAccessToken token = walletService.getPrivacyShieldService()
                    .createTemporaryAccess(qrCodeHash, authorizedUserId, accessPurpose);
            
            Map<String, Object> response = new HashMap<>();
            response.put("accessToken", token.getToken());
            response.put("expiresAt", token.getExpiresAt());
            response.put("accessPurpose", token.getAccessPurpose());
            response.put("message", "Temporary access token created. Valid for 1 hour.");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/access/{accessToken}")
    @Operation(summary = "Access wallet data using temporary token",
               description = "Use temporary access token to retrieve wallet data. Token expires after 1 hour.")
    public ResponseEntity<Map<String, Object>> accessWithToken(@PathVariable String accessToken) {
        try {
            Map<String, Object> walletData = walletService.getPrivacyShieldService()
                    .accessWithToken(accessToken);
            return ResponseEntity.ok(walletData);
        } catch (RuntimeException e) {
            return ResponseEntity.status(403).build(); // Forbidden if token invalid/expired
        }
    }
    
    @PostMapping("/access/{accessToken}/revoke")
    @Operation(summary = "Revoke temporary access token",
               description = "Patient can revoke access token to terminate session")
    public ResponseEntity<Map<String, Object>> revokeToken(@PathVariable String accessToken) {
        try {
            walletService.getPrivacyShieldService().revokeToken(accessToken);
            return ResponseEntity.ok(Map.of("message", "Access token revoked successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/user/{userId}/complete")
    @Operation(summary = "Get complete wallet data with QR code",
               description = "Returns all wallet data including QR code image")
    public ResponseEntity<Map<String, Object>> getCompleteWalletData(@PathVariable Long userId) {
        try {
            return ResponseEntity.ok(walletService.getCompleteWalletData(userId));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/{walletId}/verify-owner")
    @Operation(summary = "Verify wallet ownership (BOLA check)",
               description = "Check if user owns this wallet - used by Security Audit Service")
    public ResponseEntity<Map<String, Object>> verifyOwner(
            @PathVariable String walletId,
            @RequestParam Long userId) {
        try {
            HealthWallet wallet = walletService.getWalletByWalletId(walletId);
            boolean isOwner = wallet.getUserId().equals(userId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("isOwner", isOwner);
            response.put("walletId", walletId);
            response.put("userId", userId);
            
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("isOwner", false);
            response.put("error", "Wallet not found");
            return ResponseEntity.ok(response);
        }
    }
}
