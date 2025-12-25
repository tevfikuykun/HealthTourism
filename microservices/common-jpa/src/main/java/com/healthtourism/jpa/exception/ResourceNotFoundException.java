package com.healthtourism.jpa.exception;

/**
 * Resource Not Found Exception
 * 
 * Thrown when a requested resource (entity) is not found.
 * Maps to HTTP 404 status code.
 */
public class ResourceNotFoundException extends BusinessException {
    
    public ResourceNotFoundException(ErrorCode errorCode) {
        super(errorCode);
    }
    
    public ResourceNotFoundException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
    
    /**
     * Convenience method for patient not found
     */
    public static ResourceNotFoundException patientNotFound(Object identifier) {
        return new ResourceNotFoundException(
            ErrorCode.PATIENT_NOT_FOUND,
            "Hasta bulunamadı: " + identifier
        );
    }
    
    /**
     * Convenience method for appointment not found
     */
    public static ResourceNotFoundException appointmentNotFound(Object identifier) {
        return new ResourceNotFoundException(
            ErrorCode.APPOINTMENT_NOT_FOUND,
            "Randevu bulunamadı: " + identifier
        );
    }
    
    /**
     * Convenience method for doctor not found
     */
    public static ResourceNotFoundException doctorNotFound(Object identifier) {
        return new ResourceNotFoundException(
            ErrorCode.DOCTOR_NOT_FOUND,
            "Doktor bulunamadı: " + identifier
        );
    }
    
    /**
     * Convenience method for hospital not found
     */
    public static ResourceNotFoundException hospitalNotFound(Object identifier) {
        return new ResourceNotFoundException(
            ErrorCode.HOSPITAL_NOT_FOUND,
            "Hastane bulunamadı: " + identifier
        );
    }
}

