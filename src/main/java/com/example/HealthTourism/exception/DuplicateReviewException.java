package com.example.HealthTourism.exception;

/**
 * Exception thrown when a user tries to submit a duplicate review
 * (already reviewed the same doctor/hospital)
 */
public class DuplicateReviewException extends RuntimeException {
    public DuplicateReviewException(String message) {
        super(message);
    }
    
    public DuplicateReviewException(String reviewType, String entityName) {
        super(String.format("Bu %s için daha önce yorum yapmışsınız: %s", reviewType, entityName));
    }
}

