package com.healthtourism.jpa.entity;

import com.healthtourism.jpa.audit.AuditableEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.envers.Audited;

/**
 * Reservation Entity with Hibernate Features
 */
@Entity
@Table(name = "reservations")
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "reservationCache")
@Audited
@Data
@EqualsAndHashCode(callSuper = true)
public class Reservation extends AuditableEntity {
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private Patient patient;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hospital_id", nullable = false)
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private Hospital hospital;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private Doctor doctor;
    
    @Column(name = "procedure_type", nullable = false)
    private String procedureType;
    
    @Column(name = "reservation_date", nullable = false)
    private java.time.LocalDateTime reservationDate;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReservationStatus status;
    
    @Column(name = "total_amount", precision = 10, scale = 2)
    private java.math.BigDecimal totalAmount;
    
    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;
    
    public enum ReservationStatus {
        PENDING, CONFIRMED, CANCELLED, COMPLETED
    }
}


