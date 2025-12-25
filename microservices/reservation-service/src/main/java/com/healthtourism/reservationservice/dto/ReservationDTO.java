package com.healthtourism.reservationservice.dto;

import com.healthtourism.reservationservice.entity.ReservationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Reservation DTO
 * 
 * Response DTO for reservation data.
 * Used for API responses to prevent entity leakage.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReservationDTO {
    private UUID id;
    private String reservationNumber;
    private LocalDateTime appointmentDate;
    private LocalDateTime checkInDate;
    private LocalDateTime checkOutDate;
    private Integer numberOfNights;
    private BigDecimal totalPrice;
    private String currency;
    private ReservationStatus status;
    private String notes;
    private String contactPreference;
    private UUID userId;
    private Long hospitalId;
    private UUID doctorId;
    private Long accommodationId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
