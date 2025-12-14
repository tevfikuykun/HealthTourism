package com.healthtourism.healthrecordsservice.controller;

import com.healthtourism.healthrecordsservice.dto.HealthRecordDTO;
import com.healthtourism.healthrecordsservice.service.HealthRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/health-records")
@CrossOrigin(origins = "*")
public class HealthRecordController {

    @Autowired
    private HealthRecordService healthRecordService;

    @GetMapping
    public ResponseEntity<List<HealthRecordDTO>> getAll(
            @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        if (userId == null) {
            userId = 1L; // Default for testing
        }
        return ResponseEntity.ok(healthRecordService.getAll(userId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<HealthRecordDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(healthRecordService.getById(id));
    }

    @PostMapping
    public ResponseEntity<HealthRecordDTO> create(
            @RequestBody HealthRecordDTO dto,
            @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        if (userId != null) {
            dto.setUserId(userId);
        }
        return ResponseEntity.ok(healthRecordService.create(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<HealthRecordDTO> update(
            @PathVariable Long id,
            @RequestBody HealthRecordDTO dto) {
        return ResponseEntity.ok(healthRecordService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        healthRecordService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

