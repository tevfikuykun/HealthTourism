package com.healthtourism.reviewservice.controller;
import com.healthtourism.reviewservice.entity.Review;
import com.healthtourism.reviewservice.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reviews")
@CrossOrigin(origins = "*")
public class ReviewController {
    @Autowired
    private ReviewService service;
    
    @PostMapping
    public ResponseEntity<Review> createReview(@RequestBody Review review) {
        return ResponseEntity.ok(service.createReview(review));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Review> updateReview(@PathVariable Long id, @RequestBody Review review) {
        return ResponseEntity.ok(service.updateReview(id, review));
    }
    
    @GetMapping("/entity/{entityType}/{entityId}")
    public ResponseEntity<List<Review>> getReviewsByEntity(
            @PathVariable String entityType,
            @PathVariable Long entityId) {
        return ResponseEntity.ok(service.getReviewsByEntity(entityType, entityId));
    }
    
    @PostMapping("/{id}/response")
    public ResponseEntity<Review> addDoctorResponse(
            @PathVariable Long id,
            @RequestBody Map<String, String> request) {
        return ResponseEntity.ok(service.addDoctorResponse(id, request.get("response")));
    }
    
    @PostMapping("/{id}/helpful")
    public ResponseEntity<Review> markHelpful(
            @PathVariable Long id,
            @RequestBody Map<String, Boolean> request) {
        return ResponseEntity.ok(service.markHelpful(id, request.get("helpful")));
    }
    
    @PostMapping("/{id}/verify")
    public ResponseEntity<Review> verifyReview(@PathVariable Long id) {
        return ResponseEntity.ok(service.verifyReview(id));
    }
    
    @GetMapping("/entity/{entityType}/{entityId}/sorted")
    public ResponseEntity<List<Review>> getReviewsSorted(
            @PathVariable String entityType,
            @PathVariable Long entityId,
            @RequestParam(required = false, defaultValue = "NEWEST") String sortBy) {
        return ResponseEntity.ok(service.getReviewsSorted(entityType, entityId, sortBy));
    }
    
    @GetMapping("/entity/{entityType}/{entityId}/with-images")
    public ResponseEntity<List<Review>> getReviewsWithImages(
            @PathVariable String entityType,
            @PathVariable Long entityId) {
        return ResponseEntity.ok(service.getReviewsWithImages(entityType, entityId));
    }
    
    @GetMapping("/entity/{entityType}/{entityId}/with-responses")
    public ResponseEntity<List<Review>> getReviewsWithResponses(
            @PathVariable String entityType,
            @PathVariable Long entityId) {
        return ResponseEntity.ok(service.getReviewsWithResponses(entityType, entityId));
    }
}

