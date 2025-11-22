package com.example.HealthTourism.repository;

import com.example.HealthTourism.entity.CarRentalReservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CarRentalReservationRepository extends JpaRepository<CarRentalReservation, Long> {
    List<CarRentalReservation> findByUserId(Long userId);
    
    Optional<CarRentalReservation> findByReservationNumber(String reservationNumber);
}

