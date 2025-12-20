package com.healthtourism.jpa.service;

import com.healthtourism.jpa.entity.Patient;
import com.healthtourism.jpa.repository.PatientRepository;
import org.hibernate.envers.AuditReader;
import org.hibernate.envers.AuditReaderFactory;
import org.hibernate.envers.query.AuditEntity;
import org.hibernate.envers.query.AuditQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

/**
 * Patient Service with Hibernate Features
 * - L2 Cache (Redis)
 * - Dirty Checking (automatic)
 * - Envers Audit
 */
@Service
@Transactional
public class PatientService {
    
    @Autowired
    private PatientRepository patientRepository;
    
    @Autowired
    private EntityManager entityManager;
    
    /**
     * Save patient with L2 cache
     * Dirty checking automatically detects changes
     */
    @CachePut(value = "patientCache", key = "#patient.id")
    public Patient save(Patient patient) {
        // Hibernate dirty checking will automatically detect changes
        // Only changed fields will be updated in database
        return patientRepository.save(patient);
    }
    
    /**
     * Find by ID with L2 cache
     * First checks Redis, then database
     */
    @Cacheable(value = "patientCache", key = "#id")
    public Optional<Patient> findById(Long id) {
        return patientRepository.findById(id);
    }
    
    /**
     * Find by email with L2 cache
     */
    @Cacheable(value = "patientCache", key = "#email")
    public Optional<Patient> findByEmail(String email) {
        return patientRepository.findByEmail(email);
    }
    
    /**
     * Update patient
     * Dirty checking: Only changed fields updated
     */
    @CachePut(value = "patientCache", key = "#patient.id")
    public Patient update(Patient patient) {
        // Hibernate dirty checking automatically detects what changed
        // Only modified fields will be updated in SQL
        Patient existing = patientRepository.findById(patient.getId())
            .orElseThrow(() -> new RuntimeException("Patient not found"));
        
        // Update fields (dirty checking will detect changes)
        if (patient.getFirstName() != null) {
            existing.setFirstName(patient.getFirstName());
        }
        if (patient.getLastName() != null) {
            existing.setLastName(patient.getLastName());
        }
        if (patient.getPhone() != null) {
            existing.setPhone(patient.getPhone());
        }
        if (patient.getMedicalHistory() != null) {
            existing.setMedicalHistory(patient.getMedicalHistory());
        }
        
        // Hibernate will generate UPDATE with only changed fields
        return patientRepository.save(existing);
    }
    
    /**
     * Delete patient
     * Cache eviction + Envers audit
     */
    @CacheEvict(value = "patientCache", key = "#id")
    public void delete(Long id) {
        patientRepository.deleteById(id);
        // Envers automatically creates audit record
    }
    
    /**
     * Get audit history using Hibernate Envers
     * Returns all revisions of patient data
     */
    @Transactional(readOnly = true)
    @SuppressWarnings("unchecked")
    public List<Object[]> getAuditHistory(Long patientId) {
        AuditReader auditReader = AuditReaderFactory.get(entityManager);
        
        // Get all revisions for this patient
        AuditQuery query = auditReader.createQuery()
            .forRevisionsOfEntity(Patient.class, false, true)
            .add(AuditEntity.id().eq(patientId));
        
        return query.getResultList();
    }
    
    /**
     * Get patient at specific revision
     */
    @Transactional(readOnly = true)
    public Patient getPatientAtRevision(Long patientId, Number revision) {
        AuditReader auditReader = AuditReaderFactory.get(entityManager);
        return auditReader.find(Patient.class, patientId, revision);
    }
}

