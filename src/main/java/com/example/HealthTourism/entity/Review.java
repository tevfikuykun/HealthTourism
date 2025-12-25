package com.example.HealthTourism.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "reviews", indexes = {
    @Index(name = "idx_doctor", columnList = "doctor_id"),
    @Index(name = "idx_hospital", columnList = "hospital_id"),
    @Index(name = "idx_user", columnList = "user_id"),
    @Index(name = "idx_approved", columnList = "is_approved"),
    @Index(name = "idx_created_at", columnList = "created_at"),
    @Index(name = "idx_rating", columnList = "rating"),
    // Composite indexes for common query patterns
    @Index(name = "idx_doctor_approved", columnList = "doctor_id,is_approved"),
    @Index(name = "idx_hospital_approved", columnList = "hospital_id,is_approved"),
    @Index(name = "idx_review_type_approved", columnList = "review_type,is_approved")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer rating; // 1-5 arası puan

    @Column(length = 2000)
    private String comment;

    @Column(nullable = false)
    private LocalDateTime createdAt;
    
    /**
     * Moderasyon ve onay mekanizması.
     * false: Onay bekliyor (PENDING) - henüz yayınlanmamış
     * true: Onaylanmış (APPROVED) - yayınlanmış ve görünür
     * 
     * Kurumsal gereklilik: Uygunsuz veya sahte yorumların
     * doğrudan doktor/hastane profilinde görünmesini engeller.
     */
    @Column(nullable = false)
    private Boolean isApproved = false;
    
    /**
     * Yorum statüsü (opsiyonel, isApproved ile birlikte kullanılabilir).
     * PENDING: Onay bekliyor
     * APPROVED: Onaylanmış ve yayında
     * REJECTED: Reddedilmiş
     */
    @Column(length = 50)
    private String status; // PENDING, APPROVED, REJECTED

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id")
    private Doctor doctor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hospital_id")
    private Hospital hospital;

    @Column(nullable = false)
    private String reviewType; // DOCTOR, HOSPITAL
}

