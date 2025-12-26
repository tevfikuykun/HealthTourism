package com.example.HealthTourism.exception;

import java.time.LocalDateTime;

/**
 * Exception thrown when a reservation conflicts with an existing reservation
 * (e.g., doctor already has an appointment at the requested time)
 */
public class ReservationConflictException extends RuntimeException {
    public ReservationConflictException(String message) {
        super(message);
    }
    
    public ReservationConflictException(String doctorName, LocalDateTime appointmentDate) {
        super(String.format("%s doktorunun %s tarih/saatinde başka bir randevusu bulunmaktadır.", 
                doctorName, appointmentDate));
    }
}

