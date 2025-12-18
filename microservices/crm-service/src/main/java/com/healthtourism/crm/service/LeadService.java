package com.healthtourism.crm.service;

import com.healthtourism.crm.entity.Lead;
import com.healthtourism.crm.repository.LeadRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * CRM Lead Management Service
 * Manages sales funnel and lead conversion process
 */
@Service
public class LeadService {
    
    @Autowired
    private LeadRepository leadRepository;
    
    @Transactional
    public Lead createLead(Lead lead) {
        lead.setStatus(Lead.LeadStatus.NEW);
        lead.setCreatedAt(LocalDateTime.now());
        return leadRepository.save(lead);
    }
    
    @Transactional
    public Lead updateLeadStatus(Long leadId, Lead.LeadStatus newStatus) {
        Lead lead = leadRepository.findById(leadId)
            .orElseThrow(() -> new RuntimeException("Lead bulunamadı"));
        
        lead.setStatus(newStatus);
        lead.setUpdatedAt(LocalDateTime.now());
        return leadRepository.save(lead);
    }
    
    @Transactional
    public Lead contactLead(Long leadId, String notes) {
        Lead lead = leadRepository.findById(leadId)
            .orElseThrow(() -> new RuntimeException("Lead bulunamadı"));
        
        lead.setStatus(Lead.LeadStatus.CONTACTED);
        if (notes != null) {
            lead.setNotes(lead.getNotes() != null ? lead.getNotes() + "\n" + notes : notes);
        }
        lead.setUpdatedAt(LocalDateTime.now());
        return leadRepository.save(lead);
    }
    
    @Transactional
    public Lead qualifyLead(Long leadId) {
        Lead lead = leadRepository.findById(leadId)
            .orElseThrow(() -> new RuntimeException("Lead bulunamadı"));
        
        lead.setStatus(Lead.LeadStatus.QUALIFIED);
        lead.setUpdatedAt(LocalDateTime.now());
        return leadRepository.save(lead);
    }
    
    @Transactional
    public Lead markDocumentPending(Long leadId) {
        Lead lead = leadRepository.findById(leadId)
            .orElseThrow(() -> new RuntimeException("Lead bulunamadı"));
        
        lead.setStatus(Lead.LeadStatus.DOCUMENT_PENDING);
        lead.setUpdatedAt(LocalDateTime.now());
        return leadRepository.save(lead);
    }
    
    @Transactional
    public Lead markQuoteSent(Long leadId) {
        Lead lead = leadRepository.findById(leadId)
            .orElseThrow(() -> new RuntimeException("Lead bulunamadı"));
        
        lead.setStatus(Lead.LeadStatus.QUOTE_SENT);
        lead.setUpdatedAt(LocalDateTime.now());
        return leadRepository.save(lead);
    }
    
    @Transactional
    public Lead markQuoteAccepted(Long leadId) {
        Lead lead = leadRepository.findById(leadId)
            .orElseThrow(() -> new RuntimeException("Lead bulunamadı"));
        
        lead.setStatus(Lead.LeadStatus.QUOTE_ACCEPTED);
        lead.setUpdatedAt(LocalDateTime.now());
        return leadRepository.save(lead);
    }
    
    @Transactional
    public Lead markPaymentPending(Long leadId) {
        Lead lead = leadRepository.findById(leadId)
            .orElseThrow(() -> new RuntimeException("Lead bulunamadı"));
        
        lead.setStatus(Lead.LeadStatus.PAYMENT_PENDING);
        lead.setUpdatedAt(LocalDateTime.now());
        return leadRepository.save(lead);
    }
    
    @Transactional
    public Lead convertToCustomer(Long leadId, Long userId) {
        Lead lead = leadRepository.findById(leadId)
            .orElseThrow(() -> new RuntimeException("Lead bulunamadı"));
        
        lead.setStatus(Lead.LeadStatus.CONVERTED);
        lead.setConvertedToUserId(userId);
        lead.setConvertedAt(LocalDateTime.now());
        lead.setUpdatedAt(LocalDateTime.now());
        return leadRepository.save(lead);
    }
    
    @Transactional
    public Lead markAsLost(Long leadId, String reason) {
        Lead lead = leadRepository.findById(leadId)
            .orElseThrow(() -> new RuntimeException("Lead bulunamadı"));
        
        lead.setStatus(Lead.LeadStatus.LOST);
        if (reason != null) {
            lead.setNotes(lead.getNotes() != null ? lead.getNotes() + "\nLost: " + reason : "Lost: " + reason);
        }
        lead.setUpdatedAt(LocalDateTime.now());
        return leadRepository.save(lead);
    }
    
    @Transactional
    public Lead assignToAgent(Long leadId, Long agentId) {
        Lead lead = leadRepository.findById(leadId)
            .orElseThrow(() -> new RuntimeException("Lead bulunamadı"));
        
        lead.setAssignedToUserId(agentId);
        lead.setUpdatedAt(LocalDateTime.now());
        return leadRepository.save(lead);
    }
    
    public List<Lead> getAllLeads() {
        return leadRepository.findAll();
    }
    
    public List<Lead> getLeadsByStatus(Lead.LeadStatus status) {
        return leadRepository.findByStatus(status);
    }
    
    public List<Lead> getLeadsBySource(Lead.LeadSource source) {
        return leadRepository.findBySource(source);
    }
    
    public List<Lead> getLeadsByAgent(Long agentId) {
        return leadRepository.findByAssignedToUserId(agentId);
    }
    
    public Lead getLeadById(Long id) {
        return leadRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Lead bulunamadı"));
    }
    
    public List<Lead> getSalesFunnel() {
        // Returns leads grouped by status for funnel visualization
        return leadRepository.findAll();
    }
}
