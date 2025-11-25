package com.healthtourism.reservationservice.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReservationEvent {
    private String eventType; // RESERVATION_CREATED, RESERVATION_UPDATED, RESERVATION_CANCELLED
    private Long reservationId;
    private String reservationNumber;
    private Long userId;
    private Long hospitalId;
    private Long doctorId;
    private Long accommodationId;
    private LocalDateTime appointmentDate;
    private LocalDateTime checkInDate;
    private LocalDateTime checkOutDate;
    private Integer numberOfNights;
    private BigDecimal totalPrice;
    private String status;
    private String notes;
    private LocalDateTime eventTimestamp;
    private String eventId;
}


