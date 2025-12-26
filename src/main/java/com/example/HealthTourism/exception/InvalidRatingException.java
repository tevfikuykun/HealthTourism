package com.example.HealthTourism.exception;

/**
 * Exception thrown when rating is invalid (not between 1-5)
 */
public class InvalidRatingException extends RuntimeException {
    public InvalidRatingException(String message) {
        super(message);
    }
    
    public InvalidRatingException(Integer rating) {
        super(String.format("Geçersiz puan: %d. Puan 1-5 arasında olmalıdır.", rating));
    }
}

