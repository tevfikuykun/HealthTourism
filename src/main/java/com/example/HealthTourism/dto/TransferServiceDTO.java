package com.example.HealthTourism.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransferServiceDTO {
    private Long id;
    private String companyName;
    private String vehicleType;
    private Integer passengerCapacity;
    private String serviceType;
    private String pickupLocation;
    private String dropoffLocation;
    private BigDecimal price;
    private Integer durationMinutes;
    private Double distanceKm;
    private Boolean hasMeetAndGreet;
    private Boolean hasLuggageAssistance;
    private Boolean hasWifi;
    private Boolean hasChildSeat;
    private String phone;
    private String email;
    private String description;
    private Double rating;
    private Integer totalReviews;
    private Boolean isAvailable;
}

