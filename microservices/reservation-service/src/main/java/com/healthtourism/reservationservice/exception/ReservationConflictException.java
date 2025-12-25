package com.healthtourism.reservationservice.exception;

/**
 * Reservation Conflict Exception
 * 
 * Thrown when a reservation conflicts with existing appointments
 * (e.g., doctor already has appointment at requested time).
 */
public class ReservationConflictException extends RuntimeException {
    
    public ReservationConflictException(String message) {
        super(message);
    }
    
    public ReservationConflictException(String message, Throwable cause) {
        super(message, cause);
    }
}

