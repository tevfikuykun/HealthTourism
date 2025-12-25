package com.example.HealthTourism.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "flight_bookings", indexes = {
    @Index(name = "idx_departure", columnList = "departure_city,departure_date_time"),
    @Index(name = "idx_arrival", columnList = "arrival_city"),
    @Index(name = "idx_available", columnList = "is_available"),
    @Index(name = "idx_date", columnList = "departure_date_time"),
    @Index(name = "idx_route", columnList = "departure_city,arrival_city"),
    @Index(name = "idx_airline", columnList = "airline_name"),
    @Index(name = "idx_available_seats", columnList = "available_seats,is_available"),
    @Index(name = "idx_date_range", columnList = "departure_date_time,arrival_date_time")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FlightBooking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * Optimistic locking version field.
     * Prevents concurrent seat booking conflicts.
     * Automatically incremented on each update.
     */
    @Version
    @Column(name = "version")
    private Long version;

    @Column(nullable = false)
    private String airlineName;

    @Column(nullable = false)
    private String flightNumber;

    @Column(nullable = false)
    private String departureCity;

    @Column(nullable = false)
    private String arrivalCity;

    @Column(nullable = false)
    private LocalDateTime departureDateTime;

    @Column(nullable = false)
    private LocalDateTime arrivalDateTime;

    @Column(nullable = false)
    private String flightClass; // Economy, Business, First

    @Column(nullable = false)
    private Integer availableSeats;
    
    /**
     * Total seats capacity of the flight.
     * Used for calculating occupancy rate and promotional queries.
     */
    @Column(nullable = false)
    private Integer totalSeats;

    @Column(nullable = false)
    private BigDecimal price;

    @Column(nullable = false)
    private Integer durationMinutes; // Uçuş süresi

    @Column(nullable = false)
    private Boolean hasMeal;

    @Column(nullable = false)
    private Boolean hasEntertainment;

    @Column(nullable = false)
    private Integer baggageAllowance; // kg cinsinden

    @Column(nullable = false)
    private Boolean isDirectFlight;

    @Column(length = 2000)
    private String description;

    @Column(nullable = false)
    private Double rating;

    @Column(nullable = false)
    private Integer totalReviews;

    @Column(nullable = false)
    private Boolean isAvailable;
}

