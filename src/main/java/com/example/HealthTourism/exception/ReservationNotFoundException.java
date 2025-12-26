package com.example.HealthTourism.exception;

/**
 * Exception thrown when a reservation resource is not found
 */
public class ReservationNotFoundException extends RuntimeException {
    public ReservationNotFoundException(String message) {
        super(message);
    }
    
    public ReservationNotFoundException(Long id) {
        super("ID'si " + id + " olan rezervasyon bulunamadı.");
    }
    
    public ReservationNotFoundException(String reservationNumber) {
        super("Rezervasyon numarası '" + reservationNumber + "' ile rezervasyon bulunamadı.");
    }
}

