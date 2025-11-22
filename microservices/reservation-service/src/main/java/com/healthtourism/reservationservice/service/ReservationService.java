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
        
        return convertToDTO(reservationRepository.save(reservation));
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
        return convertToDTO(reservationRepository.save(reservation));
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

