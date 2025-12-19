package com.healthtourism.jpa.entity;

import com.healthtourism.jpa.audit.AuditableEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.envers.Audited;

/**
 * Hospital Entity with Hibernate Features
 */
@Entity
@Table(name = "hospitals")
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "hospitalCache")
@Audited
@Data
@EqualsAndHashCode(callSuper = true)
public class Hospital extends AuditableEntity {
    
    @Column(nullable = false)
    private String name;
    
    @Column(nullable = false)
    private String address;
    
    @Column(nullable = false)
    private String city;
    
    @Column(nullable = false)
    private String country;
    
    @Column(name = "phone_number")
    private String phoneNumber;
    
    @Column(name = "email")
    private String email;
    
    @Column(name = "website")
    private String website;
    
    @Column(name = "accreditation", columnDefinition = "TEXT")
    private String accreditation;
    
    @Column(name = "specialties", columnDefinition = "TEXT")
    private String specialties;
}


