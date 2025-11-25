package com.example.HealthTourism.controller;

import com.example.HealthTourism.dto.ReviewDTO;
import com.example.HealthTourism.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
@CrossOrigin(origins = "*")
public class ReviewController {
    
    @Autowired
    private ReviewService reviewService;
    
    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<List<ReviewDTO>> getReviewsByDoctor(@PathVariable Long doctorId) {
        return ResponseEntity.ok(reviewService.getReviewsByDoctor(doctorId));
    }

    @GetMapping("/hospital/{hospitalId}")
    public ResponseEntity<List<ReviewDTO>> getReviewsByHospital(@PathVariable Long hospitalId) {
        return ResponseEntity.ok(reviewService.getReviewsByHospital(hospitalId));
    }
    
    @PostMapping("/doctor")
    public ResponseEntity<?> createDoctorReview(
            @RequestParam Long userId,
            @RequestParam Long doctorId,
            @RequestParam Integer rating,
            @RequestParam(required = false) String comment) {
        try {
            return ResponseEntity.ok(reviewService.createReview(userId, doctorId, rating, comment));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/hospital")
    public ResponseEntity<?> createHospitalReview(
            @RequestParam Long userId,
            @RequestParam Long hospitalId,
            @RequestParam Integer rating,
            @RequestParam(required = false) String comment) {
        try {
            return ResponseEntity.ok(reviewService.createHospitalReview(userId, hospitalId, rating, comment));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}

