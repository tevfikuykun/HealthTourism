package com.example.HealthTourism.controller;

import com.example.HealthTourism.dto.ReviewDTO;
import com.example.HealthTourism.dto.ReviewRequestDTO;
import com.example.HealthTourism.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Review Controller
 * Yorum yönetimi endpoint'lerini işler
 * 
 * Hata yönetimi GlobalExceptionHandler'a devredilmiştir:
 * - DuplicateReviewException -> 409 CONFLICT
 * - UnverifiedReviewException -> 403 FORBIDDEN
 * - InvalidRatingException -> 400 BAD_REQUEST
 * - MethodArgumentNotValidException -> 400 BAD_REQUEST (validation errors)
 * 
 * Ayrıştırma (Polymorphism): Doktor ve hastane yorumları ayrı endpoint'lerde tutulur.
 * Bu sayede her iki varlığın yorum mantığı ileride birbirinden tamamen farklılaştırılabilir
 * (Örn: Hastane için temizlik puanı, doktor için iletişim puanı gibi).
 * 
 * Güvenlik: @PostMapping metodlarında userId parametresini dışarıdan almak bir güvenlik açığıdır.
 * Gerçek dünya senaryosunda userId bilgisi Spring Security context'inden (JWT üzerinden)
 * otomatik alınmalıdır. Principal veya Authentication nesnesi kullanılmalıdır.
 */
@RestController
@RequestMapping("/api/v1/reviews")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ReviewController {
    
    private final ReviewService reviewService;
    
    /**
     * Doktor yorumlarını getir
     * GET /api/v1/reviews/doctor/{doctorId}
     * 
     * Sadece onaylanmış yorumlar gösterilir (moderasyon sistemi).
     * 
     * @param doctorId Doktor ID'si
     * @return Onaylanmış yorum listesi
     */
    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<List<ReviewDTO>> getReviewsByDoctor(@PathVariable Long doctorId) {
        return ResponseEntity.ok(reviewService.getReviewsByDoctor(doctorId));
    }

    /**
     * Hastane yorumlarını getir
     * GET /api/v1/reviews/hospital/{hospitalId}
     * 
     * Sadece onaylanmış yorumlar gösterilir (moderasyon sistemi).
     * 
     * @param hospitalId Hastane ID'si
     * @return Onaylanmış yorum listesi
     */
    @GetMapping("/hospital/{hospitalId}")
    public ResponseEntity<List<ReviewDTO>> getReviewsByHospital(@PathVariable Long hospitalId) {
        return ResponseEntity.ok(reviewService.getReviewsByHospital(hospitalId));
    }
    
    /**
     * Doktor için yorum oluştur
     * POST /api/v1/reviews/doctor
     * 
     * ReviewRequestDTO kullanımı kurumsal standartlara uygundur ve gelecekte
     * yoruma fotoğraf ekleme gibi özellikler geldiğinde esneklik sağlar.
     * 
     * Güvenlik: userId alanı gerçek dünya senaryosunda Principal'dan alınmalıdır.
     * Örnek: Principal principal parametresi eklenip, principal.getName() ile userId alınabilir.
     * 
     * @param request Yorum isteği DTO'su (userId, doctorId, rating, comment içerir)
     * @return Oluşturulan yorum (201 CREATED)
     */
    @PostMapping("/doctor")
    public ResponseEntity<ReviewDTO> createDoctorReview(@Valid @RequestBody ReviewRequestDTO request) {
        // Güvenlik: Gerçek dünya senaryosunda userId Principal'dan alınmalı
        // Long userId = getUserIdFromPrincipal(principal);
        ReviewDTO created = reviewService.createReview(
                request.getUserId(),
                request.getDoctorId(),
                request.getRating(),
                request.getComment()
        );
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    /**
     * Hastane için yorum oluştur
     * POST /api/v1/reviews/hospital
     * 
     * ReviewRequestDTO kullanımı kurumsal standartlara uygundur ve gelecekte
     * yoruma fotoğraf ekleme gibi özellikler geldiğinde esneklik sağlar.
     * 
     * Güvenlik: userId alanı gerçek dünya senaryosunda Principal'dan alınmalıdır.
     * Örnek: Principal principal parametresi eklenip, principal.getName() ile userId alınabilir.
     * 
     * @param request Yorum isteği DTO'su (userId, hospitalId, rating, comment içerir)
     * @return Oluşturulan yorum (201 CREATED)
     */
    @PostMapping("/hospital")
    public ResponseEntity<ReviewDTO> createHospitalReview(@Valid @RequestBody ReviewRequestDTO request) {
        // Güvenlik: Gerçek dünya senaryosunda userId Principal'dan alınmalı
        // Long userId = getUserIdFromPrincipal(principal);
        ReviewDTO created = reviewService.createHospitalReview(
                request.getUserId(),
                request.getHospitalId(),
                request.getRating(),
                request.getComment()
        );
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }
}

