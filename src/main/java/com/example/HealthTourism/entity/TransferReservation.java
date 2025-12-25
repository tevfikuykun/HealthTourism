package com.example.HealthTourism.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transfer_reservations", indexes = {
    @Index(name = "idx_reservation_number", columnList = "reservation_number"),
    @Index(name = "idx_user", columnList = "user_id"),
    @Index(name = "idx_transfer_service", columnList = "transfer_service_id"),
    @Index(name = "idx_status", columnList = "status"),
    @Index(name = "idx_transfer_date_time", columnList = "transfer_date_time"),
    @Index(name = "idx_created_at", columnList = "created_at"),
    @Index(name = "idx_flight_number", columnList = "flight_number"),
    // Composite indexes for common query patterns
    @Index(name = "idx_status_date", columnList = "status,transfer_date_time"),
    @Index(name = "idx_user_status", columnList = "user_id,status"),
    @Index(name = "idx_service_date_status", columnList = "transfer_service_id,transfer_date_time,status")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransferReservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String reservationNumber;

    @Column(nullable = false)
    private LocalDateTime transferDateTime;

    @Column(nullable = false)
    private Integer numberOfPassengers;

    @Column(nullable = false)
    private BigDecimal totalPrice;

    @Column(nullable = false)
    private String status; // PENDING, CONFIRMED, CANCELLED, COMPLETED

    @Column(length = 500)
    private String specialRequests;
    
    /**
     * Uçuş numarası (flight number).
     * Sağlık turizminde havalimanı karşılaması için kritik.
     * Uçuş takip servisleri (flight tracking bots) bu bilgiyi kullanarak
     * rötarları kontrol eder ve ilgili transfer rezervasyonunu bulur.
     * Örnek: "TK1234", "LH5678"
     */
    @Column(name = "flight_number", length = 50)
    private String flightNumber;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transfer_service_id", nullable = false)
    private TransferService transferService;
}

