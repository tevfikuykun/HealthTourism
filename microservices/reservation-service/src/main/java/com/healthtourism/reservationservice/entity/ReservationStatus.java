package com.healthtourism.reservationservice.entity;

/**
 * Reservation Status Enum
 * 
 * Defines the lifecycle states of a reservation.
 * Using enum prevents typos and ensures data consistency.
 */
public enum ReservationStatus {
    
    /**
     * Initial state - reservation created but not yet confirmed
     */
    PENDING("Beklemede"),
    
    /**
     * Reservation confirmed by hospital/admin
     */
    CONFIRMED("Onaylandı"),
    
    /**
     * Reservation cancelled by user or admin
     */
    CANCELLED("İptal Edildi"),
    
    /**
     * Appointment completed
     */
    COMPLETED("Tamamlandı"),
    
    /**
     * Appointment no-show (patient didn't show up)
     */
    NO_SHOW("Gelmedi"),
    
    /**
     * Refund in process
     */
    REFUNDING("İade İşleminde"),
    
    /**
     * Refund completed
     */
    REFUNDED("İade Edildi");
    
    private final String displayName;
    
    ReservationStatus(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    /**
     * Check if reservation can be cancelled
     */
    public boolean canBeCancelled() {
        return this == PENDING || this == CONFIRMED;
    }
    
    /**
     * Check if reservation is in final state (cannot be modified)
     */
    public boolean isFinalState() {
        return this == COMPLETED || this == CANCELLED || this == REFUNDED || this == NO_SHOW;
    }
    
    /**
     * Check if reservation allows refund
     */
    public boolean allowsRefund() {
        return this == CANCELLED || this == CONFIRMED || this == NO_SHOW;
    }
    
    /**
     * Find status by display name (case-insensitive)
     */
    public static ReservationStatus fromDisplayName(String displayName) {
        if (displayName == null || displayName.trim().isEmpty()) {
            return null;
        }
        for (ReservationStatus status : values()) {
            if (status.displayName.equalsIgnoreCase(displayName.trim())) {
                return status;
            }
        }
        return null;
    }
    
    /**
     * Find status by enum name (case-insensitive)
     */
    public static ReservationStatus fromName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return null;
        }
        try {
            return valueOf(name.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}

