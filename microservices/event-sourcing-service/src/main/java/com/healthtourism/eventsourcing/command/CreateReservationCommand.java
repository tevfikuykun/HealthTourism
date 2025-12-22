package com.healthtourism.eventsourcing.command;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateReservationCommand {
    @TargetAggregateIdentifier
    private String reservationId;
    private Long userId;
    private Long hospitalId;
    private String procedureType;
    private LocalDateTime reservationDate;
}






