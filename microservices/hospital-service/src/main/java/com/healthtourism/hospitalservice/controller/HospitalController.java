package com.healthtourism.hospitalservice.controller;

import com.healthtourism.hospitalservice.dto.HospitalDTO;
import com.healthtourism.hospitalservice.dto.HospitalRequestDTO;
import com.healthtourism.hospitalservice.entity.Hospital;
import com.healthtourism.hospitalservice.service.HospitalService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/hospitals")
@CrossOrigin(origins = "*")
public class HospitalController {
    @Autowired
    private HospitalService hospitalService;
    
    @GetMapping
    public ResponseEntity<List<HospitalDTO>> getAllHospitals() {
        return ResponseEntity.ok(hospitalService.getAllActiveHospitals());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<HospitalDTO> getHospitalById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(hospitalService.getHospitalById(id));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/city/{city}")
    public ResponseEntity<List<HospitalDTO>> getHospitalsByCity(@PathVariable String city) {
        return ResponseEntity.ok(hospitalService.getHospitalsByCity(city));
    }
    
    @GetMapping("/district/{district}")
    public ResponseEntity<List<HospitalDTO>> getHospitalsByDistrict(@PathVariable String district) {
        return ResponseEntity.ok(hospitalService.getHospitalsByDistrict(district));
    }
    
    @GetMapping("/near-airport")
    public ResponseEntity<List<HospitalDTO>> getHospitalsNearAirport(
            @RequestParam(defaultValue = "50.0") Double maxDistance) {
        return ResponseEntity.ok(hospitalService.getHospitalsNearAirport(maxDistance));
    }
    
    @PostMapping
    public ResponseEntity<HospitalDTO> createHospital(@Valid @RequestBody HospitalRequestDTO request) {
        Hospital hospital = new Hospital();
        hospital.setName(request.getName());
        hospital.setAddress(request.getAddress());
        hospital.setCity(request.getCity());
        hospital.setAirportDistance(request.getAirportDistance());
        hospital.setRating(request.getRating());
        hospital.setIsActive(request.getIsActive() != null ? request.getIsActive() : true);
        return ResponseEntity.ok(hospitalService.createHospital(hospital));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<HospitalDTO> updateHospital(@PathVariable Long id, @Valid @RequestBody HospitalRequestDTO request) {
        Hospital hospital = new Hospital();
        hospital.setName(request.getName());
        hospital.setAddress(request.getAddress());
        hospital.setCity(request.getCity());
        hospital.setAirportDistance(request.getAirportDistance());
        hospital.setRating(request.getRating());
        hospital.setIsActive(request.getIsActive());
        return ResponseEntity.ok(hospitalService.updateHospital(id, hospital));
    }
}

