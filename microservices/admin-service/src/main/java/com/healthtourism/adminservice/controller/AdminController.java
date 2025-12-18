package com.healthtourism.adminservice.controller;

import com.healthtourism.adminservice.dto.AdminUserDTO;
import com.healthtourism.adminservice.entity.AdminUser;
import com.healthtourism.adminservice.service.AdminService;
import com.healthtourism.adminservice.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
public class AdminController {
    
    @Autowired
    private AdminService adminService;
    
    @Autowired
    private ReportService reportService;
    
    @PostMapping
    public ResponseEntity<AdminUserDTO> createAdmin(@RequestBody AdminUser adminUser) {
        try {
            return ResponseEntity.ok(adminService.createAdmin(adminUser));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping
    public ResponseEntity<List<AdminUserDTO>> getAllAdmins() {
        return ResponseEntity.ok(adminService.getAllAdmins());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<AdminUserDTO> getAdminById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(adminService.getAdminById(id));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<AdminUserDTO> updateAdmin(@PathVariable Long id, @RequestBody AdminUser adminUser) {
        try {
            return ResponseEntity.ok(adminService.updateAdmin(id, adminUser));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAdmin(@PathVariable Long id) {
        adminService.deleteAdmin(id);
        return ResponseEntity.ok().build();
    }
    
    // Dashboard and Reports
    @GetMapping("/dashboard/statistics")
    public ResponseEntity<Map<String, Object>> getDashboardStatistics() {
        return ResponseEntity.ok(reportService.getDashboardStatistics());
    }
    
    @GetMapping("/reports/revenue")
    public ResponseEntity<Map<String, Object>> getRevenueReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        return ResponseEntity.ok(reportService.getRevenueReport(startDate, endDate));
    }
    
    @GetMapping("/reports/bookings")
    public ResponseEntity<Map<String, Object>> getBookingStatistics() {
        return ResponseEntity.ok(reportService.getBookingStatistics());
    }
}

