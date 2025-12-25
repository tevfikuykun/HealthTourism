package com.example.HealthTourism.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "reservations", indexes = {
    @Index(name = "idx_reservation_number", columnList = "reservation_number"),
    @Index(name = "idx_user", columnList = "user_id"),
    @Index(name = "idx_hospital", columnList = "hospital_id"),
    @Index(name = "idx_doctor", columnList = "doctor_id"),
    @Index(name = "idx_status", columnList = "status"),
    @Index(name = "idx_appointment_date", columnList = "appointment_date"),
    @Index(name = "idx_created_at", columnList = "created_at"),
    // Composite indexes for common query patterns
    @Index(name = "idx_doctor_date_status", columnList = "doctor_id,appointment_date,status"),
    @Index(name = "idx_hospital_date_status", columnList = "hospital_id,appointment_date,status"),
    @Index(name = "idx_user_status", columnList = "user_id,status"),
    @Index(name = "idx_appointment_status", columnList = "appointment_date,status")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String reservationNumber; // Benzersiz rezervasyon numarası

    @Column(nullable = false)
    private LocalDateTime appointmentDate; // Randevu tarihi ve saati

    @Column(nullable = false)
    private LocalDateTime checkInDate; // Konaklama giriş tarihi

    @Column(nullable = false)
    private LocalDateTime checkOutDate; // Konaklama çıkış tarihi

    @Column(nullable = false)
    private Integer numberOfNights; // Konaklama gece sayısı

    @Column(nullable = false)
    private BigDecimal totalPrice; // Toplam fiyat

    @Column(nullable = false)
    private String status; // PENDING, CONFIRMED, CANCELLED, COMPLETED

    @Column(length = 1000)
    private String notes; // Notlar

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hospital_id", nullable = false)
    private Hospital hospital;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id", nullable = false)
    private Doctor doctor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "accommodation_id")
    private Accommodation accommodation;
}

