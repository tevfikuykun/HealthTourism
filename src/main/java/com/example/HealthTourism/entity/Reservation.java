package com.example.HealthTourism.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "reservations")
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

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "hospital_id", nullable = false)
    private Hospital hospital;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "doctor_id", nullable = false)
    private Doctor doctor;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "accommodation_id")
    private Accommodation accommodation;
}

