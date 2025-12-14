package com.healthtourism.posttreatmentservice.service;
import com.healthtourism.posttreatmentservice.entity.CarePackage;
import com.healthtourism.posttreatmentservice.repository.CarePackageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class PostTreatmentService {
    @Autowired
    private CarePackageRepository repository;
    
    public CarePackage createCarePackage(CarePackage carePackage) {
        return repository.save(carePackage);
    }
    
    public List<CarePackage> getCarePackagesByUser(Long userId) {
        return repository.findByUserId(userId);
    }
    
    public CarePackage updateTaskStatus(Long packageId, Integer taskIndex, Boolean isCompleted) {
        CarePackage carePackage = repository.findById(packageId)
            .orElseThrow(() -> new RuntimeException("Care package bulunamadı"));
        if (taskIndex < carePackage.getTasks().size()) {
            carePackage.getTasks().get(taskIndex).setIsCompleted(isCompleted);
        }
        return repository.save(carePackage);
    }
    
    public CarePackage scheduleFollowUp(Long packageId, java.time.LocalDateTime followUpDate) {
        CarePackage carePackage = repository.findById(packageId)
            .orElseThrow(() -> new RuntimeException("Care package bulunamadı"));
        // Follow-up appointment oluştur
        return repository.save(carePackage);
    }
}

