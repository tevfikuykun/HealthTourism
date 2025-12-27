package com.example.HealthTourism.controller;

import com.example.HealthTourism.dto.TransferServiceDTO;
import com.example.HealthTourism.service.TransferServiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * Transfer Service Controller
 * Transfer hizmeti yönetimi endpoint'lerini işler
 * 
 * Hata yönetimi GlobalExceptionHandler'a devredilmiştir:
 * - TransferNotFoundException -> 404 NOT_FOUND
 * - IllegalArgumentException -> 400 BAD_REQUEST
 * 
 * Not: Servis katmanında TransferServiceService ismi kullanılıyor.
 * İlerleyen süreçte bunu TransferService olarak refactor etmek kod okunabilirliğini artırır.
 */
@RestController
@RequestMapping("/api/v1/transfers")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class TransferServiceController {
    
    private final TransferServiceService transferServiceService;
    
    /**
     * Tüm müsait transfer hizmetlerini getir
     * GET /api/v1/transfers
     */
    @GetMapping
    public ResponseEntity<List<TransferServiceDTO>> getAllTransfers() {
        return ResponseEntity.ok(transferServiceService.getAllAvailableTransfers());
    }
    
    /**
     * Servis tipine göre filtrele
     * GET /api/v1/transfers/type/{serviceType}
     * 
     * @param serviceType Servis tipi (örn: "Airport-Hospital", "Airport-Hotel", "Hotel-Hospital")
     */
    @GetMapping("/type/{serviceType}")
    public ResponseEntity<List<TransferServiceDTO>> getTransfersByType(@PathVariable String serviceType) {
        return ResponseEntity.ok(transferServiceService.getTransfersByType(serviceType));
    }
    
    /**
     * Maksimum fiyata göre filtrele
     * GET /api/v1/transfers/price?maxPrice=500
     * 
     * @RequestParam kullanımı frontend tarafında bir fiyat slider'ı (kaydırıcı)
     * yapmayı oldukça kolaylaştırır.
     * 
     * @param maxPrice Maksimum fiyat (varsayılan: 500)
     */
    @GetMapping("/price")
    public ResponseEntity<List<TransferServiceDTO>> getTransfersByPrice(
            @RequestParam(defaultValue = "500") BigDecimal maxPrice) {
        return ResponseEntity.ok(transferServiceService.getTransfersByPrice(maxPrice));
    }
    
    /**
     * ID ile transfer hizmeti getir
     * GET /api/v1/transfers/{id}
     * 
     * Hata yönetimi GlobalExceptionHandler'a devredilmiştir
     */
    @GetMapping("/{id}")
    public ResponseEntity<TransferServiceDTO> getTransferById(@PathVariable Long id) {
        return ResponseEntity.ok(transferServiceService.getTransferById(id));
    }
}

