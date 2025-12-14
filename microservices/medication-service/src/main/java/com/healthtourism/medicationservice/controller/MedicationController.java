package com.healthtourism.medicationservice.controller;

import com.healthtourism.medicationservice.dto.MedicationDTO;
import com.healthtourism.medicationservice.service.MedicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/medications")
@CrossOrigin(origins = "*")
public class MedicationController {
    @Autowired
    private MedicationService medicationService;

    @GetMapping
    public ResponseEntity<List<MedicationDTO>> getAll(
            @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        if (userId == null) userId = 1L;
        return ResponseEntity.ok(medicationService.getAll(userId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<MedicationDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(medicationService.getById(id));
    }

    @PostMapping
    public ResponseEntity<MedicationDTO> create(@RequestBody MedicationDTO dto,
            @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        if (userId != null) dto.setUserId(userId);
        return ResponseEntity.ok(medicationService.create(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<MedicationDTO> update(@PathVariable Long id, @RequestBody MedicationDTO dto) {
        return ResponseEntity.ok(medicationService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        medicationService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/toggle")
    public ResponseEntity<MedicationDTO> toggle(@PathVariable Long id) {
        return ResponseEntity.ok(medicationService.toggle(id));
    }
}

