package com.healthtourism.reservationservice.service;

import com.healthtourism.jpa.service.IdempotencyService;
import com.healthtourism.jpa.util.SecurityContextHelper;
import com.healthtourism.reservationservice.dto.ReservationDTO;
import com.healthtourism.reservationservice.dto.ReservationRequestDTO;
import com.healthtourism.reservationservice.entity.Reservation;
import com.healthtourism.reservationservice.entity.ReservationStatus;
import com.healthtourism.reservationservice.exception.BusinessRuleException;
import com.healthtourism.reservationservice.exception.ReservationConflictException;
import com.healthtourism.reservationservice.exception.ReservationNotFoundException;
import com.healthtourism.reservationservice.mapper.ReservationMapper;
import com.healthtourism.reservationservice.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Reservation Service - Professional Enterprise Implementation
 * 
 * Best Practices Applied:
 * - Pagination support (Pageable)
 * - Idempotency support (prevent duplicate operations)
 * - MapStruct for Entity-DTO conversion
 * - Business rule validation
 * - Custom exceptions
 * - Transactional methods
 * - Comprehensive logging
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ReservationService {
    
    private final ReservationRepository reservationRepository;
    private final ReservationMapper reservationMapper;
    private final PriceCalculationService priceCalculationService;
    private final ReservationValidationService validationService;
    private final ReservationStateMachine stateMachine;
    private final NotificationService notificationService;
    private final IdempotencyService idempotencyService;
    
    /**
     * Create a new reservation with idempotency support
     * 
     * @param request Reservation request
     * @param userId User ID (from SecurityContext)
     * @param idempotencyKey Idempotency key (optional)
     * @return Created reservation DTO
     */
    @Transactional
    public ReservationDTO createReservation(ReservationRequestDTO request, UUID userId, String idempotencyKey) {
        log.info("Creating reservation for user: {} (idempotency key: {})", userId, idempotencyKey);
        
        // Idempotency check: If key exists, return cached result
        if (idempotencyKey != null && !idempotencyKey.trim().isEmpty()) {
            if (idempotencyService.exists(idempotencyKey)) {
                log.info("Duplicate request detected (idempotency key: {}). Returning cached result.", idempotencyKey);
                Object cachedResult = idempotencyService.getCachedResult(idempotencyKey)
                    .orElseThrow(() -> new BusinessRuleException("Idempotency key exists but no cached result found"));
                return (ReservationDTO) cachedResult;
            }
        }
        
        // Convert DTO to Entity
        Reservation reservation = reservationMapper.toEntity(request);
        reservation.setUserId(userId);
        
        // Business validations
        validationService.validateAppointmentTime(reservation.getDoctorId(), reservation.getAppointmentDate(), null);
        validationService.validateDates(reservation.getCheckInDate(), reservation.getCheckOutDate(), reservation.getAppointmentDate());
        
        // Calculate price
        BigDecimal doctorFee = priceCalculationService.getDoctorConsultationFee(reservation.getDoctorId());
        BigDecimal accommodationPrice = reservation.getAccommodationId() != null ?
            priceCalculationService.getAccommodationPricePerNight(reservation.getAccommodationId()) : null;
        
        reservation.calculateTotal(doctorFee, accommodationPrice);
        reservation.setStatus(ReservationStatus.PENDING);
        
        Reservation savedReservation = reservationRepository.save(reservation);
        
        // Store idempotency key with result
        if (idempotencyKey != null && !idempotencyKey.trim().isEmpty()) {
            ReservationDTO resultDTO = reservationMapper.toDTO(savedReservation);
            idempotencyService.store(idempotencyKey, resultDTO);
        }
        
        log.info("Reservation created successfully: {}", savedReservation.getReservationNumber());
        
        return reservationMapper.toDTO(savedReservation);
    }
    
    /**
     * Get reservations by user ID with pagination
     * 
     * @param userId User ID
     * @param pageable Pagination and sorting parameters
     * @return Page of reservation DTOs
     */
    public Page<ReservationDTO> getReservationsByUserId(UUID userId, Pageable pageable) {
        log.debug("Fetching reservations for user: {} (page: {}, size: {})", 
            userId, pageable.getPageNumber(), pageable.getPageSize());
        
        Page<Reservation> reservations = reservationRepository.findByUserId(userId, pageable);
        return reservations.map(reservationMapper::toDTO);
    }
    
    /**
     * Get reservation by ID with ownership verification
     * 
     * @param id Reservation ID
     * @param userId User ID (for ownership check)
     * @return Reservation DTO
     */
    public ReservationDTO getReservationById(UUID id, UUID userId) {
        log.debug("Fetching reservation: {} for user: {}", id, userId);
        
        Reservation reservation = reservationRepository.findById(id)
            .orElseThrow(() -> new ReservationNotFoundException(id));
        
        // Ownership verification
        if (!reservation.getUserId().equals(userId) && !SecurityContextHelper.hasRole("ADMIN")) {
            throw new BusinessRuleException("Bu rezervasyona erişim yetkiniz yok");
        }
        
        return reservationMapper.toDTO(reservation);
    }
    
    /**
     * Cancel reservation with ownership verification
     * 
     * @param id Reservation ID
     * @param userId User ID (for ownership check)
     * @return Cancelled reservation DTO
     */
    @Transactional
    public ReservationDTO cancelReservation(UUID id, UUID userId) {
        log.info("Cancelling reservation: {} by user: {}", id, userId);
        
        Reservation reservation = reservationRepository.findById(id)
            .orElseThrow(() -> new ReservationNotFoundException(id));
        
        // Ownership verification
        if (!reservation.getUserId().equals(userId)) {
            throw new BusinessRuleException("Bu rezervasyonu iptal etme yetkiniz yok");
        }
        
        // Business rule: Check if can be cancelled
        if (!reservation.canBeCancelled()) {
            throw new BusinessRuleException("Bu rezervasyon iptal edilemez (durum: " + reservation.getStatus() + ")");
        }
        
        reservation.cancel();
        Reservation savedReservation = reservationRepository.save(reservation);
        
        log.info("Reservation cancelled successfully: {}", savedReservation.getReservationNumber());
        
        return reservationMapper.toDTO(savedReservation);
    }
    
    /**
     * Confirm reservation (admin only)
     * 
     * @param id Reservation ID
     * @return Confirmed reservation DTO
     */
    @Transactional
    public ReservationDTO confirmReservation(UUID id) {
        log.info("Confirming reservation: {}", id);
        
        Reservation reservation = reservationRepository.findById(id)
            .orElseThrow(() -> new ReservationNotFoundException(id));
        
        reservation.confirm();
        Reservation savedReservation = reservationRepository.save(reservation);
        
        log.info("Reservation confirmed successfully: {}", savedReservation.getReservationNumber());
        
        return reservationMapper.toDTO(savedReservation);
    }
    
    /**
     * Update reservation status (admin only)
     * 
     * @param id Reservation ID
     * @param status New status
     * @return Updated reservation DTO
     */
    @Transactional
    public ReservationDTO updateReservationStatus(UUID id, ReservationStatus status) {
        log.info("Updating reservation status: {} to {}", id, status);
        
        Reservation reservation = reservationRepository.findById(id)
            .orElseThrow(() -> new ReservationNotFoundException(id));
        
        stateMachine.validateTransition(reservation.getStatus(), status);
        reservation.setStatus(status);
        
        Reservation savedReservation = reservationRepository.save(reservation);
        
        log.info("Reservation status updated successfully: {}", savedReservation.getReservationNumber());
        
        return reservationMapper.toDTO(savedReservation);
    }
}
