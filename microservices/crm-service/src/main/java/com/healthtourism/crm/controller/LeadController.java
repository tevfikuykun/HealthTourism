package com.healthtourism.crm.controller;

import com.healthtourism.crm.entity.Lead;
import com.healthtourism.crm.service.LeadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/crm/leads")
@CrossOrigin(origins = "*")
public class LeadController {
    
    @Autowired
    private LeadService leadService;
    
    @PostMapping
    public ResponseEntity<Lead> createLead(@RequestBody Lead lead) {
        return ResponseEntity.ok(leadService.createLead(lead));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Lead> getLeadById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(leadService.getLeadById(id));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping
    public ResponseEntity<List<Lead>> getAllLeads() {
        return ResponseEntity.ok(leadService.getAllLeads());
    }
    
    @GetMapping("/status/{status}")
    public ResponseEntity<List<Lead>> getLeadsByStatus(@PathVariable Lead.LeadStatus status) {
        return ResponseEntity.ok(leadService.getLeadsByStatus(status));
    }
    
    @GetMapping("/source/{source}")
    public ResponseEntity<List<Lead>> getLeadsBySource(@PathVariable Lead.LeadSource source) {
        return ResponseEntity.ok(leadService.getLeadsBySource(source));
    }
    
    @GetMapping("/agent/{agentId}")
    public ResponseEntity<List<Lead>> getLeadsByAgent(@PathVariable Long agentId) {
        return ResponseEntity.ok(leadService.getLeadsByAgent(agentId));
    }
    
    @GetMapping("/funnel")
    public ResponseEntity<List<Lead>> getSalesFunnel() {
        return ResponseEntity.ok(leadService.getSalesFunnel());
    }
    
    @PutMapping("/{id}/status")
    public ResponseEntity<Lead> updateStatus(
            @PathVariable Long id,
            @RequestParam Lead.LeadStatus status) {
        try {
            return ResponseEntity.ok(leadService.updateLeadStatus(id, status));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PostMapping("/{id}/contact")
    public ResponseEntity<Lead> contactLead(
            @PathVariable Long id,
            @RequestParam(required = false) String notes) {
        try {
            return ResponseEntity.ok(leadService.contactLead(id, notes));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PostMapping("/{id}/qualify")
    public ResponseEntity<Lead> qualifyLead(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(leadService.qualifyLead(id));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PostMapping("/{id}/assign")
    public ResponseEntity<Lead> assignToAgent(
            @PathVariable Long id,
            @RequestParam Long agentId) {
        try {
            return ResponseEntity.ok(leadService.assignToAgent(id, agentId));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PostMapping("/{id}/convert")
    public ResponseEntity<Lead> convertToCustomer(
            @PathVariable Long id,
            @RequestParam Long userId) {
        try {
            return ResponseEntity.ok(leadService.convertToCustomer(id, userId));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PostMapping("/{id}/lost")
    public ResponseEntity<Lead> markAsLost(
            @PathVariable Long id,
            @RequestParam(required = false) String reason) {
        try {
            return ResponseEntity.ok(leadService.markAsLost(id, reason));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
