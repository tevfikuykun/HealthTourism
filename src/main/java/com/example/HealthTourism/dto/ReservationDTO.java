package com.example.HealthTourism.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReservationDTO {
    private Long id;
    private String reservationNumber;
    private LocalDateTime appointmentDate;
    private LocalDateTime checkInDate;
    private LocalDateTime checkOutDate;
    private Integer numberOfNights;
    private BigDecimal totalPrice;
    private String status;
    private String notes;
    private LocalDateTime createdAt;
    private Long userId;
    private String userName;
    private Long hospitalId;
    private String hospitalName;
    private Long doctorId;
    private String doctorName;
    private Long accommodationId;
    private String accommodationName;
}

