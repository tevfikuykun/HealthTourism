package com.example.HealthTourism.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReservationRequestDTO {
    private Long userId;
    private Long hospitalId;
    private Long doctorId;
    private Long accommodationId;
    private LocalDateTime appointmentDate;
    private LocalDateTime checkInDate;
    private LocalDateTime checkOutDate;
    private String notes;
}

