package com.healthtourism.eventsourcing.projection;

import com.healthtourism.eventsourcing.event.ReservationCreatedEvent;
import com.healthtourism.eventsourcing.event.ReservationConfirmedEvent;
import com.healthtourism.eventsourcing.event.ReservationCancelledEvent;
import com.healthtourism.eventsourcing.model.ReservationReadModel;
import com.healthtourism.eventsourcing.repository.ReservationReadModelRepository;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Reservation Projection (Read Model)
 * CQRS Pattern - Separates read and write models
 */
@Component
public class ReservationProjection {
    
    @Autowired
    private ReservationReadModelRepository readModelRepository;
    
    @EventHandler
    public void on(ReservationCreatedEvent event) {
        ReservationReadModel readModel = new ReservationReadModel();
        readModel.setReservationId(event.getReservationId());
        readModel.setUserId(event.getUserId());
        readModel.setHospitalId(event.getHospitalId());
        readModel.setProcedureType(event.getProcedureType());
        readModel.setReservationDate(event.getReservationDate());
        readModel.setStatus("PENDING");
        readModel.setCreatedAt(event.getCreatedAt());
        
        readModelRepository.save(readModel);
    }
    
    @EventHandler
    public void on(ReservationConfirmedEvent event) {
        ReservationReadModel readModel = readModelRepository
            .findByReservationId(event.getReservationId())
            .orElseThrow(() -> new RuntimeException("Reservation not found"));
        
        readModel.setStatus("CONFIRMED");
        readModel.setConfirmedAt(event.getConfirmedAt());
        
        readModelRepository.save(readModel);
    }
    
    @EventHandler
    public void on(ReservationCancelledEvent event) {
        ReservationReadModel readModel = readModelRepository
            .findByReservationId(event.getReservationId())
            .orElseThrow(() -> new RuntimeException("Reservation not found"));
        
        readModel.setStatus("CANCELLED");
        readModel.setCancelledAt(event.getCancelledAt());
        readModel.setCancellationReason(event.getReason());
        
        readModelRepository.save(readModel);
    }
}


