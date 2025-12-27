package com.example.HealthTourism.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Global exception handler for the application.
 * Provides centralized exception handling and meaningful error responses.
 */
@ControllerAdvice
public class GlobalExceptionHandler {
    
    /**
     * Handles DoctorNotFoundException
     */
    @ExceptionHandler(DoctorNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleDoctorNotFoundException(DoctorNotFoundException e) {
        ErrorResponse error = new ErrorResponse(
                HttpStatus.NOT_FOUND.value(), 
                e.getMessage(), 
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }
    
    /**
     * Handles FlightNotFoundException
     */
    @ExceptionHandler(FlightNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleFlightNotFoundException(FlightNotFoundException e) {
        ErrorResponse error = new ErrorResponse(
                HttpStatus.NOT_FOUND.value(), 
                e.getMessage(), 
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }
    
    /**
     * Handles HospitalNotFoundException
     */
    @ExceptionHandler(HospitalNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleHospitalNotFoundException(HospitalNotFoundException e) {
        ErrorResponse error = new ErrorResponse(
                HttpStatus.NOT_FOUND.value(), 
                e.getMessage(), 
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }
    
    /**
     * Handles ReservationNotFoundException
     */
    @ExceptionHandler(ReservationNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleReservationNotFoundException(ReservationNotFoundException e) {
        ErrorResponse error = new ErrorResponse(
                HttpStatus.NOT_FOUND.value(), 
                e.getMessage(), 
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }
    
    /**
     * Handles ReservationConflictException
     */
    @ExceptionHandler(ReservationConflictException.class)
    public ResponseEntity<ErrorResponse> handleReservationConflictException(ReservationConflictException e) {
        ErrorResponse error = new ErrorResponse(
                HttpStatus.CONFLICT.value(), 
                e.getMessage(), 
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }
    
    /**
     * Handles InvalidDateRangeException
     */
    @ExceptionHandler(InvalidDateRangeException.class)
    public ResponseEntity<ErrorResponse> handleInvalidDateRangeException(InvalidDateRangeException e) {
        ErrorResponse error = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(), 
                e.getMessage(), 
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }
    
    /**
     * Handles DoctorHospitalMismatchException
     */
    @ExceptionHandler(DoctorHospitalMismatchException.class)
    public ResponseEntity<ErrorResponse> handleDoctorHospitalMismatchException(DoctorHospitalMismatchException e) {
        ErrorResponse error = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(), 
                e.getMessage(), 
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }
    
    /**
     * Handles DuplicateReviewException
     */
    @ExceptionHandler(DuplicateReviewException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateReviewException(DuplicateReviewException e) {
        ErrorResponse error = new ErrorResponse(
                HttpStatus.CONFLICT.value(), 
                e.getMessage(), 
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }
    
    /**
     * Handles UnverifiedReviewException
     */
    @ExceptionHandler(UnverifiedReviewException.class)
    public ResponseEntity<ErrorResponse> handleUnverifiedReviewException(UnverifiedReviewException e) {
        ErrorResponse error = new ErrorResponse(
                HttpStatus.FORBIDDEN.value(), 
                e.getMessage(), 
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
    }
    
    /**
     * Handles InvalidRatingException
     */
    @ExceptionHandler(InvalidRatingException.class)
    public ResponseEntity<ErrorResponse> handleInvalidRatingException(InvalidRatingException e) {
        ErrorResponse error = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(), 
                e.getMessage(), 
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }
    
    /**
     * Handles TransferNotFoundException
     */
    @ExceptionHandler(TransferNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleTransferNotFoundException(TransferNotFoundException e) {
        ErrorResponse error = new ErrorResponse(
                HttpStatus.NOT_FOUND.value(), 
                e.getMessage(), 
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }
    
    /**
     * Handles TravelPackageNotFoundException
     */
    @ExceptionHandler(TravelPackageNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleTravelPackageNotFoundException(TravelPackageNotFoundException e) {
        ErrorResponse error = new ErrorResponse(
                HttpStatus.NOT_FOUND.value(), 
                e.getMessage(), 
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }
    
    /**
     * Handles TranslationServiceNotFoundException
     */
    @ExceptionHandler(TranslationServiceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleTranslationServiceNotFoundException(TranslationServiceNotFoundException e) {
        ErrorResponse error = new ErrorResponse(
                HttpStatus.NOT_FOUND.value(), 
                e.getMessage(), 
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }
    
    /**
     * Handles VisaConsultancyNotFoundException
     */
    @ExceptionHandler(VisaConsultancyNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleVisaConsultancyNotFoundException(VisaConsultancyNotFoundException e) {
        ErrorResponse error = new ErrorResponse(
                HttpStatus.NOT_FOUND.value(), 
                e.getMessage(), 
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }
    
    /**
     * Handles EmailAlreadyExistsException
     */
    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleEmailAlreadyExistsException(EmailAlreadyExistsException e) {
        ErrorResponse error = new ErrorResponse(
                HttpStatus.CONFLICT.value(), 
                e.getMessage(), 
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }
    
    /**
     * Handles InvalidCredentialsException
     */
    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleInvalidCredentialsException(InvalidCredentialsException e) {
        ErrorResponse error = new ErrorResponse(
                HttpStatus.UNAUTHORIZED.value(), 
                e.getMessage(), 
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }
    
    /**
     * Handles IllegalArgumentException (validation errors)
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException e) {
        ErrorResponse error = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(), 
                e.getMessage(), 
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }
    
    /**
     * Handles IllegalStateException (business logic violations)
     */
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorResponse> handleIllegalStateException(IllegalStateException e) {
        ErrorResponse error = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(), 
                e.getMessage(), 
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }
    
    /**
     * Handles MethodArgumentNotValidException (validation errors from @Valid)
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(MethodArgumentNotValidException e) {
        Map<String, String> errors = new HashMap<>();
        e.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        
        Map<String, Object> response = new HashMap<>();
        response.put("status", HttpStatus.BAD_REQUEST.value());
        response.put("message", "Validation failed");
        response.put("errors", errors);
        response.put("timestamp", LocalDateTime.now());
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
    
    /**
     * Handles generic RuntimeException
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntimeException(RuntimeException e) {
        ErrorResponse error = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(), 
                e.getMessage(), 
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }
    
    /**
     * Handles generic Exception
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception e) {
        ErrorResponse error = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(), 
                "An unexpected error occurred: " + e.getMessage(), 
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
    
    /**
     * Error response DTO
     */
    public static class ErrorResponse {
        private int status;
        private String message;
        private LocalDateTime timestamp;
        
        public ErrorResponse(int status, String message, LocalDateTime timestamp) {
            this.status = status;
            this.message = message;
            this.timestamp = timestamp;
        }
        
        // Getters and setters
        public int getStatus() {
            return status;
        }
        
        public void setStatus(int status) {
            this.status = status;
        }
        
        public String getMessage() {
            return message;
        }
        
        public void setMessage(String message) {
            this.message = message;
        }
        
        public LocalDateTime getTimestamp() {
            return timestamp;
        }
        
        public void setTimestamp(LocalDateTime timestamp) {
            this.timestamp = timestamp;
        }
    }
}

