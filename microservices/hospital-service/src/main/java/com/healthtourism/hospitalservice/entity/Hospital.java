package com.healthtourism.hospitalservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "hospitals")
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
    private Double airportDistance;
    @Column(nullable = false)
    private Integer airportDistanceMinutes;
    @Column(nullable = false)
    private Double rating;
    @Column(nullable = false)
    private Integer totalReviews;
    @Column(nullable = false)
    private Boolean isActive;
}

