package com.healthtourism.posttreatmentservice.controller;
import com.healthtourism.posttreatmentservice.entity.CarePackage;
import com.healthtourism.posttreatmentservice.service.PostTreatmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/post-treatment")
@CrossOrigin(origins = "*")
public class PostTreatmentController {
    @Autowired
    private PostTreatmentService service;
    
    @PostMapping
    public ResponseEntity<CarePackage> createCarePackage(@RequestBody CarePackage carePackage) {
        return ResponseEntity.ok(service.createCarePackage(carePackage));
    }
    
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<CarePackage>> getCarePackagesByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(service.getCarePackagesByUser(userId));
    }
    
    @PutMapping("/{id}/task/{taskIndex}")
    public ResponseEntity<CarePackage> updateTaskStatus(
            @PathVariable Long id,
            @PathVariable Integer taskIndex,
            @RequestBody Map<String, Boolean> request) {
        return ResponseEntity.ok(service.updateTaskStatus(id, taskIndex, request.get("isCompleted")));
    }
    
    @PostMapping("/{id}/follow-up")
    public ResponseEntity<CarePackage> scheduleFollowUp(
            @PathVariable Long id,
            @RequestBody Map<String, String> request) {
        return ResponseEntity.ok(service.scheduleFollowUp(id, java.time.LocalDateTime.parse(request.get("date"))));
    }
}

