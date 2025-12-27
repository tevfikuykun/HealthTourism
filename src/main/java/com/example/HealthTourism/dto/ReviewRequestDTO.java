package com.example.HealthTourism.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Review Request DTO
 * Yorum oluşturma isteği için kullanılır
 * 
 * Güvenlik Notu: userId alanı gerçek dünya senaryosunda
 * Spring Security Principal'dan otomatik alınmalıdır.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReviewRequestDTO {
    
    /**
     * Kullanıcı ID'si
     * Güvenlik: Gerçek dünya senaryosunda Principal'dan alınmalı
     */
    @NotNull(message = "Kullanıcı ID'si boş olamaz")
    private Long userId;
    
    /**
     * Doktor ID'si (doktor yorumu için)
     */
    private Long doctorId;
    
    /**
     * Hastane ID'si (hastane yorumu için)
     */
    private Long hospitalId;
    
    /**
     * Puan (1-5 arası)
     */
    @NotNull(message = "Puan boş olamaz")
    @Min(value = 1, message = "Puan en az 1 olmalıdır")
    @Max(value = 5, message = "Puan en fazla 5 olmalıdır")
    private Integer rating;
    
    /**
     * Yorum metni (opsiyonel)
     */
    private String comment;
}

