package com.example.HealthTourism.repository.projection;

import org.springframework.beans.factory.annotation.Value;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Interface-based projection for lightweight reservation queries.
 * Used when full entity details are not needed, improving query performance.
 * 
 * This projection returns only essential fields without loading full CarRental
 * and User entities, making list queries much faster.
 * 
 * Usage:
 * <pre>
 * List&lt;ReservationSummary&gt; summaries = repository.findSummaryByUserId(userId);
 * summaries.forEach(summary -&gt; {
 *     System.out.println(summary.getReservationNumber());
 *     System.out.println(summary.getCarModel());
 * });
 * </pre>
 */
public interface ReservationSummary {
    
    /**
     * Unique reservation number (e.g., "CAR-RES-2024-001234")
     */
    String getReservationNumber();
    
    /**
     * Reservation status: PENDING, CONFIRMED, CANCELLED, COMPLETED
     */
    String getStatus();
    
    /**
     * Total price of the reservation
     */
    BigDecimal getTotalPrice();
    
    /**
     * Pickup date
     */
    LocalDate getPickupDate();
    
    /**
     * Dropoff date
     */
    LocalDate getDropoffDate();
    
    /**
     * Car model name (from CarRental entity)
     * Uses @Value to access nested property from joined entity.
     */
    @Value("#{target.carRental.carModel}")
    String getCarModel();
    
    /**
     * Rental company name (from CarRental entity)
     * Uses @Value to access nested property from joined entity.
     */
    @Value("#{target.carRental.companyName}")
    String getCompanyName();
}

