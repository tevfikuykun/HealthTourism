package com.healthtourism.adminservice.service;

import com.healthtourism.adminservice.dto.AdminUserDTO;
import com.healthtourism.adminservice.entity.AdminUser;
import com.healthtourism.adminservice.repository.AdminUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminService {
    
    @Autowired
    private AdminUserRepository adminUserRepository;
    
    public AdminUserDTO createAdmin(AdminUser adminUser) {
        if (adminUserRepository.existsByEmail(adminUser.getEmail())) {
            throw new RuntimeException("Email already exists");
        }
        adminUser = adminUserRepository.save(adminUser);
        return convertToDTO(adminUser);
    }
    
    public AdminUserDTO getAdminById(Long id) {
        AdminUser admin = adminUserRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Admin not found"));
        return convertToDTO(admin);
    }
    
    public List<AdminUserDTO> getAllAdmins() {
        return adminUserRepository.findAll()
            .stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
    
    public AdminUserDTO updateAdmin(Long id, AdminUser adminUser) {
        AdminUser existing = adminUserRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Admin not found"));
        
        existing.setFirstName(adminUser.getFirstName());
        existing.setLastName(adminUser.getLastName());
        existing.setRole(adminUser.getRole());
        existing.setIsActive(adminUser.getIsActive());
        
        existing = adminUserRepository.save(existing);
        return convertToDTO(existing);
    }
    
    public void deleteAdmin(Long id) {
        adminUserRepository.deleteById(id);
    }
    
    public AdminUserDTO updateLastLogin(Long id) {
        AdminUser admin = adminUserRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Admin not found"));
        admin.setLastLogin(LocalDateTime.now());
        admin = adminUserRepository.save(admin);
        return convertToDTO(admin);
    }
    
    private AdminUserDTO convertToDTO(AdminUser admin) {
        return new AdminUserDTO(
            admin.getId(),
            admin.getEmail(),
            admin.getFirstName(),
            admin.getLastName(),
            admin.getRole(),
            admin.getIsActive(),
            admin.getLastLogin(),
            admin.getCreatedAt()
        );
    }
}

