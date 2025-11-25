package com.healthtourism.kafkaservice.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReservationEvent {
    private String eventType; // CREATED, UPDATED, CANCELLED, COMPLETED
    private Long reservationId;
    private Long userId;
    private Long hospitalId;
    private LocalDateTime timestamp;
    private Object data;
}


