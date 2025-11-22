package com.example.HealthTourism.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccommodationDTO {
    private Long id;
    private String name;
    private String type;
    private String address;
    private String city;
    private String district;
    private String phone;
    private String email;
    private String description;
    private Double latitude;
    private Double longitude;
    private Double distanceToHospital;
    private Integer distanceToHospitalMinutes;
    private Double airportDistance;
    private Integer airportDistanceMinutes;
    private BigDecimal pricePerNight;
    private Integer starRating;
    private Double rating;
    private Integer totalReviews;
    private Boolean hasWifi;
    private Boolean hasParking;
    private Boolean hasBreakfast;
    private Long hospitalId;
    private String hospitalName;
}

