package com.healthtourism.doctorservice.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Doctor Update Request DTO
 * 
 * Professional request DTO for updating an existing doctor.
 * All fields are optional for partial updates.
 * 
 * Features:
 * - List-based specializations and languages
 * - Currency support
 * - All fields optional (partial update support)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DoctorUpdateRequest {
    
    @Size(min = 2, max = 50, message = "İsim 2 ile 50 karakter arasında olmalıdır")
    private String firstName;
    
    @Size(min = 2, max = 50, message = "Soyisim 2 ile 50 karakter arasında olmalıdır")
    private String lastName;
    
    /**
     * List of specializations (optional for update)
     */
    @Size(min = 1, max = 10, message = "1 ile 10 arasında uzmanlık alanı belirtilebilir")
    private List<@NotBlank(message = "Uzmanlık alanı boş olamaz") 
                 @Size(max = 100, message = "Uzmanlık alanı en fazla 100 karakter olabilir") String> specializations;
    
    @Size(max = 50, message = "Ünvan en fazla 50 karakter olabilir")
    private String title;
    
    @Size(max = 2000, message = "Biyografi en fazla 2000 karakter olabilir")
    private String bio;
    
    @Min(value = 0, message = "Deneyim yılı negatif olamaz")
    @Max(value = 60, message = "Deneyim yılı 60'dan büyük olamaz")
    private Integer experienceYears;
    
    /**
     * List of languages (optional for update)
     */
    @Size(min = 1, max = 10, message = "1 ile 10 arasında dil belirtilebilir")
    private List<@NotBlank(message = "Dil boş olamaz") 
                 @Size(max = 50, message = "Dil en fazla 50 karakter olabilir") String> languages;
    
    /**
     * Currency code (ISO 4217)
     */
    @Pattern(regexp = "^[A-Z]{3}$", message = "Para birimi 3 harfli ISO 4217 formatında olmalıdır (örn: EUR, USD, TRY)")
    private String currency;
    
    @DecimalMin(value = "0.0", inclusive = false, message = "Muayene ücreti negatif olamaz")
    @Digits(integer = 10, fraction = 2, message = "Muayene ücreti formatı geçersiz")
    private Double consultationFee;
    
    private Boolean isAvailable;
    
    @Size(max = 500, message = "Görsel URL en fazla 500 karakter olabilir")
    @Pattern(regexp = "^https?://.*|$", message = "Görsel URL geçerli bir HTTP/HTTPS URL olmalıdır")
    private String imageUrl;
    
    @Size(max = 500, message = "Thumbnail URL en fazla 500 karakter olabilir")
    @Pattern(regexp = "^https?://.*|$", message = "Thumbnail URL geçerli bir HTTP/HTTPS URL olmalıdır")
    private String thumbnailUrl;
}

