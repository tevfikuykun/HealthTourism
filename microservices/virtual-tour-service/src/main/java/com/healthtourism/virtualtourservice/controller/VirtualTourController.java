package com.healthtourism.virtualtourservice.controller;

import com.healthtourism.virtualtourservice.entity.VirtualTour;
import com.healthtourism.virtualtourservice.service.VirtualTourService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/virtual-tours")
@CrossOrigin(origins = "*")
@Tag(name = "Virtual Tours", description = "AR/VR 360-degree tours for hospitals, accommodations, and doctor offices")
public class VirtualTourController {
    
    @Autowired
    private VirtualTourService virtualTourService;
    
    @GetMapping
    @Operation(summary = "Get all active virtual tours")
    public ResponseEntity<List<VirtualTour>> getAllTours() {
        return ResponseEntity.ok(virtualTourService.getAllActiveTours());
    }
    
    @GetMapping("/type/{tourType}")
    @Operation(summary = "Get tours by type", 
               description = "Tour types: HOSPITAL, ACCOMMODATION, DOCTOR_OFFICE, OPERATING_ROOM")
    public ResponseEntity<List<VirtualTour>> getToursByType(@PathVariable String tourType) {
        return ResponseEntity.ok(virtualTourService.getToursByType(tourType));
    }
    
    @GetMapping("/entity/{entityId}")
    @Operation(summary = "Get tours for a specific entity")
    public ResponseEntity<List<VirtualTour>> getToursByEntity(
            @PathVariable Long entityId,
            @RequestParam String tourType) {
        return ResponseEntity.ok(virtualTourService.getToursByEntity(entityId, tourType));
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get tour by ID")
    public ResponseEntity<VirtualTour> getTourById(@PathVariable Long id) {
        try {
            VirtualTour tour = virtualTourService.getTourById(id);
            virtualTourService.incrementViewCount(id); // Track views
            return ResponseEntity.ok(tour);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/entity/{entityId}/tour")
    @Operation(summary = "Get active tour for entity")
    public ResponseEntity<VirtualTour> getTourByEntity(
            @PathVariable Long entityId,
            @RequestParam String tourType) {
        try {
            VirtualTour tour = virtualTourService.getTourByEntity(entityId, tourType);
            virtualTourService.incrementViewCount(tour.getId());
            return ResponseEntity.ok(tour);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @PostMapping
    @Operation(summary = "Create a new virtual tour")
    public ResponseEntity<VirtualTour> createTour(@RequestBody VirtualTour tour) {
        return ResponseEntity.ok(virtualTourService.createTour(tour));
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Update virtual tour")
    public ResponseEntity<VirtualTour> updateTour(
            @PathVariable Long id,
            @RequestBody VirtualTour tour) {
        try {
            return ResponseEntity.ok(virtualTourService.updateTour(id, tour));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @PostMapping("/{id}/rate")
    @Operation(summary = "Rate a virtual tour")
    public ResponseEntity<VirtualTour> rateTour(
            @PathVariable Long id,
            @RequestBody Map<String, Double> ratingRequest) {
        try {
            Double rating = ratingRequest.get("rating");
            return ResponseEntity.ok(virtualTourService.rateTour(id, rating));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/top-rated")
    @Operation(summary = "Get top rated tours")
    public ResponseEntity<List<VirtualTour>> getTopRatedTours(
            @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(virtualTourService.getTopRatedTours(limit));
    }
}
