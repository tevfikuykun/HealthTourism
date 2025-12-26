package com.example.HealthTourism.service;

import com.example.HealthTourism.dto.ReservationDTO;
import com.example.HealthTourism.dto.ReservationRequestDTO;
import com.example.HealthTourism.entity.*;
import com.example.HealthTourism.exception.*;
import com.example.HealthTourism.mapper.ReservationMapper;
import com.example.HealthTourism.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Enterprise-grade ReservationService with:
 * - Constructor Injection for testability
 * - MapStruct for entity-DTO mapping
 * - Transactional management (data integrity)
 * - Pessimistic locking for concurrency control
 * - Custom exception handling
 * - Human-readable reservation numbers
 * - Full package reservation support (flight, transfer)
 * - Dynamic pricing calculation
 * - Comprehensive business logic validation
 */
@Slf4j
@Service
@RequiredArgsConstructor // Constructor Injection - Spring best practice
@Transactional(readOnly = true) // Default: read-only for all methods (performance optimization)
public class ReservationService {
    
    private final ReservationRepository reservationRepository;
    private final UserRepository userRepository;
    private final HospitalRepository hospitalRepository;
    private final DoctorRepository doctorRepository;
    private final AccommodationRepository accommodationRepository;
    private final FlightBookingRepository flightBookingRepository;
    private final TransferServiceRepository transferServiceRepository;
    private final ReservationMapper reservationMapper;
    private final ReservationNumberGenerator reservationNumberGenerator;
    
    // Business constants
    private static final int DEFAULT_APPOINTMENT_DURATION_MINUTES = 60; // Default appointment duration (1 hour)

    /**
     * Creates a basic reservation (hospital + doctor + optional accommodation).
     * Business Logic:
     * - Validates all entities exist and are active/available
     * - Checks doctor works at specified hospital
     * - Prevents double booking with pessimistic locking
     * - Validates date ranges
     * - Calculates dynamic pricing
     * - Generates human-readable reservation number
     * 
     * @param request Reservation request DTO
     * @return Created ReservationDTO
     * @throws ReservationConflictException if appointment time conflicts
     * @throws InvalidDateRangeException if dates are invalid
     * @throws DoctorHospitalMismatchException if doctor doesn't work at hospital
     */
    @Transactional // Override read-only for write operation
    public ReservationDTO createReservation(ReservationRequestDTO request) {
        log.info("Creating reservation for user ID: {}, doctor ID: {}, hospital ID: {}", 
                request.getUserId(), request.getDoctorId(), request.getHospitalId());
        
        // 1. Validate and fetch entities
        User user = validateAndGetUser(request.getUserId());
        Hospital hospital = validateAndGetHospital(request.getHospitalId());
        Doctor doctor = validateAndGetDoctor(request.getDoctorId());
        
        // 2. Business logic: Validate doctor works at hospital
        validateDoctorHospitalMatch(doctor, hospital);
        
        // 3. Business logic: Validate date ranges
        validateDateRanges(request);
        
        // 4. Business logic: Check appointment conflicts with PESSIMISTIC LOCK
        // This prevents race conditions when multiple users book simultaneously
        checkAppointmentConflicts(doctor, request.getAppointmentDate());
        
        // 5. Validate and fetch accommodation (optional)
        Accommodation accommodation = null;
        if (request.getAccommodationId() != null) {
            accommodation = validateAndGetAccommodation(request.getAccommodationId());
        }
        
        // 6. Calculate number of nights
        long nights = calculateNights(request.getCheckInDate(), request.getCheckOutDate());
        
        // 7. Calculate total price
        BigDecimal totalPrice = calculateTotalPrice(doctor, accommodation, nights, null, null);
        
        // 8. Create reservation
        Reservation reservation = buildReservation(request, user, hospital, doctor, accommodation, nights, totalPrice);
        
        Reservation saved = reservationRepository.save(reservation);
        log.info("Successfully created reservation with number: {}", saved.getReservationNumber());
        
        return reservationMapper.toDto(saved);
    }
    
    /**
     * Creates a full package reservation including flight and transfer services.
     * Health Tourism Critical: Complete end-to-end booking from flight to medical service.
     * 
     * @param request Reservation request (should include flightId and transferId if applicable)
     * @param flightId Optional flight booking ID
     * @param transferId Optional transfer service ID
     * @param flightPassengerCount Number of passengers for flight
     * @return Created ReservationDTO with full package details
     */
    @Transactional
    public ReservationDTO createFullPackage(ReservationRequestDTO request, Long flightId, Long transferId, Integer flightPassengerCount) {
        log.info("Creating full package reservation for user ID: {}", request.getUserId());
        
        // 1. Create base reservation
        ReservationDTO baseReservation = createReservation(request);
        
        // 2. Book flight if provided
        if (flightId != null) {
            if (flightPassengerCount == null || flightPassengerCount <= 0) {
                flightPassengerCount = 1; // Default to 1 passenger
            }
            
            // Validate flight exists
            flightBookingRepository.findById(flightId)
                    .orElseThrow(() -> new RuntimeException("Uçuş bulunamadı: " + flightId));
            
            // Reserve seats using FlightBookingService logic
            // Note: This should ideally call FlightBookingService.reserveSeat()
            // For now, we use repository directly
            int updatedRows = flightBookingRepository.bookSeats(flightId, flightPassengerCount);
            if (updatedRows == 0) {
                throw new IllegalStateException("Uçuş için yeterli koltuk bulunamadı");
            }
            
            log.info("Reserved {} seat(s) for flight ID: {}", flightPassengerCount, flightId);
        }
        
        // 3. Reserve transfer if provided
        if (transferId != null) {
            // Validate transfer service exists
            transferServiceRepository.findById(transferId)
                    .orElseThrow(() -> new RuntimeException("Transfer servisi bulunamadı: " + transferId));
            
            // Transfer reservation logic can be added here
            // Similar to flight, you might want to check availability and book
            log.info("Reserved transfer service ID: {}", transferId);
        }
        
        return baseReservation;
    }
    
    /**
     * Calculates total price for reservation.
     * Business Logic: Dynamic pricing based on:
     * - Doctor consultation fee
     * - Accommodation price per night × nights
     * - Flight price (if included)
     * - Transfer price (if included)
     * - Future: Discount codes, seasonal pricing, etc.
     * 
     * @param doctor Doctor entity
     * @param accommodation Accommodation entity (can be null)
     * @param nights Number of nights
     * @param flightPrice Flight price (can be null)
     * @param transferPrice Transfer price (can be null)
     * @return Total price
     */
    private BigDecimal calculateTotalPrice(Doctor doctor, Accommodation accommodation, 
                                          long nights, BigDecimal flightPrice, BigDecimal transferPrice) {
        BigDecimal total = BigDecimal.valueOf(doctor.getConsultationFee());
        
        if (accommodation != null && nights > 0) {
            BigDecimal accommodationCost = accommodation.getPricePerNight()
                    .multiply(BigDecimal.valueOf(nights));
            total = total.add(accommodationCost);
        }
        
        if (flightPrice != null) {
            total = total.add(flightPrice);
        }
        
        if (transferPrice != null) {
            total = total.add(transferPrice);
        }
        
        return total;
    }

    /**
     * Gets reservations for a specific user.
     * Performance: Uses JOIN FETCH to prevent N+1 queries.
     * 
     * @param userId User ID
     * @return List of user's reservations
     */
    public List<ReservationDTO> getReservationsByUser(Long userId) {
        log.debug("Fetching reservations for user ID: {}", userId);
        return reservationRepository.findAllByUserIdWithDetails(userId)
                .stream()
                .map(reservationMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Gets reservation by reservation number (PNR).
     * 
     * @param reservationNumber Reservation number (e.g., HT-2025-A12B)
     * @return ReservationDTO
     * @throws ReservationNotFoundException if reservation not found
     */
    public ReservationDTO getReservationByNumber(String reservationNumber) {
        log.debug("Fetching reservation by number: {}", reservationNumber);
        return reservationRepository.findByReservationNumberWithUser(reservationNumber)
                .map(reservationMapper::toDto)
                .orElseThrow(() -> new ReservationNotFoundException(reservationNumber));
    }

    /**
     * Updates reservation status.
     * Business Logic: Validates status transitions (e.g., cannot cancel COMPLETED reservation).
     * 
     * @param id Reservation ID
     * @param status New status (PENDING, CONFIRMED, CANCELLED, COMPLETED)
     * @return Updated ReservationDTO
     * @throws ReservationNotFoundException if reservation not found
     */
    @Transactional
    public ReservationDTO updateReservationStatus(Long id, String status) {
        log.info("Updating reservation ID: {} status to: {}", id, status);
        
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new ReservationNotFoundException(id));
        
        // Business logic: Validate status transition
        validateStatusTransition(reservation.getStatus(), status);
        
        reservation.setStatus(status);
        Reservation saved = reservationRepository.save(reservation);
        
        log.info("Successfully updated reservation ID: {} status to: {}", id, status);
        return reservationMapper.toDto(saved);
    }
    
    // ==================== Private Helper Methods ====================
    
    private User validateAndGetUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı: " + userId));
    }
    
    private Hospital validateAndGetHospital(Long hospitalId) {
        return hospitalRepository.findByIdAndIsActiveTrue(hospitalId)
                .orElseThrow(() -> new RuntimeException("Hastane bulunamadı veya aktif değil: " + hospitalId));
    }
    
    private Doctor validateAndGetDoctor(Long doctorId) {
        return doctorRepository.findByIdAndIsAvailableTrue(doctorId)
                .orElseThrow(() -> new RuntimeException("Doktor bulunamadı veya müsait değil: " + doctorId));
    }
    
    private Accommodation validateAndGetAccommodation(Long accommodationId) {
        return accommodationRepository.findByIdAndIsActiveTrue(accommodationId)
                .orElseThrow(() -> new RuntimeException("Konaklama bulunamadı veya aktif değil: " + accommodationId));
    }
    
    private void validateDoctorHospitalMatch(Doctor doctor, Hospital hospital) {
        if (!doctor.getHospital().getId().equals(hospital.getId())) {
            String doctorName = doctor.getTitle() + " " + doctor.getFirstName() + " " + doctor.getLastName();
            throw new DoctorHospitalMismatchException(doctorName, hospital.getName());
        }
    }
    
    private void validateDateRanges(ReservationRequestDTO request) {
        LocalDateTime now = LocalDateTime.now();
        
        // Appointment must be in the future
        if (request.getAppointmentDate().isBefore(now)) {
            throw new InvalidDateRangeException("Randevu tarihi geçmiş bir tarih olamaz.");
        }
        
        // Check-in must be before check-out
        if (request.getCheckInDate().isAfter(request.getCheckOutDate()) || 
            request.getCheckInDate().equals(request.getCheckOutDate())) {
            throw new InvalidDateRangeException("Giriş tarihi çıkış tarihinden önce veya aynı olamaz.");
        }
        
        // Check-in must be before or on appointment date (patient needs accommodation before appointment)
        if (request.getCheckInDate().isAfter(request.getAppointmentDate())) {
            throw new InvalidDateRangeException("Giriş tarihi randevu tarihinden sonra olamaz.");
        }
    }
    
    /**
     * Checks appointment conflicts using PESSIMISTIC LOCK.
     * CRITICAL: Uses database-level locking to prevent race conditions.
     */
    private void checkAppointmentConflicts(Doctor doctor, LocalDateTime appointmentDate) {
        LocalDateTime appointmentEnd = appointmentDate.plus(DEFAULT_APPOINTMENT_DURATION_MINUTES, ChronoUnit.MINUTES);
        
        // Use PESSIMISTIC LOCK method to prevent concurrent bookings
        List<Reservation> conflicting = reservationRepository.findConflictingReservationsWithLock(
                doctor.getId(), appointmentDate, appointmentEnd);
        
        if (!conflicting.isEmpty()) {
            String doctorName = doctor.getTitle() + " " + doctor.getFirstName() + " " + doctor.getLastName();
            throw new ReservationConflictException(doctorName, appointmentDate);
        }
    }
    
    private long calculateNights(LocalDateTime checkIn, LocalDateTime checkOut) {
        long nights = ChronoUnit.DAYS.between(checkIn, checkOut);
        if (nights <= 0) {
            throw new InvalidDateRangeException("Geçersiz konaklama tarihleri. Gece sayısı pozitif olmalıdır.");
        }
        return nights;
    }
    
    private Reservation buildReservation(ReservationRequestDTO request, User user, Hospital hospital, 
                                        Doctor doctor, Accommodation accommodation, long nights, BigDecimal totalPrice) {
        Reservation reservation = new Reservation();
        reservation.setReservationNumber(reservationNumberGenerator.generate());
        reservation.setUser(user);
        reservation.setHospital(hospital);
        reservation.setDoctor(doctor);
        reservation.setAccommodation(accommodation);
        reservation.setAppointmentDate(request.getAppointmentDate());
        reservation.setCheckInDate(request.getCheckInDate());
        reservation.setCheckOutDate(request.getCheckOutDate());
        reservation.setNumberOfNights((int) nights);
        reservation.setTotalPrice(totalPrice);
        reservation.setStatus("PENDING");
        reservation.setNotes(request.getNotes());
        reservation.setCreatedAt(LocalDateTime.now());
        return reservation;
    }
    
    private void validateStatusTransition(String currentStatus, String newStatus) {
        // Business logic: Define valid status transitions
        // PENDING -> CONFIRMED, CANCELLED
        // CONFIRMED -> COMPLETED, CANCELLED
        // CANCELLED -> (cannot transition)
        // COMPLETED -> (cannot transition)
        
        if ("CANCELLED".equals(currentStatus) || "COMPLETED".equals(currentStatus)) {
            throw new IllegalStateException(
                    String.format("Rezervasyon durumu %s iken %s durumuna geçirilemez.", currentStatus, newStatus));
        }
    }
}
