package com.healthtourism.searchservice.controller;

import com.healthtourism.searchservice.document.DoctorDocument;
import com.healthtourism.searchservice.document.HospitalDocument;
import com.healthtourism.searchservice.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/search")
@CrossOrigin(origins = "*")
public class SearchController {
    
    @Autowired
    private SearchService searchService;
    
    @GetMapping("/hospitals")
    public ResponseEntity<List<HospitalDocument>> searchHospitals(@RequestParam(required = false) String query,
                                                                   @RequestParam(required = false) String city,
                                                                   @RequestParam(required = false) Double minRating) {
        if (query != null) {
            return ResponseEntity.ok(searchService.searchHospitals(query));
        } else if (city != null) {
            return ResponseEntity.ok(searchService.searchHospitalsByCity(city));
        } else if (minRating != null) {
            return ResponseEntity.ok(searchService.searchHospitalsByRating(minRating));
        }
        return ResponseEntity.badRequest().build();
    }
    
    @GetMapping("/doctors")
    public ResponseEntity<List<DoctorDocument>> searchDoctors(@RequestParam(required = false) String query,
                                                               @RequestParam(required = false) String specialty,
                                                               @RequestParam(required = false) Double minRating) {
        if (query != null) {
            return ResponseEntity.ok(searchService.searchDoctors(query));
        } else if (specialty != null) {
            return ResponseEntity.ok(searchService.searchDoctorsBySpecialty(specialty));
        } else if (minRating != null) {
            return ResponseEntity.ok(searchService.searchDoctorsByRating(minRating));
        }
        return ResponseEntity.badRequest().build();
    }
    
    @PostMapping("/hospitals/index")
    public ResponseEntity<String> indexHospital(@RequestBody HospitalDocument hospital) {
        searchService.indexHospital(hospital);
        return ResponseEntity.ok("Hospital indexed");
    }
    
    @PostMapping("/doctors/index")
    public ResponseEntity<String> indexDoctor(@RequestBody DoctorDocument doctor) {
        searchService.indexDoctor(doctor);
        return ResponseEntity.ok("Doctor indexed");
    }
}


