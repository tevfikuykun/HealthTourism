package com.healthtourism.eventsourcing.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReservationCreatedEvent {
    private String reservationId;
    private Long userId;
    private Long hospitalId;
    private String procedureType;
    private LocalDateTime reservationDate;
    private LocalDateTime createdAt;
}






