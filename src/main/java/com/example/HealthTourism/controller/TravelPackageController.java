package com.example.HealthTourism.controller;

import com.example.HealthTourism.dto.TravelPackageDTO;
import com.example.HealthTourism.service.TravelPackageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Travel Package Controller
 * Paket tur yönetimi endpoint'lerini işler
 * 
 * Hata yönetimi GlobalExceptionHandler'a devredilmiştir:
 * - TravelPackageNotFoundException -> 404 NOT_FOUND
 * - IllegalArgumentException -> 400 BAD_REQUEST
 * 
 * Performance: Servis katmanında JOIN FETCH veya Eager Loading kullanılarak
 * N+1 query problemi önlenmiştir. Paketler genellikle çok sayıda ilişkili veri
 * (Doktor, Hastane, Otel) içerdiği için bu optimizasyon kritiktir.
 * 
 * DTO Kullanımı: TravelPackageDTO kullanarak servis katmanından veri dönmesi,
 * frontend tarafına sadece ihtiyaç duyulan temiz verinin gitmesini sağlar.
 */
@RestController
@RequestMapping("/api/v1/packages")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class TravelPackageController {
    
    private final TravelPackageService travelPackageService;
    
    /**
     * Tüm aktif paketleri getir
     * GET /api/v1/packages
     * 
     * Performance: Servis katmanında JOIN FETCH kullanılarak ilişkili veriler
     * (Doktor, Hastane, Otel) tek sorguda yüklenir.
     */
    @GetMapping
    public ResponseEntity<List<TravelPackageDTO>> getAllPackages() {
        return ResponseEntity.ok(travelPackageService.getAllActivePackages());
    }
    
    /**
     * Hastane bazlı filtreleme
     * GET /api/v1/packages/hospital/{hospitalId}
     * 
     * Filtreleme Esnekliği: Hastane bazlı ayrıştırma kullanıcı deneyimi açısından isabetlidir.
     * 
     * @param hospitalId Hastane ID'si
     */
    @GetMapping("/hospital/{hospitalId}")
    public ResponseEntity<List<TravelPackageDTO>> getPackagesByHospital(@PathVariable Long hospitalId) {
        return ResponseEntity.ok(travelPackageService.getPackagesByHospital(hospitalId));
    }
    
    /**
     * Paket türü bazlı filtreleme
     * GET /api/v1/packages/type/{packageType}
     * 
     * Filtreleme Esnekliği: Paket türü bazlı ayrıştırma (Örn: Basic, Standard, Premium, VIP)
     * kullanıcı deneyimi açısından isabetlidir. İleride tıbbi alan bazlı (Diş, Estetik, Onkoloji)
     * filtreleme de eklenebilir.
     * 
     * @param packageType Paket türü (Basic, Standard, Premium, VIP)
     */
    @GetMapping("/type/{packageType}")
    public ResponseEntity<List<TravelPackageDTO>> getPackagesByType(@PathVariable String packageType) {
        return ResponseEntity.ok(travelPackageService.getPackagesByType(packageType));
    }
    
    /**
     * ID ile paket getir
     * GET /api/v1/packages/{id}
     * 
     * Hata yönetimi GlobalExceptionHandler'a devredilmiştir.
     * 
     * Performance: Servis katmanında JOIN FETCH ile ilişkili veriler optimize edilmiş şekilde yüklenir.
     */
    @GetMapping("/{id}")
    public ResponseEntity<TravelPackageDTO> getPackageById(@PathVariable Long id) {
        return ResponseEntity.ok(travelPackageService.getPackageById(id));
    }
}

