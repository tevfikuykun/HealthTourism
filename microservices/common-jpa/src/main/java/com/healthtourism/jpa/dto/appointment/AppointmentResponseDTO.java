package com.healthtourism.jpa.dto.appointment;

import com.healthtourism.jpa.dto.BaseResponseDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Appointment Response DTO
 * 
 * Used for returning appointment data to clients.
 * Includes all appointment information with audit fields.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AppointmentResponseDTO extends BaseResponseDTO {
    
    private UUID id;
    
    /**
     * Unique appointment/reservation number
     */
    private String appointmentNumber;
    
    /**
     * Patient information
     */
    private UUID patientId;
    private String patientName;
    private String patientEmail;
    
    /**
     * Doctor information
     */
    private UUID doctorId;
    private String doctorName;
    private String doctorSpecialization;
    
    /**
     * Hospital information
     */
    private UUID hospitalId;
    private String hospitalName;
    
    /**
     * Appointment details
     */
    private LocalDateTime appointmentDate;
    private String status; // PENDING, CONFIRMED, CANCELLED, COMPLETED
    
    /**
     * Accommodation information (if applicable)
     */
    private UUID accommodationId;
    private String accommodationName;
    private LocalDateTime checkInDate;
    private LocalDateTime checkOutDate;
    private Integer numberOfNights;
    
    /**
     * Transfer information (if applicable)
     */
    private UUID transferId;
    private String transferServiceName;
    
    /**
     * Pricing information
     */
    private BigDecimal totalPrice;
    private BigDecimal doctorFee;
    private BigDecimal accommodationFee;
    private BigDecimal transferFee;
    
    /**
     * Additional information
     */
    private String notes;
    
    /**
     * Audit fields
     */
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;
}

