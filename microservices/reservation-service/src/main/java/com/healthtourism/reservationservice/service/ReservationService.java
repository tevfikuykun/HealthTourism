package com.healthtourism.reservationservice.service;

import com.healthtourism.reservationservice.dto.ReservationDTO;
import com.healthtourism.reservationservice.dto.ReservationRequestDTO;
import com.healthtourism.reservationservice.entity.Reservation;
import com.healthtourism.reservationservice.exception.InsufficientCapacityException;
import com.healthtourism.reservationservice.exception.ResourceNotFoundException;
import com.healthtourism.reservationservice.repository.ReservationRepository;
import com.healthtourism.reservationservice.service.PriceCalculationService;
import com.healthtourism.reservationservice.util.ReservationNumberGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReservationService {
    @Autowired
    private ReservationRepository reservationRepository;
    
    @Autowired
    private KafkaEventService kafkaEventService;
    
    @Autowired
    private EventStoreService eventStoreService;
    
    @Autowired
    private ReservationNumberGenerator reservationNumberGenerator;
    
    @Autowired
    private PriceCalculationService priceCalculationService;
    
    @Transactional
    public ReservationDTO createReservation(ReservationRequestDTO request) {
        // Validate appointment date is not in the past
        if (request.getAppointmentDate().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Appointment date cannot be in the past");
        }
        
        // Check for appointment conflicts
        LocalDateTime appointmentEnd = request.getAppointmentDate().plusHours(1);
        List<Reservation> conflicting = reservationRepository.findConflictingReservations(
                request.getDoctorId(), request.getAppointmentDate(), appointmentEnd);
        if (!conflicting.isEmpty()) {
            throw new InsufficientCapacityException(
                "Bu saatte başka bir randevu var. Lütfen farklı bir saat seçin.");
        }
        
        // Calculate number of nights
        long nights = ChronoUnit.DAYS.between(request.getCheckInDate(), request.getCheckOutDate());
        if (nights <= 0) {
            throw new IllegalArgumentException("Geçersiz konaklama tarihleri. Check-out tarihi check-in tarihinden sonra olmalıdır.");
        }
        
        // Validate check-in date is not before appointment date
        if (request.getCheckInDate().isBefore(request.getAppointmentDate().toLocalDate().atStartOfDay())) {
            throw new IllegalArgumentException("Check-in tarihi randevu tarihinden önce olamaz.");
        }
        
        // Generate unique reservation number
        String reservationNumber = reservationNumberGenerator.generateReservationNumber();
        
        // Calculate total price using PriceCalculationService
        BigDecimal totalPrice = priceCalculationService.calculateTotalPrice(
            request.getDoctorId(),
            request.getAccommodationId(),
            (int) nights,
            request.getTransferId()
        );
        
        // Create reservation entity
        Reservation reservation = new Reservation();
        reservation.setReservationNumber(reservationNumber);
        reservation.setUserId(request.getUserId());
        reservation.setHospitalId(request.getHospitalId());
        reservation.setDoctorId(request.getDoctorId());
        reservation.setAccommodationId(request.getAccommodationId());
        reservation.setAppointmentDate(request.getAppointmentDate());
        reservation.setCheckInDate(request.getCheckInDate());
        reservation.setCheckOutDate(request.getCheckOutDate());
        reservation.setNumberOfNights((int) nights);
        reservation.setTotalPrice(totalPrice);
        reservation.setStatus("PENDING");
        reservation.setNotes(request.getNotes());
        reservation.setCreatedAt(LocalDateTime.now());
        
        Reservation saved = reservationRepository.save(reservation);
        
        // Save event to event store
        com.healthtourism.reservationservice.event.ReservationEvent event = 
            new com.healthtourism.reservationservice.event.ReservationEvent();
        event.setEventType("RESERVATION_CREATED");
        event.setReservationId(saved.getId());
        event.setReservationNumber(saved.getReservationNumber());
        event.setUserId(saved.getUserId());
        event.setHospitalId(saved.getHospitalId());
        event.setDoctorId(saved.getDoctorId());
        event.setAccommodationId(saved.getAccommodationId());
        event.setAppointmentDate(saved.getAppointmentDate());
        event.setCheckInDate(saved.getCheckInDate());
        event.setCheckOutDate(saved.getCheckOutDate());
        event.setNumberOfNights(saved.getNumberOfNights());
        event.setTotalPrice(saved.getTotalPrice());
        event.setStatus(saved.getStatus());
        event.setNotes(saved.getNotes());
        event.setEventTimestamp(java.time.LocalDateTime.now());
        event.setEventId(java.util.UUID.randomUUID().toString());
        
        eventStoreService.saveEvent("RESERVATION_CREATED", saved.getId(), event, 1L);
        
        // Publish event to Kafka
        kafkaEventService.publishReservationCreated(saved.getId(), saved.getUserId(), saved.getHospitalId());
        
        return convertToDTO(saved);
    }
    
    public List<ReservationDTO> getReservationsByUser(Long userId) {
        return reservationRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream().map(this::convertToDTO).collect(Collectors.toList());
    }
    
    public ReservationDTO getReservationByNumber(String reservationNumber) {
        Reservation reservation = reservationRepository.findByReservationNumber(reservationNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Rezervasyon bulunamadı: " + reservationNumber));
        return convertToDTO(reservation);
    }
    
    public ReservationDTO getReservationById(Long id) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Rezervasyon bulunamadı: " + id));
        return convertToDTO(reservation);
    }
    
    @Transactional
    public ReservationDTO updateReservationStatus(Long id, String status) {
        Reservation reservation = reservationRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Rezervasyon bulunamadı: " + id));
        reservation.setStatus(status);
        Reservation saved = reservationRepository.save(reservation);
        
        // Save event to event store
        com.healthtourism.reservationservice.event.ReservationEvent event = 
            new com.healthtourism.reservationservice.event.ReservationEvent();
        event.setEventType("RESERVATION_UPDATED");
        event.setReservationId(saved.getId());
        event.setReservationNumber(saved.getReservationNumber());
        event.setStatus(saved.getStatus());
        event.setEventTimestamp(java.time.LocalDateTime.now());
        event.setEventId(java.util.UUID.randomUUID().toString());
        
        // Get current version from event store
        java.util.List<com.healthtourism.reservationservice.entity.ReservationEventStore> events = 
            eventStoreService.getEventsByReservationId(saved.getId());
        Long version = events.stream()
            .mapToLong(e -> e.getAggregateVersion())
            .max()
            .orElse(0L) + 1;
        eventStoreService.saveEvent("RESERVATION_UPDATED", saved.getId(), event, version);
        
        // Publish event to Kafka
        kafkaEventService.publishReservationUpdated(saved.getId(), saved.getStatus());
        
        return convertToDTO(saved);
    }
    
    private ReservationDTO convertToDTO(Reservation reservation) {
        ReservationDTO dto = new ReservationDTO();
        dto.setId(reservation.getId());
        dto.setReservationNumber(reservation.getReservationNumber());
        dto.setAppointmentDate(reservation.getAppointmentDate());
        dto.setCheckInDate(reservation.getCheckInDate());
        dto.setCheckOutDate(reservation.getCheckOutDate());
        dto.setNumberOfNights(reservation.getNumberOfNights());
        dto.setTotalPrice(reservation.getTotalPrice());
        dto.setStatus(reservation.getStatus());
        dto.setNotes(reservation.getNotes());
        dto.setCreatedAt(reservation.getCreatedAt());
        dto.setUserId(reservation.getUserId());
        dto.setHospitalId(reservation.getHospitalId());
        dto.setDoctorId(reservation.getDoctorId());
        dto.setAccommodationId(reservation.getAccommodationId());
        return dto;
    }
}

