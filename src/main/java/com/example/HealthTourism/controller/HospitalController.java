package com.example.HealthTourism.controller;

import com.example.HealthTourism.dto.HospitalDTO;
import com.example.HealthTourism.entity.Hospital;
import com.example.HealthTourism.service.HospitalService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Hospital Controller
 * Hastane yönetimi endpoint'lerini işler
 * 
 * Hata yönetimi GlobalExceptionHandler'a devredilmiştir:
 * - HospitalNotFoundException -> 404 NOT_FOUND
 * - IllegalArgumentException -> 400 BAD_REQUEST
 * - MethodArgumentNotValidException -> 400 BAD_REQUEST (validation errors)
 * 
 * Not: Şehir ve ilçe sorguları @PathVariable kullanıyor (SEO dostu URL'ler için).
 * Birden fazla kriteri birleştirmek için /search endpoint'i daha esnek olabilir.
 */
@RestController
@RequestMapping("/api/v1/hospitals")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class HospitalController {
    
    private final HospitalService hospitalService;
    
    /**
     * Tüm aktif hastaneleri getir
     * GET /api/v1/hospitals
     */
    @GetMapping
    public ResponseEntity<List<HospitalDTO>> getAllHospitals() {
        return ResponseEntity.ok(hospitalService.getAllActiveHospitals());
    }
    
    /**
     * ID ile hastane getir
     * GET /api/v1/hospitals/{id}
     * 
     * Hata yönetimi GlobalExceptionHandler'a devredilmiştir
     */
    @GetMapping("/{id}")
    public ResponseEntity<HospitalDTO> getHospitalById(@PathVariable Long id) {
        return ResponseEntity.ok(hospitalService.getHospitalById(id));
    }
    
    /**
     * Şehre göre filtrele
     * GET /api/v1/hospitals/city/{city}
     * 
     * SEO dostu URL yapısı için @PathVariable kullanılmıştır.
     * 
     * @param city Şehir adı
     */
    @GetMapping("/city/{city}")
    public ResponseEntity<List<HospitalDTO>> getHospitalsByCity(@PathVariable String city) {
        return ResponseEntity.ok(hospitalService.getHospitalsByCity(city));
    }
    
    /**
     * İlçeye göre filtrele
     * GET /api/v1/hospitals/district/{district}
     * 
     * SEO dostu URL yapısı için @PathVariable kullanılmıştır.
     * 
     * @param district İlçe adı
     */
    @GetMapping("/district/{district}")
    public ResponseEntity<List<HospitalDTO>> getHospitalsByDistrict(@PathVariable String district) {
        return ResponseEntity.ok(hospitalService.getHospitalsByDistrict(district));
    }
    
    /**
     * Havalimanına yakın hastaneler
     * GET /api/v1/hospitals/near-airport?maxDistance=50.0
     * 
     * Sağlık turizmi için kritik: Lojistik odaklı filtreleme.
     * Varsayılan değer (50.0 km) API kullanımını kolaylaştırır.
     * 
     * @param maxDistance Maksimum mesafe (km) - varsayılan: 50.0
     */
    @GetMapping("/near-airport")
    public ResponseEntity<List<HospitalDTO>> getHospitalsNearAirport(
            @RequestParam(defaultValue = "50.0") Double maxDistance) {
        return ResponseEntity.ok(hospitalService.getHospitalsNearAirport(maxDistance));
    }
    
    /**
     * Gelişmiş arama (birden fazla kriter ile)
     * GET /api/v1/hospitals/search?city=İstanbul&maxDistance=10.0
     * 
     * Birden fazla kriteri birleştirmek için esnek bir arama endpoint'i.
     * Örnek: Hem İstanbul'da hem de havalimanına 10km mesafede hastaneler.
     * 
     * @param city Opsiyonel şehir filtresi
     * @param district Opsiyonel ilçe filtresi
     * @param maxDistance Opsiyonel maksimum havalimanı mesafesi (km)
     * @return Filtrelenmiş hastane listesi
     */
    @GetMapping("/search")
    public ResponseEntity<List<HospitalDTO>> searchHospitals(
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String district,
            @RequestParam(required = false) Double maxDistance) {
        
        // Basit implementasyon: İlk eşleşen kriteri kullan
        // Gelecekte servis katmanında daha gelişmiş bir search metodu eklenebilir
        if (city != null && !city.trim().isEmpty()) {
            List<HospitalDTO> hospitals = hospitalService.getHospitalsByCity(city);
            if (maxDistance != null) {
                // Hem şehir hem mesafe filtresi uygula
                return ResponseEntity.ok(hospitals.stream()
                        .filter(h -> h.getAirportDistance() != null && h.getAirportDistance() <= maxDistance)
                        .toList());
            }
            return ResponseEntity.ok(hospitals);
        }
        
        if (district != null && !district.trim().isEmpty()) {
            return ResponseEntity.ok(hospitalService.getHospitalsByDistrict(district));
        }
        
        if (maxDistance != null) {
            return ResponseEntity.ok(hospitalService.getHospitalsNearAirport(maxDistance));
        }
        
        // Hiçbir kriter verilmezse tüm hastaneleri döndür
        return ResponseEntity.ok(hospitalService.getAllActiveHospitals());
    }
    
    /**
     * Yeni hastane oluştur
     * POST /api/v1/hospitals
     * 
     * Future Enhancement: Consider using HospitalCreateRequestDTO instead of Hospital entity
     * to improve API security and avoid exposing internal database schema.
     * 
     * @param hospital Hastane entity'si
     * @return Oluşturulan hastane (201 CREATED)
     */
    @PostMapping
    public ResponseEntity<HospitalDTO> createHospital(@Valid @RequestBody Hospital hospital) {
        HospitalDTO created = hospitalService.createHospital(hospital);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }
}

