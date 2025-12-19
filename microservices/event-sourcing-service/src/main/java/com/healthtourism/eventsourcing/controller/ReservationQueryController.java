package com.healthtourism.eventsourcing.controller;

import com.healthtourism.eventsourcing.model.ReservationReadModel;
import com.healthtourism.eventsourcing.repository.ReservationReadModelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * Query Controller (Read Side - CQRS)
 * Optimized for fast queries using read models
 */
@RestController
@RequestMapping("/api/queries/reservations")
public class ReservationQueryController {
    
    @Autowired
    private ReservationReadModelRepository readModelRepository;
    
    @GetMapping("/{reservationId}")
    public ResponseEntity<ReservationReadModel> getReservation(
            @PathVariable String reservationId) {
        
        Optional<ReservationReadModel> reservation = readModelRepository
            .findByReservationId(reservationId);
        
        return reservation.map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/user/{userId}")
    public List<ReservationReadModel> getReservationsByUser(
            @PathVariable Long userId) {
        return readModelRepository.findByUserId(userId);
    }
    
    @GetMapping("/hospital/{hospitalId}")
    public List<ReservationReadModel> getReservationsByHospital(
            @PathVariable Long hospitalId) {
        return readModelRepository.findByHospitalId(hospitalId);
    }
    
    @GetMapping("/status/{status}")
    public List<ReservationReadModel> getReservationsByStatus(
            @PathVariable String status) {
        return readModelRepository.findByStatus(status);
    }
}

