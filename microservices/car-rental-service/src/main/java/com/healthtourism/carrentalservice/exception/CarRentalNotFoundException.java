package com.healthtourism.carrentalservice.exception;

/**
 * Exception thrown when a car rental resource is not found
 */
public class CarRentalNotFoundException extends ResourceNotFoundException {
    public CarRentalNotFoundException(String message) {
        super(message);
    }
    
    public CarRentalNotFoundException(Long id) {
        super("ID'si " + id + " olan araç kiralama bulunamadı.");
    }
}
