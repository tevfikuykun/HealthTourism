package com.healthtourism.auditreportgeneratorservice.controller;

import com.healthtourism.auditreportgeneratorservice.service.AuditReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/audit-reports")
@Tag(name = "Audit Report Generator", description = "SOC2 & ISO 27001 compliance report generation")
public class AuditReportController {
    
    @Autowired
    private AuditReportService auditReportService;
    
    @GetMapping(value = "/generate", produces = MediaType.APPLICATION_PDF_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Generate audit report", 
               description = "Generate security and access audit report (PDF) for compliance")
    public ResponseEntity<byte[]> generateReport(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(defaultValue = "MONTHLY") String reportType) {
        
        LocalDateTime start = startDate != null ? 
            LocalDateTime.parse(startDate) : LocalDateTime.now().minusMonths(1);
        LocalDateTime end = endDate != null ? 
            LocalDateTime.parse(endDate) : LocalDateTime.now();
        
        byte[] report = auditReportService.generateReport(start, end, reportType);
        
        if (report == null) {
            return ResponseEntity.badRequest().build();
        }
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", 
            "audit-report-" + LocalDateTime.now().format(java.time.format.DateTimeFormatter.ISO_DATE) + ".pdf");
        
        return ResponseEntity.ok()
            .headers(headers)
            .body(report);
    }
}
