package com.healthtourism.reservationservice.exception;

/**
 * Exception thrown when there is insufficient capacity for a reservation
 * (e.g., appointment slot already taken)
 */
public class InsufficientCapacityException extends RuntimeException {
    public InsufficientCapacityException(String message) {
        super(message);
    }
}
