package com.healthtourism.reservationservice.util;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Reservation Number Generator
 * 
 * Generates unique, human-readable reservation numbers.
 * Format: HT-YYYY-MMDD-XXXX
 * 
 * Example: HT-2024-0325-A3B7
 * 
 * Features:
 * - Predictable format (HT prefix, year, date)
 * - Unpredictable suffix (random characters) for security
 * - Human-readable for customer support
 * - Unique identifier
 */
@Component
public class ReservationNumberGenerator {
    
    private static final String PREFIX = "HT";
    private static final String CHARACTERS = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789"; // Excluding confusing chars (0, O, I, 1)
    private static final int SUFFIX_LENGTH = 4;
    private static final SecureRandom RANDOM = new SecureRandom();
    
    /**
     * Generate a new reservation number
     * 
     * Format: HT-YYYY-MMDD-XXXX
     * Example: HT-2024-0325-A3B7
     * 
     * @return Unique reservation number
     */
    public String generate() {
        LocalDateTime now = LocalDateTime.now();
        
        // Format: HT-YYYY-MMDD-XXXX
        String year = String.valueOf(now.getYear());
        String month = String.format("%02d", now.getMonthValue());
        String day = String.format("%02d", now.getDayOfMonth());
        
        String datePart = month + day;
        String randomSuffix = generateRandomSuffix();
        
        return String.format("%s-%s-%s-%s", PREFIX, year, datePart, randomSuffix);
    }
    
    /**
     * Generate random suffix using alphanumeric characters
     * Excludes confusing characters (0, O, I, 1) for better readability
     */
    private String generateRandomSuffix() {
        StringBuilder suffix = new StringBuilder(SUFFIX_LENGTH);
        for (int i = 0; i < SUFFIX_LENGTH; i++) {
            int index = RANDOM.nextInt(CHARACTERS.length());
            suffix.append(CHARACTERS.charAt(index));
        }
        return suffix.toString();
    }
    
    /**
     * Validate reservation number format
     */
    public boolean isValidFormat(String reservationNumber) {
        if (reservationNumber == null || reservationNumber.trim().isEmpty()) {
            return false;
        }
        
        // Pattern: HT-YYYY-MMDD-XXXX
        String pattern = "^HT-\\d{4}-\\d{4}-[A-Z2-9]{4}$";
        return reservationNumber.matches(pattern);
    }
    
    /**
     * Extract year from reservation number
     */
    public Integer extractYear(String reservationNumber) {
        if (!isValidFormat(reservationNumber)) {
            return null;
        }
        try {
            // HT-YYYY-MMDD-XXXX
            String[] parts = reservationNumber.split("-");
            if (parts.length >= 2) {
                return Integer.parseInt(parts[1]);
            }
        } catch (NumberFormatException e) {
            // Invalid format
        }
        return null;
    }
}
