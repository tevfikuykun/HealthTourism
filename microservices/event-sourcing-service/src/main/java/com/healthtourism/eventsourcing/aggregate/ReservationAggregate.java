package com.healthtourism.eventsourcing.aggregate;

import com.healthtourism.eventsourcing.command.CreateReservationCommand;
import com.healthtourism.eventsourcing.command.ConfirmReservationCommand;
import com.healthtourism.eventsourcing.command.CancelReservationCommand;
import com.healthtourism.eventsourcing.event.ReservationCreatedEvent;
import com.healthtourism.eventsourcing.event.ReservationConfirmedEvent;
import com.healthtourism.eventsourcing.event.ReservationCancelledEvent;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;

import java.time.LocalDateTime;

/**
 * Reservation Aggregate (Write Model)
 * Event Sourcing pattern implementation
 */
@Aggregate
public class ReservationAggregate {
    
    @AggregateIdentifier
    private String reservationId;
    private Long userId;
    private Long hospitalId;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime confirmedAt;
    private String cancellationReason;
    
    protected ReservationAggregate() {
        // Required by Axon
    }
    
    @CommandHandler
    public ReservationAggregate(CreateReservationCommand command) {
        // Validate command
        if (command.getUserId() == null || command.getHospitalId() == null) {
            throw new IllegalArgumentException("User ID and Hospital ID are required");
        }
        
        // Publish event
        AggregateLifecycle.apply(new ReservationCreatedEvent(
            command.getReservationId(),
            command.getUserId(),
            command.getHospitalId(),
            command.getProcedureType(),
            command.getReservationDate(),
            LocalDateTime.now()
        ));
    }
    
    @CommandHandler
    public void handle(ConfirmReservationCommand command) {
        if (!"PENDING".equals(this.status)) {
            throw new IllegalStateException("Only pending reservations can be confirmed");
        }
        
        AggregateLifecycle.apply(new ReservationConfirmedEvent(
            command.getReservationId(),
            LocalDateTime.now(),
            command.getConfirmedBy()
        ));
    }
    
    @CommandHandler
    public void handle(CancelReservationCommand command) {
        if ("CANCELLED".equals(this.status)) {
            throw new IllegalStateException("Reservation is already cancelled");
        }
        
        AggregateLifecycle.apply(new ReservationCancelledEvent(
            command.getReservationId(),
            LocalDateTime.now(),
            command.getReason()
        ));
    }
    
    @EventSourcingHandler
    public void on(ReservationCreatedEvent event) {
        this.reservationId = event.getReservationId();
        this.userId = event.getUserId();
        this.hospitalId = event.getHospitalId();
        this.status = "PENDING";
        this.createdAt = event.getCreatedAt();
    }
    
    @EventSourcingHandler
    public void on(ReservationConfirmedEvent event) {
        this.status = "CONFIRMED";
        this.confirmedAt = event.getConfirmedAt();
    }
    
    @EventSourcingHandler
    public void on(ReservationCancelledEvent event) {
        this.status = "CANCELLED";
        this.cancellationReason = event.getReason();
    }
}


