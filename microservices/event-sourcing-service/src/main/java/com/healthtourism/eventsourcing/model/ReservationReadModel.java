package com.healthtourism.eventsourcing.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

/**
 * Reservation Read Model (CQRS)
 * Optimized for queries, stored in MongoDB
 */
@Data
@Document(collection = "reservations")
public class ReservationReadModel {
    @Id
    private String id;
    private String reservationId;
    private Long userId;
    private Long hospitalId;
    private String procedureType;
    private LocalDateTime reservationDate;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime confirmedAt;
    private LocalDateTime cancelledAt;
    private String cancellationReason;
}


