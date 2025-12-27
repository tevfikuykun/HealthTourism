package com.example.HealthTourism.controller;

import com.example.HealthTourism.dto.ReservationDTO;
import com.example.HealthTourism.dto.ReservationRequestDTO;
import com.example.HealthTourism.service.ReservationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Reservation Controller
 * Rezervasyon yönetimi endpoint'lerini işler
 * 
 * Hata yönetimi GlobalExceptionHandler'a devredilmiştir:
 * - ReservationNotFoundException -> 404 NOT_FOUND
 * - ReservationConflictException -> 409 CONFLICT
 * - InvalidDateRangeException -> 400 BAD_REQUEST
 * - DoctorHospitalMismatchException -> 400 BAD_REQUEST
 * - IllegalArgumentException -> 400 BAD_REQUEST
 * - MethodArgumentNotValidException -> 400 BAD_REQUEST (validation errors)
 * 
 * Security Note: Gerçek dünya senaryosunda /user/{userId} endpoint'inde
 * Spring Security Principal veya Authentication nesnesi kullanılarak
 * kullanıcının sadece kendi rezervasyonlarını görebilmesi sağlanmalıdır.
 */
@RestController
@RequestMapping("/api/v1/reservations")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ReservationController {
    
    private final ReservationService reservationService;
    
    /**
     * Yeni rezervasyon oluştur
     * POST /api/v1/reservations
     * 
     * ReservationRequestDTO kullanımı karmaşık JSON yapısını düzenli bir nesne olarak
     * servis katmanına paslamayı sağlar.
     * 
     * @param request Rezervasyon isteği DTO'su
     * @return Oluşturulan rezervasyon (201 CREATED)
     */
    @PostMapping
    public ResponseEntity<ReservationDTO> createReservation(@Valid @RequestBody ReservationRequestDTO request) {
        ReservationDTO created = reservationService.createReservation(request);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }
    
    /**
     * Kullanıcının rezervasyonlarını getir
     * GET /api/v1/reservations/user/{userId}
     * 
     * Security: Gerçek dünya senaryosunda Spring Security Principal kullanılarak
     * kullanıcının sadece kendi rezervasyonlarını görebilmesi sağlanmalıdır.
     * Örnek: Principal'den userId alınarak yetki kontrolü yapılabilir.
     * 
     * @param userId Kullanıcı ID'si
     * @return Kullanıcının rezervasyon listesi
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ReservationDTO>> getReservationsByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(reservationService.getReservationsByUser(userId));
    }
    
    /**
     * Rezervasyon numarası ile rezervasyon getir
     * GET /api/v1/reservations/number/{reservationNumber}
     * 
     * Hata yönetimi GlobalExceptionHandler'a devredilmiştir
     * 
     * @param reservationNumber Rezervasyon numarası (örn: HT-2025-A12B)
     * @return Rezervasyon detayları
     */
    @GetMapping("/number/{reservationNumber}")
    public ResponseEntity<ReservationDTO> getReservationByNumber(@PathVariable String reservationNumber) {
        return ResponseEntity.ok(reservationService.getReservationByNumber(reservationNumber));
    }
    
    /**
     * Rezervasyon durumunu güncelle
     * PUT /api/v1/reservations/{id}/status?status=CONFIRMED
     * 
     * RESTful standartlarına uygun: @PutMapping ve @RequestParam kullanımı.
     * 
     * Dinamik Statü Yönetimi: Servis katmanında statü geçişleri doğrulanır.
     * Örneğin "COMPLETED" durumundan "PENDING" durumuna geçiş gibi mantıksal
     * hatalar engellenir. (Ödendi -> Beklemede gibi geçişler servis katmanında kontrol edilir)
     * 
     * @param id Rezervasyon ID'si
     * @param status Yeni durum (PENDING, CONFIRMED, CANCELLED, COMPLETED)
     * @return Güncellenmiş rezervasyon
     */
    @PutMapping("/{id}/status")
    public ResponseEntity<ReservationDTO> updateReservationStatus(
            @PathVariable Long id,
            @RequestParam String status) {
        ReservationDTO updated = reservationService.updateReservationStatus(id, status);
        return ResponseEntity.ok(updated);
    }
}

