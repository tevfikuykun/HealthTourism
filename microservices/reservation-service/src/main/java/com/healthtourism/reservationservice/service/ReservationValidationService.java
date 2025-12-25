package com.healthtourism.reservationservice.service;

import com.healthtourism.reservationservice.entity.Reservation;
import com.healthtourism.reservationservice.entity.ReservationStatus;
import com.healthtourism.reservationservice.exception.ReservationConflictException;
import com.healthtourism.reservationservice.exception.BusinessRuleException;
import com.healthtourism.reservationservice.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

/**
 * Reservation Validation Service
 * 
 * Centralized validation logic for reservations.
 * Separates validation concerns from business logic.
 * 
 * Business Rules:
 * - Appointment conflict detection
 * - User daily limit validation
 * - Date validation
 * - Doctor-hospital matching
 */
@Service
@RequiredArgsConstructor
public class ReservationValidationService {
    
    private static final Logger logger = LoggerFactory.getLogger(ReservationValidationService.class);
    
    // Active statuses that should be considered for conflict detection
    private static final List<ReservationStatus> ACTIVE_STATUSES = Arrays.asList(
        ReservationStatus.PENDING,
        ReservationStatus.CONFIRMED
    );
    
    // Default appointment duration in minutes
    private static final int DEFAULT_APPOINTMENT_DURATION_MINUTES = 60;
    
    // Maximum reservations per user per day (business rule)
    private static final int MAX_RESERVATIONS_PER_USER_PER_DAY = 1;
    
    private final ReservationRepository reservationRepository;
    
    /**
     * Validate appointment time for conflicts
     * 
     * Business Rule: A doctor cannot have two appointments at the same time
     * 
     * @param doctorId Doctor ID
     * @param appointmentDate Proposed appointment date
     * @param excludeReservationId Reservation ID to exclude (for updates)
     * @throws ReservationConflictException if conflict found
     */
    public void validateAppointmentTime(
            Long doctorId,
            LocalDateTime appointmentDate,
            Long excludeReservationId) {
        
        logger.debug("Validating appointment time: doctorId={}, date={}", doctorId, appointmentDate);
        
        // Validate appointment date is in the future
        if (appointmentDate.isBefore(LocalDateTime.now())) {
            throw new BusinessRuleException("Appointment date cannot be in the past");
        }
        
        // Check for conflicts
        LocalDateTime startTime = appointmentDate.minusMinutes(DEFAULT_APPOINTMENT_DURATION_MINUTES);
        LocalDateTime endTime = appointmentDate.plusMinutes(DEFAULT_APPOINTMENT_DURATION_MINUTES);
        
        List<Reservation> conflicts = reservationRepository.findConflictingAppointments(
            doctorId,
            startTime,
            endTime,
            ACTIVE_STATUSES,
            excludeReservationId
        );
        
        if (!conflicts.isEmpty()) {
            Reservation conflict = conflicts.get(0);
            logger.warn("Appointment conflict detected: doctorId={}, date={}, existing={}",
                doctorId, appointmentDate, conflict.getReservationNumber());
            throw new ReservationConflictException(
                String.format(
                    "Doctor %d already has an appointment at %s (Reservation: %s)",
                    doctorId,
                    conflict.getAppointmentDate(),
                    conflict.getReservationNumber()
                )
            );
        }
        
        logger.debug("Appointment time validation passed");
    }
    
    /**
     * Validate user daily limit
     * 
     * Business Rule: Optional - prevent users from booking multiple appointments on same day
     * 
     * @param userId User ID
     * @param appointmentDate Appointment date
     * @throws BusinessRuleException if user already has appointment on same day
     */
    public void validateUserDailyLimit(Long userId, LocalDateTime appointmentDate) {
        logger.debug("Validating user daily limit: userId={}, date={}", userId, appointmentDate);
        
        long existingCount = reservationRepository.countByUserIdAndAppointmentDate(
            userId,
            appointmentDate,
            ACTIVE_STATUSES
        );
        
        if (existingCount >= MAX_RESERVATIONS_PER_USER_PER_DAY) {
            logger.warn("User daily limit exceeded: userId={}, date={}, count={}",
                userId, appointmentDate, existingCount);
            throw new BusinessRuleException(
                String.format(
                    "User %d already has %d appointment(s) on %s. Maximum allowed: %d",
                    userId,
                    existingCount,
                    appointmentDate.toLocalDate(),
                    MAX_RESERVATIONS_PER_USER_PER_DAY
                )
            );
        }
        
        logger.debug("User daily limit validation passed");
    }
    
    /**
     * Validate reservation dates
     * 
     * @param checkInDate Check-in date
     * @param checkOutDate Check-out date
     * @param appointmentDate Appointment date
     * @throws BusinessRuleException if dates are invalid
     */
    public void validateDates(
            LocalDateTime checkInDate,
            LocalDateTime checkOutDate,
            LocalDateTime appointmentDate) {
        
        logger.debug("Validating dates: checkIn={}, checkOut={}, appointment={}",
            checkInDate, checkOutDate, appointmentDate);
        
        // Check-in must be before check-out
        if (checkInDate != null && checkOutDate != null) {
            if (!checkOutDate.isAfter(checkInDate)) {
                throw new BusinessRuleException("Check-out date must be after check-in date");
            }
        }
        
        // Appointment should be between check-in and check-out (business rule)
        if (checkInDate != null && checkOutDate != null && appointmentDate != null) {
            if (appointmentDate.isBefore(checkInDate) || appointmentDate.isAfter(checkOutDate)) {
                throw new BusinessRuleException(
                    "Appointment date must be between check-in and check-out dates"
                );
            }
        }
        
        logger.debug("Date validation passed");
    }
    
    /**
     * Validate reservation can be cancelled
     * 
     * @param reservation Reservation to validate
     * @throws BusinessRuleException if reservation cannot be cancelled
     */
    public void validateCanBeCancelled(Reservation reservation) {
        if (reservation == null) {
            throw new BusinessRuleException("Reservation cannot be null");
        }
        
        if (!reservation.canBeCancelled()) {
            throw new BusinessRuleException(
                String.format("Reservation with status %s cannot be cancelled", reservation.getStatus())
            );
        }
        
        logger.debug("Cancellation validation passed for reservation: {}", reservation.getReservationNumber());
    }
    
    /**
     * Validate reservation can be confirmed
     * 
     * @param reservation Reservation to validate
     * @throws BusinessRuleException if reservation cannot be confirmed
     */
    public void validateCanBeConfirmed(Reservation reservation) {
        if (reservation == null) {
            throw new BusinessRuleException("Reservation cannot be null");
        }
        
        if (reservation.getStatus() != ReservationStatus.PENDING) {
            throw new BusinessRuleException(
                String.format("Only PENDING reservations can be confirmed. Current status: %s", 
                    reservation.getStatus())
            );
        }
        
        logger.debug("Confirmation validation passed for reservation: {}", reservation.getReservationNumber());
    }
    
    /**
     * Validate reservation can be completed
     * 
     * @param reservation Reservation to validate
     * @throws BusinessRuleException if reservation cannot be completed
     */
    public void validateCanBeCompleted(Reservation reservation) {
        if (reservation == null) {
            throw new BusinessRuleException("Reservation cannot be null");
        }
        
        if (reservation.getStatus() != ReservationStatus.CONFIRMED) {
            throw new BusinessRuleException(
                String.format("Only CONFIRMED reservations can be completed. Current status: %s",
                    reservation.getStatus())
            );
        }
        
        logger.debug("Completion validation passed for reservation: {}", reservation.getReservationNumber());
    }
}

