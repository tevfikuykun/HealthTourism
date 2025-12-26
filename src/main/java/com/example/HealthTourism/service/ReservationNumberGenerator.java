package com.example.HealthTourism.service;

import org.springframework.stereotype.Component;

import java.time.Year;
import java.util.Random;

/**
 * Generates human-readable reservation numbers for health tourism.
 * Format: HT-YYYY-XXXX (e.g., HT-2025-A12B)
 * 
 * Benefits:
 * - Easy to read and communicate over phone
 * - Contains year for quick identification
 * - Alphanumeric format reduces typing errors
 */
@Component
public class ReservationNumberGenerator {
    
    private static final String PREFIX = "HT";
    private static final String CHARS = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789"; // Excludes similar chars (I, O, 0, 1)
    private static final int CODE_LENGTH = 4;
    private final Random random = new Random();
    
    /**
     * Generates a unique reservation number.
     * Format: HT-YYYY-XXXX
     * Example: HT-2025-A12B
     * 
     * @return Human-readable reservation number
     */
    public String generate() {
        int year = Year.now().getValue();
        String code = generateRandomCode();
        return String.format("%s-%d-%s", PREFIX, year, code);
    }
    
    /**
     * Generates a random alphanumeric code.
     * Excludes similar-looking characters (I, O, 0, 1) to reduce confusion.
     * 
     * @return 4-character alphanumeric code
     */
    private String generateRandomCode() {
        StringBuilder code = new StringBuilder(CODE_LENGTH);
        for (int i = 0; i < CODE_LENGTH; i++) {
            int index = random.nextInt(CHARS.length());
            code.append(CHARS.charAt(index));
        }
        return code.toString();
    }
}

