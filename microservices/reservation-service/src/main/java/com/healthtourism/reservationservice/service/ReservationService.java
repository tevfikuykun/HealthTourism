package com.healthtourism.reservationservice.service;
import com.healthtourism.reservationservice.dto.ReservationDTO;
import com.healthtourism.reservationservice.dto.ReservationRequestDTO;
import com.healthtourism.reservationservice.entity.Reservation;
import com.healthtourism.reservationservice.repository.ReservationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ReservationService {
    @Autowired
    private ReservationRepository reservationRepository;
    
    @Autowired
    private KafkaEventService kafkaEventService;
    
    @Autowired
    private EventStoreService eventStoreService;
    
    @Transactional
    public ReservationDTO createReservation(ReservationRequestDTO request) {
        LocalDateTime appointmentEnd = request.getAppointmentDate().plusHours(1);
        List<Reservation> conflicting = reservationRepository.findConflictingReservations(
                request.getDoctorId(), request.getAppointmentDate(), appointmentEnd);
        if (!conflicting.isEmpty()) {
            throw new RuntimeException("Bu saatte başka bir randevu var");
        }
        
        long nights = ChronoUnit.DAYS.between(request.getCheckInDate(), request.getCheckOutDate());
        if (nights <= 0) {
            throw new RuntimeException("Geçersiz konaklama tarihleri");
        }
        
        Reservation reservation = new Reservation();
        reservation.setReservationNumber(UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        reservation.setUserId(request.getUserId());
        reservation.setHospitalId(request.getHospitalId());
        reservation.setDoctorId(request.getDoctorId());
        reservation.setAccommodationId(request.getAccommodationId());
        reservation.setAppointmentDate(request.getAppointmentDate());
        reservation.setCheckInDate(request.getCheckInDate());
        reservation.setCheckOutDate(request.getCheckOutDate());
        reservation.setNumberOfNights((int) nights);
        reservation.setTotalPrice(BigDecimal.valueOf(1000.0)); // Simülasyon - gerçekte hesaplanacak
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
                .orElseThrow(() -> new RuntimeException("Rezervasyon bulunamadı"));
        return convertToDTO(reservation);
    }
    
    @Transactional
    public ReservationDTO updateReservationStatus(Long id, String status) {
        Reservation reservation = reservationRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Rezervasyon bulunamadı"));
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

