package com.example.HealthTourism.repository;

import com.example.HealthTourism.entity.TransferReservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TransferReservationRepository extends JpaRepository<TransferReservation, Long> {
    List<TransferReservation> findByUserId(Long userId);
    
    Optional<TransferReservation> findByReservationNumber(String reservationNumber);
}

