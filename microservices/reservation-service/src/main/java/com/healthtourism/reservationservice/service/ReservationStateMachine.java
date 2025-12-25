package com.healthtourism.reservationservice.service;

import com.healthtourism.reservationservice.entity.ReservationStatus;
import com.healthtourism.reservationservice.exception.BusinessRuleException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

import static java.util.Map.entry;

/**
 * Reservation State Machine
 * 
 * Manages valid state transitions for reservations.
 * Ensures business rules are enforced at the state level.
 * 
 * State Transitions:
 * PENDING -> CONFIRMED, CANCELLED
 * CONFIRMED -> COMPLETED, CANCELLED, NO_SHOW, REFUNDING
 * CANCELLED -> REFUNDING
 * REFUNDING -> REFUNDED
 * COMPLETED -> (final state)
 * NO_SHOW -> REFUNDING
 * REFUNDED -> (final state)
 */
@Component
public class ReservationStateMachine {
    
    private static final Logger logger = LoggerFactory.getLogger(ReservationStateMachine.class);
    
    /**
     * Valid state transitions map
     * Key: Current state
     * Value: Set of valid next states
     */
    private static final Map<ReservationStatus, Set<ReservationStatus>> VALID_TRANSITIONS = Map.ofEntries(
        entry(ReservationStatus.PENDING, EnumSet.of(
            ReservationStatus.CONFIRMED,
            ReservationStatus.CANCELLED
        )),
        entry(ReservationStatus.CONFIRMED, EnumSet.of(
            ReservationStatus.COMPLETED,
            ReservationStatus.CANCELLED,
            ReservationStatus.NO_SHOW,
            ReservationStatus.REFUNDING
        )),
        entry(ReservationStatus.CANCELLED, EnumSet.of(
            ReservationStatus.REFUNDING
        )),
        entry(ReservationStatus.NO_SHOW, EnumSet.of(
            ReservationStatus.REFUNDING
        )),
        entry(ReservationStatus.REFUNDING, EnumSet.of(
            ReservationStatus.REFUNDED
        ))
        // COMPLETED and REFUNDED are final states (no transitions allowed)
    );
    
    /**
     * Final states (cannot transition from these states)
     */
    private static final Set<ReservationStatus> FINAL_STATES = EnumSet.of(
        ReservationStatus.COMPLETED,
        ReservationStatus.REFUNDED
    );
    
    /**
     * Check if state transition is valid
     * 
     * @param currentStatus Current state
     * @param newStatus Desired new state
     * @return true if transition is valid
     */
    public boolean isValidTransition(ReservationStatus currentStatus, ReservationStatus newStatus) {
        if (currentStatus == null || newStatus == null) {
            return false;
        }
        
        // Cannot transition from final states
        if (FINAL_STATES.contains(currentStatus)) {
            logger.warn("Cannot transition from final state: {}", currentStatus);
            return false;
        }
        
        // Check if transition is allowed
        Set<ReservationStatus> allowedTransitions = VALID_TRANSITIONS.get(currentStatus);
        if (allowedTransitions == null) {
            logger.warn("No transitions defined for state: {}", currentStatus);
            return false;
        }
        
        boolean isValid = allowedTransitions.contains(newStatus);
        
        if (!isValid) {
            logger.warn("Invalid state transition: {} -> {}", currentStatus, newStatus);
        }
        
        return isValid;
    }
    
    /**
     * Validate state transition and throw exception if invalid
     * 
     * @param currentStatus Current state
     * @param newStatus Desired new state
     * @throws BusinessRuleException if transition is invalid
     */
    public void validateTransition(ReservationStatus currentStatus, ReservationStatus newStatus) {
        if (!isValidTransition(currentStatus, newStatus)) {
            throw new BusinessRuleException(
                String.format(
                    "Invalid state transition from %s to %s. Valid transitions from %s: %s",
                    currentStatus,
                    newStatus,
                    currentStatus,
                    VALID_TRANSITIONS.get(currentStatus)
                )
            );
        }
    }
    
    /**
     * Get allowed transitions from current state
     * 
     * @param currentStatus Current state
     * @return Set of allowed next states
     */
    public Set<ReservationStatus> getAllowedTransitions(ReservationStatus currentStatus) {
        if (currentStatus == null) {
            return EnumSet.noneOf(ReservationStatus.class);
        }
        
        if (FINAL_STATES.contains(currentStatus)) {
            return EnumSet.noneOf(ReservationStatus.class);
        }
        
        return VALID_TRANSITIONS.getOrDefault(
            currentStatus,
            EnumSet.noneOf(ReservationStatus.class)
        );
    }
    
    /**
     * Check if state is final (no transitions allowed)
     * 
     * @param status Status to check
     * @return true if final state
     */
    public boolean isFinalState(ReservationStatus status) {
        return FINAL_STATES.contains(status);
    }
    
    /**
     * Get next state for a given action
     * 
     * @param currentStatus Current state
     * @param action Action to perform (confirm, cancel, complete, etc.)
     * @return Next state
     * @throws BusinessRuleException if action is invalid for current state
     */
    public ReservationStatus getNextState(ReservationStatus currentStatus, StateAction action) {
        validateTransition(currentStatus, action.getTargetState());
        return action.getTargetState();
    }
    
    /**
     * State actions enum
     */
    public enum StateAction {
        CONFIRM(ReservationStatus.CONFIRMED),
        CANCEL(ReservationStatus.CANCELLED),
        COMPLETE(ReservationStatus.COMPLETED),
        MARK_NO_SHOW(ReservationStatus.NO_SHOW),
        START_REFUND(ReservationStatus.REFUNDING),
        COMPLETE_REFUND(ReservationStatus.REFUNDED);
        
        private final ReservationStatus targetState;
        
        StateAction(ReservationStatus targetState) {
            this.targetState = targetState;
        }
        
        public ReservationStatus getTargetState() {
            return targetState;
        }
    }
}

