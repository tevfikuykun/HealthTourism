package com.example.HealthTourism.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "hospitals", indexes = {
    @Index(name = "idx_city", columnList = "city"),
    @Index(name = "idx_district", columnList = "district"),
    @Index(name = "idx_active", columnList = "is_active"),
    @Index(name = "idx_rating", columnList = "rating"),
    @Index(name = "idx_airport_distance", columnList = "airport_distance"),
    // Composite indexes for common query patterns
    @Index(name = "idx_city_active", columnList = "city,is_active"),
    @Index(name = "idx_city_district_active", columnList = "city,district,is_active"),
    @Index(name = "idx_rating_airport", columnList = "rating,airport_distance")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Hospital {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false)
    private String city;

    @Column(nullable = false)
    private String district;

    @Column(nullable = false)
    private String phone;

    @Column(nullable = false)
    private String email;

    @Column(length = 2000)
    private String description;

    @Column(nullable = false)
    private Double latitude;

    @Column(nullable = false)
    private Double longitude;

    @Column(nullable = false)
    private Double airportDistance; // km cinsinden

    @Column(nullable = false)
    private Integer airportDistanceMinutes; // dakika cinsinden

    @Column(nullable = false)
    private Double rating;

    @Column(nullable = false)
    private Integer totalReviews;

    @Column(nullable = false)
    private Boolean isActive;
    
    /**
     * Hastane akreditasyonları (JCI, ISO, vb.)
     * Sağlık turizminde güven her şeydir - akreditasyonlar kurumsal güvenilirliği artırır.
     * ElementCollection olarak saklanır: hospital_accreditations tablosunda.
     */
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(
        name = "hospital_accreditations",
        joinColumns = @JoinColumn(name = "hospital_id"),
        indexes = @Index(name = "idx_hospital_accreditation", columnList = "accreditation")
    )
    @Column(name = "accreditation", length = 100)
    private Set<String> accreditations = new HashSet<>();

    @OneToMany(mappedBy = "hospital", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Doctor> doctors;

    @OneToMany(mappedBy = "hospital", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Reservation> reservations;

    @OneToMany(mappedBy = "hospital", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Accommodation> accommodations;
}

