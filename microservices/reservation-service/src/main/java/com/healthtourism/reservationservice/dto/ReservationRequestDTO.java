package com.healthtourism.reservationservice.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Reservation Request DTO
 * 
 * Request DTO for creating a new reservation.
 * Validates input data and prevents entity leakage.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReservationRequestDTO {
    
    @NotNull(message = "Randevu tarihi boş olamaz")
    @Future(message = "Randevu tarihi gelecekte olmalıdır")
    private LocalDateTime appointmentDate;
    
    @NotNull(message = "Check-in tarihi boş olamaz")
    private LocalDateTime checkInDate;
    
    @NotNull(message = "Check-out tarihi boş olamaz")
    private LocalDateTime checkOutDate;
    
    @NotNull(message = "Gece sayısı boş olamaz")
    @Min(value = 1, message = "Gece sayısı en az 1 olmalıdır")
    @Max(value = 365, message = "Gece sayısı en fazla 365 olabilir")
    private Integer numberOfNights;
    
    @NotNull(message = "Hastane ID boş olamaz")
    @Positive(message = "Hastane ID pozitif bir sayı olmalıdır")
    private Long hospitalId;
    
    @NotNull(message = "Doktor ID boş olamaz")
    private UUID doctorId;
    
    private Long accommodationId; // Optional
    
    @Size(max = 1000, message = "Notlar en fazla 1000 karakter olabilir")
    private String notes;
    
    @Size(max = 50, message = "İletişim tercihi en fazla 50 karakter olabilir")
    private String contactPreference;
}
