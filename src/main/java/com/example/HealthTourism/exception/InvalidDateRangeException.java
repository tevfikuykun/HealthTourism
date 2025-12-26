package com.example.HealthTourism.exception;

/**
 * Exception thrown when date range validation fails
 * (e.g., check-out date before check-in date, past dates, etc.)
 */
public class InvalidDateRangeException extends RuntimeException {
    public InvalidDateRangeException(String message) {
        super(message);
    }
}

