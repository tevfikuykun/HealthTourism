package com.healthtourism.accommodationservice.entity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Entity
@Table(name = "accommodations")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Accommodation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false) private String name;
    @Column(nullable = false) private String type;
    @Column(nullable = false) private String address;
    @Column(nullable = false) private String city;
    @Column(nullable = false) private String district;
    @Column(nullable = false) private String phone;
    @Column(nullable = false) private String email;
    @Column(length = 2000) private String description;
    @Column(nullable = false) private Double latitude;
    @Column(nullable = false) private Double longitude;
    @Column(nullable = false) private Double distanceToHospital;
    @Column(nullable = false) private Integer distanceToHospitalMinutes;
    @Column(nullable = false) private Double airportDistance;
    @Column(nullable = false) private Integer airportDistanceMinutes;
    @Column(nullable = false) private BigDecimal pricePerNight;
    @Column(nullable = false) private Integer starRating;
    @Column(nullable = false) private Double rating;
    @Column(nullable = false) private Integer totalReviews;
    @Column(nullable = false) private Boolean hasWifi;
    @Column(nullable = false) private Boolean hasParking;
    @Column(nullable = false) private Boolean hasBreakfast;
    @Column(nullable = false) private Boolean isActive;
    @Column(nullable = false) private Long hospitalId;
    @Column(length = 500) private String imageUrl; // URL to accommodation image
    @Column(length = 500) private String thumbnailUrl; // URL to thumbnail image
}

