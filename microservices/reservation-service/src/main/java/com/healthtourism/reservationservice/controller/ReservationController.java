package com.healthtourism.reservationservice.controller;
import com.healthtourism.reservationservice.dto.ReservationDTO;
import com.healthtourism.reservationservice.dto.ReservationRequestDTO;
import com.healthtourism.reservationservice.service.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/reservations")
@CrossOrigin(origins = "*")
public class ReservationController {
    @Autowired
    private ReservationService reservationService;
    
    @PostMapping
    public ResponseEntity<?> createReservation(@RequestBody ReservationRequestDTO request) {
        try {
            return ResponseEntity.ok(reservationService.createReservation(request));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ReservationDTO>> getReservationsByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(reservationService.getReservationsByUser(userId));
    }
    
    @GetMapping("/number/{reservationNumber}")
    public ResponseEntity<ReservationDTO> getReservationByNumber(@PathVariable String reservationNumber) {
        try {
            return ResponseEntity.ok(reservationService.getReservationByNumber(reservationNumber));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateReservationStatus(@PathVariable Long id, @RequestParam String status) {
        try {
            return ResponseEntity.ok(reservationService.updateReservationStatus(id, status));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}

