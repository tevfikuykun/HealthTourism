package com.healthtourism.eventsourcing.controller;

import com.healthtourism.eventsourcing.command.CreateReservationCommand;
import com.healthtourism.eventsourcing.command.ConfirmReservationCommand;
import com.healthtourism.eventsourcing.command.CancelReservationCommand;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * Command Controller (Write Side - CQRS)
 */
@RestController
@RequestMapping("/api/commands/reservations")
public class ReservationCommandController {
    
    @Autowired
    private CommandGateway commandGateway;
    
    @PostMapping
    public CompletableFuture<ResponseEntity<String>> createReservation(
            @RequestBody CreateReservationRequest request) {
        
        String reservationId = UUID.randomUUID().toString();
        
        CreateReservationCommand command = new CreateReservationCommand(
            reservationId,
            request.getUserId(),
            request.getHospitalId(),
            request.getProcedureType(),
            request.getReservationDate()
        );
        
        return commandGateway.send(command)
            .thenApply(result -> ResponseEntity.ok(reservationId))
            .exceptionally(ex -> ResponseEntity.badRequest().build());
    }
    
    @PostMapping("/{reservationId}/confirm")
    public CompletableFuture<ResponseEntity<String>> confirmReservation(
            @PathVariable String reservationId,
            @RequestParam String confirmedBy) {
        
        ConfirmReservationCommand command = new ConfirmReservationCommand(
            reservationId,
            confirmedBy
        );
        
        return commandGateway.send(command)
            .thenApply(result -> ResponseEntity.ok("Reservation confirmed"))
            .exceptionally(ex -> ResponseEntity.badRequest().build());
    }
    
    @PostMapping("/{reservationId}/cancel")
    public CompletableFuture<ResponseEntity<String>> cancelReservation(
            @PathVariable String reservationId,
            @RequestBody CancelReservationRequest request) {
        
        CancelReservationCommand command = new CancelReservationCommand(
            reservationId,
            request.getReason()
        );
        
        return commandGateway.send(command)
            .thenApply(result -> ResponseEntity.ok("Reservation cancelled"))
            .exceptionally(ex -> ResponseEntity.badRequest().build());
    }
    
    // Request DTOs
    @lombok.Data
    public static class CreateReservationRequest {
        private Long userId;
        private Long hospitalId;
        private String procedureType;
        private LocalDateTime reservationDate;
    }
    
    @lombok.Data
    public static class CancelReservationRequest {
        private String reason;
    }
}



