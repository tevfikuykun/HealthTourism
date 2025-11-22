package com.example.HealthTourism.repository;

import com.example.HealthTourism.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findByUserId(Long userId);
    
    List<Reservation> findByHospitalId(Long hospitalId);
    
    List<Reservation> findByDoctorId(Long doctorId);
    
    Optional<Reservation> findByReservationNumber(String reservationNumber);
    
    @Query("SELECT r FROM Reservation r WHERE r.doctor.id = :doctorId AND r.appointmentDate BETWEEN :startDate AND :endDate AND r.status != 'CANCELLED'")
    List<Reservation> findConflictingReservations(@Param("doctorId") Long doctorId, 
                                                   @Param("startDate") LocalDateTime startDate, 
                                                   @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT r FROM Reservation r WHERE r.user.id = :userId ORDER BY r.createdAt DESC")
    List<Reservation> findByUserIdOrderByCreatedAtDesc(@Param("userId") Long userId);
}

