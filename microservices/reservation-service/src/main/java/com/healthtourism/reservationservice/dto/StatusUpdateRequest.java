package com.healthtourism.reservationservice.dto;

import com.healthtourism.reservationservice.entity.ReservationStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Status Update Request DTO
 * 
 * Request DTO for updating reservation status.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StatusUpdateRequest {
    
    @NotNull(message = "Durum boş olamaz")
    private ReservationStatus status;
}

