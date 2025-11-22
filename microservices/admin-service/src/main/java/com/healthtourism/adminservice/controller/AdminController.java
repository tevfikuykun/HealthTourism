package com.healthtourism.adminservice.controller;

import com.healthtourism.adminservice.dto.AdminUserDTO;
import com.healthtourism.adminservice.entity.AdminUser;
import com.healthtourism.adminservice.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
public class AdminController {
    
    @Autowired
    private AdminService adminService;
    
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
}

