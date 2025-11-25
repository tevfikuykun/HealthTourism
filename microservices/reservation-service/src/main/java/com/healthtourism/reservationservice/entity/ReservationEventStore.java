package com.healthtourism.reservationservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "reservation_events", indexes = {
    @Index(name = "idx_reservation_id", columnList = "reservationId"),
    @Index(name = "idx_event_type", columnList = "eventType"),
    @Index(name = "idx_timestamp", columnList = "eventTimestamp")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReservationEventStore {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String eventId;
    
    @Column(nullable = false)
    private String eventType;
    
    @Column(nullable = false)
    private Long reservationId;
    
    @Column(nullable = false, columnDefinition = "TEXT")
    private String eventData; // JSON formatında event data
    
    @Column(nullable = false)
    private LocalDateTime eventTimestamp;
    
    @Column(nullable = false)
    private String aggregateType; // RESERVATION
    
    @Column(nullable = false)
    private Long aggregateVersion;
    
    @PrePersist
    protected void onCreate() {
        if (eventTimestamp == null) {
            eventTimestamp = LocalDateTime.now();
        }
    }
}


