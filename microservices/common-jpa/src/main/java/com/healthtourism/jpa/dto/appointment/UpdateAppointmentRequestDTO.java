package com.healthtourism.jpa.dto.appointment;

import com.healthtourism.jpa.dto.BaseRequestDTO;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Update Appointment Request DTO
 * 
 * Used for updating existing appointments.
 * All fields are optional - only provided fields will be updated.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class UpdateAppointmentRequestDTO extends BaseRequestDTO {
    
    /**
     * New appointment date (optional)
     */
    @Future(message = "Appointment date must be in the future")
    private LocalDateTime appointmentDate;
    
    /**
     * New doctor ID (optional)
     */
    private UUID doctorId;
    
    /**
     * New hospital ID (optional)
     */
    private UUID hospitalId;
    
    /**
     * New accommodation ID (optional)
     */
    private UUID accommodationId;
    
    /**
     * New transfer service ID (optional)
     */
    private UUID transferId;
    
    /**
     * New check-in date (optional)
     */
    private LocalDateTime checkInDate;
    
    /**
     * New check-out date (optional)
     */
    private LocalDateTime checkOutDate;
    
    /**
     * Updated notes (optional)
     */
    @Size(max = 1000, message = "Notes cannot exceed 1000 characters")
    private String notes;
    
    /**
     * New status (optional): PENDING, CONFIRMED, CANCELLED, COMPLETED
     */
    private String status;
}

