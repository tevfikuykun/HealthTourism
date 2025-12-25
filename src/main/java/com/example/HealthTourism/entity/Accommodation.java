package com.example.HealthTourism.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "accommodations", indexes = {
    @Index(name = "idx_hospital", columnList = "hospital_id"),
    @Index(name = "idx_city", columnList = "city"),
    @Index(name = "idx_rating", columnList = "rating"),
    // Composite index for health tourism queries: finding hotels near hospitals
    @Index(name = "idx_hospital_distance", columnList = "hospital_id,distance_to_hospital"),
    // Index for price range searches
    @Index(name = "idx_price_active", columnList = "price_per_night,is_active"),
    // Index for city and active status (common filter combination)
    @Index(name = "idx_city_active", columnList = "city,is_active")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Accommodation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String type; // Hotel, Apartment, Guesthouse vb.

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
    private Double distanceToHospital; // km cinsinden

    @Column(nullable = false)
    private Integer distanceToHospitalMinutes; // dakika cinsinden

    @Column(nullable = false)
    private Double airportDistance; // km cinsinden

    @Column(nullable = false)
    private Integer airportDistanceMinutes; // dakika cinsinden

    @Column(nullable = false)
    private BigDecimal pricePerNight; // Gece başına fiyat

    @Column(nullable = false)
    private Integer starRating; // 1-5 yıldız

    @Column(nullable = false)
    private Double rating;

    @Column(nullable = false)
    private Integer totalReviews;

    @Column(nullable = false)
    private Boolean hasWifi;

    @Column(nullable = false)
    private Boolean hasParking;

    @Column(nullable = false)
    private Boolean hasBreakfast;

    @Column(nullable = false)
    private Boolean isActive;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hospital_id", nullable = false)
    private Hospital hospital;

    @OneToMany(mappedBy = "accommodation", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Reservation> reservations;
}

