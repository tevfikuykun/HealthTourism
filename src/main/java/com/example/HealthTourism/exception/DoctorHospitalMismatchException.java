package com.example.HealthTourism.exception;

/**
 * Exception thrown when doctor does not work at the specified hospital
 */
public class DoctorHospitalMismatchException extends RuntimeException {
    public DoctorHospitalMismatchException(String message) {
        super(message);
    }
    
    public DoctorHospitalMismatchException(String doctorName, String hospitalName) {
        super(String.format("%s doktoru %s hastanesinde çalışmamaktadır.", doctorName, hospitalName));
    }
}

