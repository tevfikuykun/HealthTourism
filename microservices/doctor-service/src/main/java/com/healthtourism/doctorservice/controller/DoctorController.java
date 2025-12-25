package com.healthtourism.doctorservice.controller;

import com.healthtourism.doctorservice.dto.DoctorCreateRequest;
import com.healthtourism.doctorservice.dto.DoctorResponseDTO;
import com.healthtourism.doctorservice.dto.DoctorUpdateRequest;
import com.healthtourism.doctorservice.service.DoctorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;

import java.util.List;

/**
 * Doctor Controller - Professional Implementation
 * 
 * Best Practices Applied:
 * - DTO pattern (no entity leakage)
 * - Constructor injection (no field injection)
 * - Global exception handling (no try-catch in controller)
 * - API versioning (/api/v1/)
 * - Bean validation (@Valid)
 * - Security annotations (@PreAuthorize)
 * - Comprehensive Swagger documentation
 * - Logging for critical operations
 * - Secure CORS configuration
 */
@RestController
@RequestMapping("/api/v1/doctors")
@Tag(name = "Doctor", description = "Doktor yönetimi API'leri")
@Validated
@RequiredArgsConstructor // Constructor injection for all dependencies
@CrossOrigin(origins = "${app.cors.origins:http://localhost:3000}") // Configurable CORS
@SecurityRequirement(name = "bearer-jwt") // JWT security requirement for Swagger
public class DoctorController {
    
    private static final Logger logger = LoggerFactory.getLogger(DoctorController.class);
    
    private final DoctorService doctorService;
    
    @Value("${app.cors.origins:http://localhost:3000}")
    private String corsOrigins;
    
    /**
     * Get doctors by hospital ID with pagination
     */
    @GetMapping("/hospital/{hospitalId}")
    @Operation(
        summary = "Hastaneye göre doktorları listele",
        description = "Belirtilen hastanede bulunan müsait doktorları sayfalı olarak listeler. " +
                     "Pagination parametreleri: page (0-based), size (default: 20), sort (field,direction)"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Doktorlar başarıyla getirildi",
            content = @Content(schema = @Schema(implementation = DoctorResponseDTO.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Geçersiz hastane ID",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Hastane bulunamadı",
            content = @Content
        )
    })
    public ResponseEntity<Page<DoctorResponseDTO>> getDoctorsByHospital(
            @Parameter(description = "Hastane ID", required = true)
            @PathVariable Long hospitalId,
            @Parameter(description = "Pagination parameters")
            @PageableDefault(size = 20, sort = "rating", direction = org.springframework.data.domain.Sort.Direction.DESC) Pageable pageable) {
        
        logger.info("Getting doctors for hospital ID: {} (page: {}, size: {})", 
            hospitalId, pageable.getPageNumber(), pageable.getPageSize());
        Page<DoctorResponseDTO> doctors = doctorService.getDoctorsByHospital(hospitalId, pageable);
        logger.debug("Found {} doctors for hospital ID: {}", doctors.getTotalElements(), hospitalId);
        return ResponseEntity.ok(doctors);
    }
    
    /**
     * Get doctors by hospital ID (without pagination - for backward compatibility)
     */
    @GetMapping("/hospital/{hospitalId}/all")
    @Operation(
        summary = "Hastaneye göre tüm doktorları listele (sayfalama olmadan)",
        description = "Belirtilen hastanede bulunan tüm müsait doktorları listeler. " +
                     "Büyük veri setleri için /hospital/{hospitalId} endpoint'ini kullanın."
    )
    public ResponseEntity<List<DoctorResponseDTO>> getDoctorsByHospitalAll(
            @Parameter(description = "Hastane ID", required = true)
            @PathVariable Long hospitalId) {
        
        logger.info("Getting all doctors for hospital ID: {}", hospitalId);
        List<DoctorResponseDTO> doctors = doctorService.getDoctorsByHospital(hospitalId);
        logger.debug("Found {} doctors for hospital ID: {}", doctors.size(), hospitalId);
        return ResponseEntity.ok(doctors);
    }
    
    /**
     * Get doctors by specialization with pagination
     */
    @GetMapping("/specialization/{specialization}")
    @Operation(
        summary = "Uzmanlık alanına göre doktorları listele",
        description = "Belirtilen uzmanlık alanındaki doktorları rating'e göre sıralı şekilde sayfalı olarak listeler. " +
                     "Pagination parametreleri: page (0-based), size (default: 20), sort (field,direction)"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Doktorlar başarıyla getirildi",
            content = @Content(schema = @Schema(implementation = DoctorResponseDTO.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Geçersiz uzmanlık alanı",
            content = @Content
        )
    })
    public ResponseEntity<Page<DoctorResponseDTO>> getDoctorsBySpecialization(
            @Parameter(description = "Uzmanlık alanı", required = true)
            @PathVariable String specialization,
            @Parameter(description = "Pagination parameters")
            @PageableDefault(size = 20, sort = "rating", direction = org.springframework.data.domain.Sort.Direction.DESC) Pageable pageable) {
        
        logger.info("Getting doctors for specialization: {} (page: {}, size: {})", 
            specialization, pageable.getPageNumber(), pageable.getPageSize());
        Page<DoctorResponseDTO> doctors = doctorService.getDoctorsBySpecialization(specialization, pageable);
        logger.debug("Found {} doctors for specialization: {}", doctors.getTotalElements(), specialization);
        return ResponseEntity.ok(doctors);
    }
    
    /**
     * Get doctors by specialization (without pagination - for backward compatibility)
     */
    @GetMapping("/specialization/{specialization}/all")
    @Operation(
        summary = "Uzmanlık alanına göre tüm doktorları listele (sayfalama olmadan)",
        description = "Belirtilen uzmanlık alanındaki tüm doktorları listeler. " +
                     "Büyük veri setleri için /specialization/{specialization} endpoint'ini kullanın."
    )
    public ResponseEntity<List<DoctorResponseDTO>> getDoctorsBySpecializationAll(
            @Parameter(description = "Uzmanlık alanı", required = true)
            @PathVariable String specialization) {
        
        logger.info("Getting all doctors for specialization: {}", specialization);
        List<DoctorResponseDTO> doctors = doctorService.getDoctorsBySpecialization(specialization);
        logger.debug("Found {} doctors for specialization: {}", doctors.size(), specialization);
        return ResponseEntity.ok(doctors);
    }
    
    /**
     * Get doctor by ID
     * 
     * Note: No try-catch block - GlobalExceptionHandler handles ResourceNotFoundException
     */
    @GetMapping("/{id}")
    @Operation(
        summary = "ID'ye göre doktor getir",
        description = "Belirtilen ID'ye sahip doktorun detaylarını getirir"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Doktor başarıyla getirildi",
            content = @Content(schema = @Schema(implementation = DoctorResponseDTO.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Doktor bulunamadı",
            content = @Content
        )
    })
    public ResponseEntity<DoctorResponseDTO> getDoctorById(
            @Parameter(description = "Doktor ID", required = true)
            @PathVariable Long id) {
        
        logger.info("Getting doctor by ID: {}", id);
        DoctorResponseDTO doctor = doctorService.getDoctorById(id);
        logger.debug("Found doctor: {} {}", doctor.getFirstName(), doctor.getLastName());
        return ResponseEntity.ok(doctor);
    }
    
    /**
     * Get top rated doctors by hospital with pagination
     */
    @GetMapping("/hospital/{hospitalId}/top-rated")
    @Operation(
        summary = "Hastanedeki en yüksek puanlı doktorları getir",
        description = "Belirtilen hastanedeki en yüksek puanlı doktorları rating'e göre sıralı şekilde sayfalı olarak listeler. " +
                     "Pagination parametreleri: page (0-based), size (default: 20)"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Doktorlar başarıyla getirildi",
            content = @Content(schema = @Schema(implementation = DoctorResponseDTO.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Hastane bulunamadı",
            content = @Content
        )
    })
    public ResponseEntity<Page<DoctorResponseDTO>> getTopRatedDoctorsByHospital(
            @Parameter(description = "Hastane ID", required = true)
            @PathVariable Long hospitalId,
            @Parameter(description = "Pagination parameters")
            @PageableDefault(size = 20) Pageable pageable) {
        
        logger.info("Getting top rated doctors for hospital ID: {} (page: {}, size: {})", 
            hospitalId, pageable.getPageNumber(), pageable.getPageSize());
        Page<DoctorResponseDTO> doctors = doctorService.getTopRatedDoctorsByHospital(hospitalId, pageable);
        logger.debug("Found {} top rated doctors for hospital ID: {}", doctors.getTotalElements(), hospitalId);
        return ResponseEntity.ok(doctors);
    }
    
    /**
     * Get top rated doctors by hospital (without pagination - for backward compatibility)
     */
    @GetMapping("/hospital/{hospitalId}/top-rated/all")
    @Operation(
        summary = "Hastanedeki en yüksek puanlı tüm doktorları getir (sayfalama olmadan)",
        description = "Belirtilen hastanedeki en yüksek puanlı tüm doktorları listeler. " +
                     "Büyük veri setleri için /hospital/{hospitalId}/top-rated endpoint'ini kullanın."
    )
    public ResponseEntity<List<DoctorResponseDTO>> getTopRatedDoctorsByHospitalAll(
            @Parameter(description = "Hastane ID", required = true)
            @PathVariable Long hospitalId) {
        
        logger.info("Getting all top rated doctors for hospital ID: {}", hospitalId);
        List<DoctorResponseDTO> doctors = doctorService.getTopRatedDoctorsByHospital(hospitalId);
        logger.debug("Found {} top rated doctors for hospital ID: {}", doctors.size(), hospitalId);
        return ResponseEntity.ok(doctors);
    }
    
    /**
     * Create a new doctor
     * 
     * Security: Only ADMIN role can create doctors
     * Validation: @Valid annotation ensures request data is validated
     * Entity Safety: Uses DoctorCreateRequest DTO instead of Doctor entity
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Yeni doktor oluştur",
        description = "Yeni bir doktor kaydı oluşturur. Sadece ADMIN rolüne sahip kullanıcılar bu işlemi yapabilir."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201",
            description = "Doktor başarıyla oluşturuldu",
            content = @Content(schema = @Schema(implementation = DoctorResponseDTO.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Geçersiz giriş verisi veya validasyon hatası",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Kimlik doğrulama gerekli",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Bu işlem için yetkiniz bulunmamaktadır (ADMIN rolü gerekli)",
            content = @Content
        )
    })
    public ResponseEntity<DoctorResponseDTO> createDoctor(
            @Valid @RequestBody DoctorCreateRequest request) {
        
        logger.info("Creating new doctor: {} {}", request.getFirstName(), request.getLastName());
        DoctorResponseDTO createdDoctor = doctorService.createDoctor(request);
        logger.info("Doctor created successfully with ID: {}", createdDoctor.getId());
        return new ResponseEntity<>(createdDoctor, HttpStatus.CREATED);
    }
    
    /**
     * Update an existing doctor
     * 
     * Security: Only ADMIN role can update doctors
     * Validation: @Valid annotation ensures request data is validated
     * Entity Safety: Uses DoctorUpdateRequest DTO instead of Doctor entity
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Doktor bilgilerini güncelle",
        description = "Mevcut bir doktorun bilgilerini günceller. Sadece ADMIN rolüne sahip kullanıcılar bu işlemi yapabilir."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Doktor başarıyla güncellendi",
            content = @Content(schema = @Schema(implementation = DoctorResponseDTO.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Geçersiz giriş verisi",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Doktor bulunamadı",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Bu işlem için yetkiniz bulunmamaktadır (ADMIN rolü gerekli)",
            content = @Content
        )
    })
    public ResponseEntity<DoctorResponseDTO> updateDoctor(
            @Parameter(description = "Doktor ID", required = true)
            @PathVariable Long id,
            @Valid @RequestBody DoctorUpdateRequest request) {
        
        logger.info("Updating doctor with ID: {}", id);
        DoctorResponseDTO updatedDoctor = doctorService.updateDoctor(id, request);
        logger.info("Doctor updated successfully with ID: {}", id);
        return ResponseEntity.ok(updatedDoctor);
    }
    
    /**
     * Upload doctor image
     * 
     * Security: Only ADMIN role can upload images
     */
    @PostMapping("/{id}/upload-image")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Doktor görseli yükle",
        description = "Doktor için görsel yükler. Sadece ADMIN rolüne sahip kullanıcılar bu işlemi yapabilir."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Görsel başarıyla yüklendi",
            content = @Content(schema = @Schema(implementation = DoctorResponseDTO.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Geçersiz görsel veya validasyon hatası",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Doktor bulunamadı",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Bu işlem için yetkiniz bulunmamaktadır (ADMIN rolü gerekli)",
            content = @Content
        )
    })
    public ResponseEntity<DoctorResponseDTO> uploadDoctorImage(
            @Parameter(description = "Doktor ID", required = true)
            @PathVariable Long id,
            @Parameter(description = "Görsel dosyası", required = true)
            @RequestParam("file") MultipartFile file) {
        
        logger.info("Uploading image for doctor ID: {}", id);
        DoctorResponseDTO updatedDoctor = doctorService.uploadDoctorImage(id, file);
        logger.info("Image uploaded successfully for doctor ID: {}", id);
        return ResponseEntity.ok(updatedDoctor);
    }
    
    /**
     * Delete a doctor (soft delete)
     * 
     * Security: Only ADMIN role can delete doctors
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Doktoru sil",
        description = "Bir doktoru siler (soft delete). Sadece ADMIN rolüne sahip kullanıcılar bu işlemi yapabilir."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "204",
            description = "Doktor başarıyla silindi"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Doktor bulunamadı",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Bu işlem için yetkiniz bulunmamaktadır (ADMIN rolü gerekli)",
            content = @Content
        )
    })
    public ResponseEntity<Void> deleteDoctor(
            @Parameter(description = "Doktor ID", required = true)
            @PathVariable Long id) {
        
        logger.info("Deleting doctor with ID: {}", id);
        doctorService.deleteDoctor(id);
        logger.info("Doctor deleted successfully with ID: {}", id);
        return ResponseEntity.noContent().build();
    }
}
