package com.healthtourism.flightservice.dto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FlightBookingDTO {
    private Long id;
    private String airlineName;
    private String flightNumber;
    private String departureCity;
    private String arrivalCity;
    private LocalDateTime departureDateTime;
    private LocalDateTime arrivalDateTime;
    private String flightClass;
    private Integer availableSeats;
    private BigDecimal price;
    private Integer durationMinutes;
    private Boolean hasMeal;
    private Boolean hasEntertainment;
    private Integer baggageAllowance;
    private Boolean isDirectFlight;
    private String description;
    private Double rating;
    private Integer totalReviews;
    private Boolean isAvailable;
}

