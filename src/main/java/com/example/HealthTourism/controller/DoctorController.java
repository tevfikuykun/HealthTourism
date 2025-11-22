package com.example.HealthTourism.controller;

import com.example.HealthTourism.dto.DoctorDTO;
import com.example.HealthTourism.entity.Doctor;
import com.example.HealthTourism.service.DoctorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/doctors")
@CrossOrigin(origins = "*")
public class DoctorController {
    
    @Autowired
    private DoctorService doctorService;
    
    @GetMapping("/hospital/{hospitalId}")
    public ResponseEntity<List<DoctorDTO>> getDoctorsByHospital(@PathVariable Long hospitalId) {
        return ResponseEntity.ok(doctorService.getDoctorsByHospital(hospitalId));
    }
    
    @GetMapping("/specialization/{specialization}")
    public ResponseEntity<List<DoctorDTO>> getDoctorsBySpecialization(@PathVariable String specialization) {
        return ResponseEntity.ok(doctorService.getDoctorsBySpecialization(specialization));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<DoctorDTO> getDoctorById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(doctorService.getDoctorById(id));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/hospital/{hospitalId}/top-rated")
    public ResponseEntity<List<DoctorDTO>> getTopRatedDoctorsByHospital(@PathVariable Long hospitalId) {
        return ResponseEntity.ok(doctorService.getTopRatedDoctorsByHospital(hospitalId));
    }
    
    @PostMapping
    public ResponseEntity<DoctorDTO> createDoctor(@RequestBody Doctor doctor) {
        return ResponseEntity.ok(doctorService.createDoctor(doctor));
    }
}

