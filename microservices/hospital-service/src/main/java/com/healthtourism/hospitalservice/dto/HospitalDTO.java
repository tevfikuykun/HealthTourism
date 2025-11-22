package com.healthtourism.hospitalservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HospitalDTO {
    private Long id;
    private String name;
    private String address;
    private String city;
    private String district;
    private String phone;
    private String email;
    private String description;
    private Double latitude;
    private Double longitude;
    private Double airportDistance;
    private Integer airportDistanceMinutes;
    private Double rating;
    private Integer totalReviews;
}

