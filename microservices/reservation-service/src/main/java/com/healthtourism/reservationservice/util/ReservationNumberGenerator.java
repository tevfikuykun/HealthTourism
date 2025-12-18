package com.healthtourism.reservationservice.util;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Utility class for generating unique reservation numbers
 * Format: HT-YYYYMM-XXX (e.g., HT-202310-001)
 */
@Component
public class ReservationNumberGenerator {
    
    private static final String PREFIX = "HT";
    private static final AtomicInteger counter = new AtomicInteger(1);
    private static String currentMonth = "";
    
    /**
     * Generates a unique reservation number
     * Format: HT-YYYYMM-XXX where XXX is a sequential number resetting each month
     * 
     * @return Unique reservation number (e.g., HT-202310-001)
     */
    public synchronized String generateReservationNumber() {
        String monthKey = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMM"));
        
        // Reset counter if month changed
        if (!monthKey.equals(currentMonth)) {
            currentMonth = monthKey;
            counter.set(1);
        }
        
        int sequenceNumber = counter.getAndIncrement();
        String formattedSequence = String.format("%03d", sequenceNumber);
        
        return String.format("%s-%s-%s", PREFIX, monthKey, formattedSequence);
    }
    
    /**
     * Generates a reservation number for a specific date
     * Useful for testing or backdating reservations
     * 
     * @param date The date for which to generate the reservation number
     * @return Unique reservation number
     */
    public synchronized String generateReservationNumber(LocalDateTime date) {
        String monthKey = date.format(DateTimeFormatter.ofPattern("yyyyMM"));
        int sequenceNumber = counter.getAndIncrement();
        String formattedSequence = String.format("%03d", sequenceNumber);
        
        return String.format("%s-%s-%s", PREFIX, monthKey, formattedSequence);
    }
}
