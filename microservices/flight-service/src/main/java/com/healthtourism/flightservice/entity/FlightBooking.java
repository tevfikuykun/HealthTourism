package com.healthtourism.flightservice.entity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "flight_bookings")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FlightBooking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false) private String airlineName;
    @Column(nullable = false) private String flightNumber;
    @Column(nullable = false) private String departureCity;
    @Column(nullable = false) private String arrivalCity;
    @Column(nullable = false) private LocalDateTime departureDateTime;
    @Column(nullable = false) private LocalDateTime arrivalDateTime;
    @Column(nullable = false) private String flightClass;
    @Column(nullable = false) private Integer availableSeats;
    @Column(nullable = false) private BigDecimal price;
    @Column(nullable = false) private Integer durationMinutes;
    @Column(nullable = false) private Boolean hasMeal;
    @Column(nullable = false) private Boolean hasEntertainment;
    @Column(nullable = false) private Integer baggageAllowance;
    @Column(nullable = false) private Boolean isDirectFlight;
    @Column(length = 2000) private String description;
    @Column(nullable = false) private Double rating;
    @Column(nullable = false) private Integer totalReviews;
    @Column(nullable = false) private Boolean isAvailable;
}

