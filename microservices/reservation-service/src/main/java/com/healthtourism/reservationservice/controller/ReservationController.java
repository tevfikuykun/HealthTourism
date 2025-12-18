package com.healthtourism.reservationservice.controller;

import com.healthtourism.reservationservice.dto.ReservationDTO;
import com.healthtourism.reservationservice.dto.ReservationRequestDTO;
import com.healthtourism.reservationservice.service.ReservationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reservations")
@CrossOrigin(origins = "*")
@Tag(name = "Reservation", description = "Reservation management APIs")
public class ReservationController {
    @Autowired
    private ReservationService reservationService;
    
    @PostMapping
    @Operation(summary = "Create a new reservation", description = "Creates a new reservation with automatic price calculation and conflict checking")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Reservation created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "409", description = "Appointment slot conflict")
    })
    public ResponseEntity<ReservationDTO> createReservation(
            @Valid @RequestBody ReservationRequestDTO request) {
        return ResponseEntity.ok(reservationService.createReservation(request));
    }
    
    @GetMapping("/user/{userId}")
    @Operation(summary = "Get reservations by user ID", description = "Retrieves all reservations for a specific user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Reservations retrieved successfully")
    })
    public ResponseEntity<List<ReservationDTO>> getReservationsByUser(
            @Parameter(description = "User ID", required = true) @PathVariable Long userId) {
        return ResponseEntity.ok(reservationService.getReservationsByUser(userId));
    }
    
    @GetMapping("/number/{reservationNumber}")
    @Operation(summary = "Get reservation by reservation number", description = "Retrieves a reservation by its unique reservation number")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Reservation found"),
        @ApiResponse(responseCode = "404", description = "Reservation not found")
    })
    public ResponseEntity<ReservationDTO> getReservationByNumber(
            @Parameter(description = "Reservation number (e.g., HT-202310-001)", required = true) 
            @PathVariable String reservationNumber) {
        return ResponseEntity.ok(reservationService.getReservationByNumber(reservationNumber));
    }
    
    @PutMapping("/{id}/status")
    @Operation(summary = "Update reservation status", description = "Updates the status of a reservation (e.g., PENDING, CONFIRMED, CANCELLED)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Status updated successfully"),
        @ApiResponse(responseCode = "404", description = "Reservation not found")
    })
    public ResponseEntity<ReservationDTO> updateReservationStatus(
            @Parameter(description = "Reservation ID", required = true) @PathVariable Long id,
            @Parameter(description = "New status", required = true) @RequestParam String status) {
        return ResponseEntity.ok(reservationService.updateReservationStatus(id, status));
    }
    
    @GetMapping("/{reservationId}/verify-owner")
    @Operation(summary = "Verify reservation ownership (BOLA check)",
               description = "Check if user owns this reservation - used by Security Audit Service")
    public ResponseEntity<Map<String, Object>> verifyOwner(
            @PathVariable Long reservationId,
            @RequestParam Long userId) {
        try {
            ReservationDTO reservation = reservationService.getReservationByNumber(
                reservationService.getReservationById(reservationId).getReservationNumber());
            boolean isOwner = reservation.getUserId().equals(userId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("isOwner", isOwner);
            response.put("reservationId", reservationId);
            response.put("userId", userId);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("isOwner", false);
            response.put("error", "Reservation not found");
            return ResponseEntity.ok(response);
        }
    }
}

