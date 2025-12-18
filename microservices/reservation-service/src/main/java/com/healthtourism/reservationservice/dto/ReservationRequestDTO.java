package com.healthtourism.reservationservice.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReservationRequestDTO {
    @NotNull(message = "User ID is required")
    @Positive(message = "User ID must be positive")
    private Long userId;
    
    @NotNull(message = "Hospital ID is required")
    @Positive(message = "Hospital ID must be positive")
    private Long hospitalId;
    
    @NotNull(message = "Doctor ID is required")
    @Positive(message = "Doctor ID must be positive")
    private Long doctorId;
    
    private Long accommodationId;
    
    private Long transferId; // Optional transfer service ID
    
    @NotNull(message = "Appointment date is required")
    @Future(message = "Appointment date must be in the future")
    private LocalDateTime appointmentDate;
    
    @NotNull(message = "Check-in date is required")
    private LocalDateTime checkInDate;
    
    @NotNull(message = "Check-out date is required")
    private LocalDateTime checkOutDate;
    
    @Size(max = 1000, message = "Notes cannot exceed 1000 characters")
    private String notes;
}

