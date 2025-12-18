package com.healthtourism.crm.repository;

import com.healthtourism.crm.entity.Lead;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LeadRepository extends JpaRepository<Lead, Long> {
    List<Lead> findByStatus(Lead.LeadStatus status);
    List<Lead> findBySource(Lead.LeadSource source);
    List<Lead> findByAssignedToUserId(Long userId);
    List<Lead> findByEmail(String email);
    List<Lead> findByStatusOrderByCreatedAtDesc(Lead.LeadStatus status);
}
