package com.healthtourism.quote.service;

import com.healthtourism.quote.entity.Quote;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineBuilder;
import org.springframework.stereotype.Service;

import java.util.EnumSet;

/**
 * State Machine Service for Quote Workflow
 * Manages quote state transitions: DRAFT -> PENDING -> SENT -> ACCEPTED/REJECTED -> CONVERTED
 */
@Service
public class QuoteStateMachineService {

    public StateMachine<Quote.QuoteStatus, QuoteEvent> buildStateMachine() throws Exception {
        StateMachineBuilder.Builder<Quote.QuoteStatus, QuoteEvent> builder = StateMachineBuilder.builder();

        builder.configureStates()
            .withStates()
            .initial(Quote.QuoteStatus.DRAFT)
            .states(EnumSet.allOf(Quote.QuoteStatus.class));

        builder.configureTransitions()
            .withExternal()
                .source(Quote.QuoteStatus.DRAFT)
                .target(Quote.QuoteStatus.PENDING)
                .event(QuoteEvent.SUBMIT)
            .and()
            .withExternal()
                .source(Quote.QuoteStatus.PENDING)
                .target(Quote.QuoteStatus.SENT)
                .event(QuoteEvent.SEND_TO_PATIENT)
            .and()
            .withExternal()
                .source(Quote.QuoteStatus.SENT)
                .target(Quote.QuoteStatus.ACCEPTED)
                .event(QuoteEvent.ACCEPT)
            .and()
            .withExternal()
                .source(Quote.QuoteStatus.SENT)
                .target(Quote.QuoteStatus.REJECTED)
                .event(QuoteEvent.REJECT)
            .and()
            .withExternal()
                .source(Quote.QuoteStatus.ACCEPTED)
                .target(Quote.QuoteStatus.CONVERTED)
                .event(QuoteEvent.CONVERT_TO_RESERVATION)
            .and()
            .withExternal()
                .source(Quote.QuoteStatus.SENT)
                .target(Quote.QuoteStatus.EXPIRED)
                .event(QuoteEvent.EXPIRE);

        return builder.build();
    }

    public enum QuoteEvent {
        SUBMIT,
        SEND_TO_PATIENT,
        ACCEPT,
        REJECT,
        CONVERT_TO_RESERVATION,
        EXPIRE
    }
}
