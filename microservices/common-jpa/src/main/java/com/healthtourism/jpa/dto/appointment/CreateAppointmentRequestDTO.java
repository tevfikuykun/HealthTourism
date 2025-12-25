package com.healthtourism.jpa.dto.appointment;

import com.healthtourism.jpa.dto.BaseRequestDTO;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Create Appointment Request DTO
 * 
 * Used for creating new appointments.
 * Contains all required and optional fields for appointment creation.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class CreateAppointmentRequestDTO extends BaseRequestDTO {
    
    @NotNull(message = "Patient ID is required")
    private UUID patientId;
    
    @NotNull(message = "Doctor ID is required")
    private UUID doctorId;
    
    @NotNull(message = "Hospital ID is required")
    private UUID hospitalId;
    
    @NotNull(message = "Appointment date is required")
    @Future(message = "Appointment date must be in the future")
    private LocalDateTime appointmentDate;
    
    /**
     * Accommodation ID (optional)
     */
    private UUID accommodationId;
    
    /**
     * Transfer service ID (optional)
     */
    private UUID transferId;
    
    /**
     * Check-in date (optional, for accommodation)
     */
    private LocalDateTime checkInDate;
    
    /**
     * Check-out date (optional, for accommodation)
     */
    private LocalDateTime checkOutDate;
    
    /**
     * Notes (optional)
     */
    @Size(max = 1000, message = "Notes cannot exceed 1000 characters")
    private String notes;
}

