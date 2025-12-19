package com.healthtourism.eventsourcing.command;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CancelReservationCommand {
    @TargetAggregateIdentifier
    private String reservationId;
    private String reason;
}

