package com.healthtourism.carrentalservice.dto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CarRentalDTO {
    private Long id;
    private String companyName;
    private String carModel;
    private String carType;
    private Integer passengerCapacity;
    private Integer luggageCapacity;
    private String transmission;
    private Boolean hasAirConditioning;
    private Boolean hasGPS;
    private BigDecimal pricePerDay;
    private BigDecimal pricePerWeek;
    private BigDecimal pricePerMonth;
    private String pickupLocation;
    private String dropoffLocation;
    private String phone;
    private String email;
    private String description;
    private Double rating;
    private Integer totalReviews;
    private Boolean isAvailable;
    private Boolean includesInsurance;
    private Boolean includesDriver;
}

