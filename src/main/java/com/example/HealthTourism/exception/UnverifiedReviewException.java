package com.example.HealthTourism.exception;

/**
 * Exception thrown when a user tries to review without having a completed reservation
 * Security requirement: Only users with completed appointments can review
 */
public class UnverifiedReviewException extends RuntimeException {
    public UnverifiedReviewException(String message) {
        super(message);
    }
    
    public UnverifiedReviewException(String reviewType) {
        super(String.format("Sadece tamamlanmış randevular için yorum yapabilirsiniz. " +
                "Lütfen önce %s için bir randevu tamamlayın.", reviewType));
    }
}

