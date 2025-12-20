package com.healthtourism.eventsourcing.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReservationConfirmedEvent {
    private String reservationId;
    private LocalDateTime confirmedAt;
    private String confirmedBy;
}



