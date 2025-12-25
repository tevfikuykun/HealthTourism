package com.healthtourism.jpa.controller.example;

import com.healthtourism.jpa.config.SecurityContextHelper;
import com.healthtourism.jpa.config.ratelimit.RateLimited;
import com.healthtourism.jpa.config.versioning.ApiVersion;
import com.healthtourism.jpa.dto.ApiResponseWrapper;
import com.healthtourism.jpa.dto.appointment.AppointmentResponseDTO;
import com.healthtourism.jpa.dto.appointment.CreateAppointmentRequestDTO;
import com.healthtourism.jpa.dto.appointment.UpdateAppointmentRequestDTO;
import com.healthtourism.jpa.exception.ResourceNotFoundException;
import com.healthtourism.jpa.service.PatientServiceInterface;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * Reservation Controller - Professional Example
 * 
 * This is an example of a professional controller implementation with:
 * - Standard response wrapper (ApiResponseWrapper)
 * - Swagger/OpenAPI documentation
 * - Security context usage (no IDOR vulnerability)
 * - API versioning (/api/v1/)
 * - Rate limiting (@RateLimited annotation)
 * - Centralized error handling (EnhancedGlobalExceptionHandler)
 * 
 * NOTE: This is an example/template. Actual implementation should be in your service module.
 */
@RestController
@RequestMapping("/api/v1/reservations")
@Tag(name = "Reservation", description = "Rezervasyon yönetimi uç noktaları")
@Validated
@SecurityRequirement(name = "bearer-jwt")
@ApiVersion("v1")
@RateLimited(requestsPerMinute = 60) // Default rate limit for all endpoints in this controller
public class ReservationControllerExample {
    
    // NOTE: These should be injected from your service module
    // @Autowired
    // private ReservationServiceInterface reservationService;
    
    // @Autowired
    // private ReservationMapper reservationMapper;
    
    /**
     * Create a new reservation
     * 
     * Security: User ID is extracted from SecurityContext, not from request.
     * This prevents IDOR (Insecure Direct Object Reference) vulnerabilities.
     */
    @PostMapping
    @Operation(
        summary = "Yeni rezervasyon oluştur",
        description = "Hastane, doktor ve konaklama bilgilerini içeren yeni bir rezervasyon oluşturur. " +
                     "Kullanıcı bilgisi güvenli bir şekilde authentication token'dan alınır."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201",
            description = "Rezervasyon başarıyla oluşturuldu",
            content = @Content(schema = @Schema(implementation = ApiResponseWrapper.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Geçersiz giriş verisi veya randevu çakışması",
            content = @Content(schema = @Schema(implementation = com.healthtourism.jpa.exception.ErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Kimlik doğrulama gerekli",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "409",
            description = "Randevu çakışması - Bu saatte başka bir randevu mevcut",
            content = @Content(schema = @Schema(implementation = com.healthtourism.jpa.exception.ErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "429",
            description = "Rate limit aşıldı - Çok fazla istek gönderildi",
            content = @Content
        )
    })
    @PreAuthorize("hasRole('USER') or hasRole('PATIENT')")
    @RateLimited(requestsPerMinute = 30, errorMessage = "Rezervasyon oluşturma limiti aşıldı. Lütfen daha sonra tekrar deneyin.")
    public ResponseEntity<ApiResponseWrapper<AppointmentResponseDTO>> createReservation(
            @Valid @RequestBody CreateAppointmentRequestDTO request) {
        
        // SECURITY: Get user ID from SecurityContext, NOT from request
        UUID currentUserId = SecurityContextHelper.getCurrentUserId();
        
        // Map Request DTO to Entity and create reservation
        // Appointment appointment = reservationMapper.toEntity(request);
        // appointment.setPatientId(currentUserId); // Set from SecurityContext
        // Appointment created = reservationService.createAppointment(appointment);
        // AppointmentResponseDTO response = reservationMapper.toResponseDTO(created);
        
        // For example purposes, returning mock response
        AppointmentResponseDTO mockResponse = new AppointmentResponseDTO();
        
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(ApiResponseWrapper.success("Rezervasyon başarıyla oluşturuldu", mockResponse));
    }
    
    /**
     * Get current user's reservations
     * 
     * Security: User ID is extracted from SecurityContext.
     * This ensures users can only see their own reservations.
     */
    @GetMapping("/my-reservations")
    @Operation(
        summary = "Kullanıcının rezervasyonlarını listele",
        description = "Giriş yapmış kullanıcının tüm rezervasyonlarını listeler. " +
                     "Kullanıcı bilgisi authentication token'dan güvenli bir şekilde alınır."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Rezervasyonlar başarıyla getirildi",
            content = @Content(schema = @Schema(implementation = ApiResponseWrapper.class))
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Kimlik doğrulama gerekli",
            content = @Content
        )
    })
    @PreAuthorize("hasRole('USER') or hasRole('PATIENT')")
    public ResponseEntity<ApiResponseWrapper<List<AppointmentResponseDTO>>> getMyReservations() {
        
        // SECURITY: Get user ID from SecurityContext, NOT from path variable
        UUID currentUserId = SecurityContextHelper.getCurrentUserId();
        
        // List<Appointment> appointments = reservationService.getReservationsByUserId(currentUserId);
        // List<AppointmentResponseDTO> response = reservationMapper.toResponseDTOList(appointments);
        
        // For example purposes, returning mock response
        List<AppointmentResponseDTO> mockResponse = List.of();
        
        return ResponseEntity.ok(
            ApiResponseWrapper.success("Rezervasyonlar başarıyla getirildi", mockResponse)
        );
    }
    
    /**
     * Get reservation by ID
     * 
     * Security: Ensures user can only access their own reservations.
     */
    @GetMapping("/{id}")
    @Operation(
        summary = "Rezervasyon detaylarını getir",
        description = "Belirtilen ID'ye sahip rezervasyonun detaylarını getirir. " +
                     "Kullanıcı sadece kendi rezervasyonlarını görüntüleyebilir."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Rezervasyon bulundu",
            content = @Content(schema = @Schema(implementation = ApiResponseWrapper.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Rezervasyon bulunamadı",
            content = @Content(schema = @Schema(implementation = com.healthtourism.jpa.exception.ErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Bu rezervasyona erişim yetkiniz yok",
            content = @Content(schema = @Schema(implementation = com.healthtourism.jpa.exception.ErrorResponse.class))
        )
    })
    @PreAuthorize("hasRole('USER') or hasRole('PATIENT')")
    public ResponseEntity<ApiResponseWrapper<AppointmentResponseDTO>> getReservationById(
            @Parameter(description = "Rezervasyon ID", required = true, example = "123e4567-e89b-12d3-a456-426614174000")
            @PathVariable UUID id) {
        
        UUID currentUserId = SecurityContextHelper.getCurrentUserId();
        
        // Appointment appointment = reservationService.findById(id)
        //     .orElseThrow(() -> ResourceNotFoundException.appointmentNotFound(id));
        
        // SECURITY: Verify ownership
        // if (!appointment.getPatientId().equals(currentUserId) && !SecurityContextHelper.hasRole("ROLE_ADMIN")) {
        //     throw new AccessDeniedException("Bu rezervasyona erişim yetkiniz yok");
        // }
        
        // AppointmentResponseDTO response = reservationMapper.toResponseDTO(appointment);
        
        // For example purposes, returning mock response
        AppointmentResponseDTO mockResponse = new AppointmentResponseDTO();
        
        return ResponseEntity.ok(
            ApiResponseWrapper.success("Rezervasyon başarıyla getirildi", mockResponse)
        );
    }
    
    /**
     * Update reservation
     * 
     * Security: Ensures user can only update their own reservations.
     */
    @PutMapping("/{id}")
    @Operation(
        summary = "Rezervasyon güncelle",
        description = "Mevcut bir rezervasyonu günceller. " +
                     "Kullanıcı sadece kendi rezervasyonlarını güncelleyebilir."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Rezervasyon başarıyla güncellendi",
            content = @Content(schema = @Schema(implementation = ApiResponseWrapper.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Rezervasyon bulunamadı",
            content = @Content(schema = @Schema(implementation = com.healthtourism.jpa.exception.ErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Bu rezervasyonu güncelleme yetkiniz yok",
            content = @Content(schema = @Schema(implementation = com.healthtourism.jpa.exception.ErrorResponse.class))
        )
    })
    @PreAuthorize("hasRole('USER') or hasRole('PATIENT')")
    public ResponseEntity<ApiResponseWrapper<AppointmentResponseDTO>> updateReservation(
            @Parameter(description = "Rezervasyon ID", required = true)
            @PathVariable UUID id,
            @Valid @RequestBody UpdateAppointmentRequestDTO request) {
        
        UUID currentUserId = SecurityContextHelper.getCurrentUserId();
        
        // Appointment existing = reservationService.findById(id)
        //     .orElseThrow(() -> ResourceNotFoundException.appointmentNotFound(id));
        
        // SECURITY: Verify ownership
        // if (!existing.getPatientId().equals(currentUserId) && !SecurityContextHelper.hasRole("ROLE_ADMIN")) {
        //     throw new AccessDeniedException("Bu rezervasyonu güncelleme yetkiniz yok");
        // }
        
        // reservationMapper.updateEntityFromRequest(request, existing);
        // Appointment updated = reservationService.updateAppointment(existing);
        // AppointmentResponseDTO response = reservationMapper.toResponseDTO(updated);
        
        // For example purposes, returning mock response
        AppointmentResponseDTO mockResponse = new AppointmentResponseDTO();
        
        return ResponseEntity.ok(
            ApiResponseWrapper.success("Rezervasyon başarıyla güncellendi", mockResponse)
        );
    }
    
    /**
     * Cancel reservation
     * 
     * Security: Ensures user can only cancel their own reservations.
     */
    @DeleteMapping("/{id}")
    @Operation(
        summary = "Rezervasyonu iptal et",
        description = "Belirtilen rezervasyonu iptal eder (soft delete). " +
                     "Kullanıcı sadece kendi rezervasyonlarını iptal edebilir."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Rezervasyon başarıyla iptal edildi"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Rezervasyon bulunamadı"
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Bu rezervasyonu iptal etme yetkiniz yok"
        )
    })
    @PreAuthorize("hasRole('USER') or hasRole('PATIENT')")
    public ResponseEntity<ApiResponseWrapper<Void>> cancelReservation(
            @Parameter(description = "Rezervasyon ID", required = true)
            @PathVariable UUID id) {
        
        UUID currentUserId = SecurityContextHelper.getCurrentUserId();
        
        // Appointment appointment = reservationService.findById(id)
        //     .orElseThrow(() -> ResourceNotFoundException.appointmentNotFound(id));
        
        // SECURITY: Verify ownership
        // if (!appointment.getPatientId().equals(currentUserId) && !SecurityContextHelper.hasRole("ROLE_ADMIN")) {
        //     throw new AccessDeniedException("Bu rezervasyonu iptal etme yetkiniz yok");
        // }
        
        // reservationService.cancelAppointment(id);
        
        return ResponseEntity.ok(
            ApiResponseWrapper.success("Rezervasyon başarıyla iptal edildi", null)
        );
    }
}

