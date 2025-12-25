package com.healthtourism.reservationservice.exception;

/**
 * Reservation Not Found Exception
 * 
 * Thrown when a reservation with the specified ID or number is not found.
 */
public class ReservationNotFoundException extends RuntimeException {
    
    public ReservationNotFoundException(Long id) {
        super("Reservation not found: " + id);
    }
    
    public ReservationNotFoundException(String message) {
        super(message);
    }
}

