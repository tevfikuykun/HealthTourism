package com.healthtourism.jpa.entity;

import com.healthtourism.jpa.audit.AuditableEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.envers.Audited;

/**
 * Doctor Entity with Hibernate Features
 */
@Entity
@Table(name = "doctors")
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "doctorCache")
@Audited
@Data
@EqualsAndHashCode(callSuper = true)
public class Doctor extends AuditableEntity {
    
    @Column(nullable = false)
    private String firstName;
    
    @Column(nullable = false)
    private String lastName;
    
    @Column(unique = true, nullable = false)
    private String email;
    
    @Column(name = "specialization", nullable = false)
    private String specialization;
    
    @Column(name = "license_number", unique = true)
    private String licenseNumber;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hospital_id")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private Hospital hospital;
    
    @Column(name = "years_of_experience")
    private Integer yearsOfExperience;
    
    @Column(name = "bio", columnDefinition = "TEXT")
    private String bio;
}






