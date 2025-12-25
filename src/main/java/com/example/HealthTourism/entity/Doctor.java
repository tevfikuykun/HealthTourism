package com.example.HealthTourism.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "doctors", indexes = {
    @Index(name = "idx_hospital", columnList = "hospital_id"),
    @Index(name = "idx_specialization", columnList = "specialization"),
    @Index(name = "idx_available", columnList = "is_available"),
    @Index(name = "idx_rating", columnList = "rating"),
    // Composite indexes for common query patterns
    @Index(name = "idx_hospital_available", columnList = "hospital_id,is_available"),
    @Index(name = "idx_specialization_available", columnList = "specialization,is_available"),
    @Index(name = "idx_rating_experience", columnList = "rating,experience_years")
    // Note: For languages field (TEXT), consider using FULLTEXT index in MySQL for better search performance
    // This can be added via SQL migration: ALTER TABLE doctors ADD FULLTEXT INDEX idx_languages_ft (languages);
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Doctor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(nullable = false)
    private String specialization; // Uzmanlık alanı

    @Column(nullable = false)
    private String title; // Prof. Dr., Doç. Dr., Dr. vb.

    @Column(length = 2000)
    private String bio;

    @Column(nullable = false)
    private Integer experienceYears;

    @Column(nullable = false)
    private String languages; // Konuştuğu diller (virgülle ayrılmış)

    @Column(nullable = false)
    private Double rating;

    @Column(nullable = false)
    private Integer totalReviews;

    @Column(nullable = false)
    private Double consultationFee; // Muayene ücreti

    @Column(nullable = false)
    private Boolean isAvailable;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hospital_id", nullable = false)
    private Hospital hospital;

    @OneToMany(mappedBy = "doctor", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Review> reviews;

    @OneToMany(mappedBy = "doctor", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Reservation> reservations;
}

