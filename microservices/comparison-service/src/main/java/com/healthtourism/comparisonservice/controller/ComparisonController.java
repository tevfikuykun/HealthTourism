package com.healthtourism.comparisonservice.controller;

import com.healthtourism.comparisonservice.dto.ComparisonRequest;
import com.healthtourism.comparisonservice.dto.ComparisonResponse;
import com.healthtourism.comparisonservice.service.ComparisonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/comparison")
@CrossOrigin(origins = "*")
public class ComparisonController {

    @Autowired
    private ComparisonService comparisonService;

    @PostMapping("/compare")
    public ResponseEntity<ComparisonResponse> compare(
            @RequestBody ComparisonRequest request,
            @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        if (userId == null) {
            userId = 1L; // Default for testing
        }
        return ResponseEntity.ok(comparisonService.compare(request, userId));
    }

    @GetMapping("/{type}")
    public ResponseEntity<ComparisonResponse> getComparison(
            @PathVariable String type,
            @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        if (userId == null) {
            userId = 1L; // Default for testing
        }
        return ResponseEntity.ok(comparisonService.getComparison(type, userId));
    }
}

