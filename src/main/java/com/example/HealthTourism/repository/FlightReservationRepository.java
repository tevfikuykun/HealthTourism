package com.example.HealthTourism.repository;

import com.example.HealthTourism.entity.FlightReservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FlightReservationRepository extends JpaRepository<FlightReservation, Long> {
    List<FlightReservation> findByUserId(Long userId);
    
    Optional<FlightReservation> findByReservationNumber(String reservationNumber);
}

