package com.example.HealthTourism.service;

import com.example.HealthTourism.dto.ReservationDTO;
import com.example.HealthTourism.dto.ReservationRequestDTO;
import com.example.HealthTourism.entity.*;
import com.example.HealthTourism.repository.*;
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
    private UserRepository userRepository;
    
    @Autowired
    private HospitalRepository hospitalRepository;
    
    @Autowired
    private DoctorRepository doctorRepository;
    
    @Autowired
    private AccommodationRepository accommodationRepository;
    
    @Transactional
    public ReservationDTO createReservation(ReservationRequestDTO request) {
        // Kullanıcı kontrolü
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı"));
        
        // Hastane kontrolü
        Hospital hospital = hospitalRepository.findByIdAndIsActiveTrue(request.getHospitalId())
                .orElseThrow(() -> new RuntimeException("Hastane bulunamadı"));
        
        // Doktor kontrolü
        Doctor doctor = doctorRepository.findByIdAndIsAvailableTrue(request.getDoctorId())
                .orElseThrow(() -> new RuntimeException("Doktor bulunamadı"));
        
        // Doktorun bu hastanede çalıştığını kontrol et
        if (!doctor.getHospital().getId().equals(hospital.getId())) {
            throw new RuntimeException("Doktor bu hastanede çalışmıyor");
        }
        
        // Randevu çakışması kontrolü
        LocalDateTime appointmentEnd = request.getAppointmentDate().plusHours(1);
        List<Reservation> conflicting = reservationRepository.findConflictingReservations(
                doctor.getId(), request.getAppointmentDate(), appointmentEnd);
        if (!conflicting.isEmpty()) {
            throw new RuntimeException("Bu saatte başka bir randevu var");
        }
        
        // Konaklama kontrolü (opsiyonel)
        Accommodation accommodation = null;
        if (request.getAccommodationId() != null) {
            accommodation = accommodationRepository.findByIdAndIsActiveTrue(request.getAccommodationId())
                    .orElseThrow(() -> new RuntimeException("Konaklama bulunamadı"));
        }
        
        // Gece sayısını hesapla
        long nights = ChronoUnit.DAYS.between(request.getCheckInDate(), request.getCheckOutDate());
        if (nights <= 0) {
            throw new RuntimeException("Geçersiz konaklama tarihleri");
        }
        
        // Toplam fiyatı hesapla
        BigDecimal totalPrice = doctor.getConsultationFee();
        if (accommodation != null) {
            totalPrice = totalPrice.add(accommodation.getPricePerNight().multiply(BigDecimal.valueOf(nights)));
        }
        
        // Rezervasyon oluştur
        Reservation reservation = new Reservation();
        reservation.setReservationNumber(UUID.randomUUID().toString().substring(0, 8).toUpperCase());
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
        
        Reservation saved = reservationRepository.save(reservation);
        return convertToDTO(saved);
    }
    
    public List<ReservationDTO> getReservationsByUser(Long userId) {
        return reservationRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
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
        dto.setUserId(reservation.getUser().getId());
        dto.setUserName(reservation.getUser().getFirstName() + " " + reservation.getUser().getLastName());
        dto.setHospitalId(reservation.getHospital().getId());
        dto.setHospitalName(reservation.getHospital().getName());
        dto.setDoctorId(reservation.getDoctor().getId());
        dto.setDoctorName(reservation.getDoctor().getTitle() + " " + 
                          reservation.getDoctor().getFirstName() + " " + 
                          reservation.getDoctor().getLastName());
        if (reservation.getAccommodation() != null) {
            dto.setAccommodationId(reservation.getAccommodation().getId());
            dto.setAccommodationName(reservation.getAccommodation().getName());
        }
        return dto;
    }

}

