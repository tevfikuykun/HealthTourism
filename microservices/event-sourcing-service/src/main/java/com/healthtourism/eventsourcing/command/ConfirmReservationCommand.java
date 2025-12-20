package com.healthtourism.eventsourcing.command;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConfirmReservationCommand {
    @TargetAggregateIdentifier
    private String reservationId;
    private String confirmedBy;
}



