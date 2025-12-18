package com.healthtourism.audit.controller;

import com.healthtourism.audit.service.ArchiveRetrievalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/audit/archive/retrieval")
@CrossOrigin(origins = "*")
public class ArchiveRetrievalController {
    
    @Autowired
    private ArchiveRetrievalService retrievalService;
    
    /**
     * Request retrieval of archived audit logs
     */
    @PostMapping("/request")
    public ResponseEntity<Map<String, String>> requestRetrieval(
            @RequestParam String archiveId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        
        String jobId = retrievalService.requestRetrieval(archiveId, startDate, endDate);
        return ResponseEntity.ok(Map.of("jobId", jobId, "message", "Retrieval job created"));
    }
    
    /**
     * Get retrieval status
     */
    @GetMapping("/status/{jobId}")
    public ResponseEntity<Map<String, Object>> getRetrievalStatus(@PathVariable String jobId) {
        return ResponseEntity.ok(retrievalService.getRetrievalStatus(jobId));
    }
    
    /**
     * Get retrieval estimate
     */
    @GetMapping("/estimate")
    public ResponseEntity<Map<String, Object>> getRetrievalEstimate(
            @RequestParam String archiveId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        
        return ResponseEntity.ok(retrievalService.getRetrievalEstimate(archiveId, startDate, endDate));
    }
    
    /**
     * Check if archive exists
     */
    @GetMapping("/exists/{archiveId}")
    public ResponseEntity<Map<String, Boolean>> archiveExists(@PathVariable String archiveId) {
        return ResponseEntity.ok(Map.of("exists", retrievalService.archiveExists(archiveId)));
    }
}
