package com.healthtourism.localguideservice.controller;
import com.healthtourism.localguideservice.service.LocalGuideService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/local-guide")
@CrossOrigin(origins = "*")
public class LocalGuideController {
    @Autowired
    private LocalGuideService service;

    @GetMapping("/restaurants")
    public ResponseEntity<List<Map<String, Object>>> getRestaurants(@RequestParam Map<String, Object> params) {
        return ResponseEntity.ok(service.getRestaurants(params));
    }

    @GetMapping("/attractions")
    public ResponseEntity<List<Map<String, Object>>> getAttractions(@RequestParam Map<String, Object> params) {
        return ResponseEntity.ok(service.getAttractions(params));
    }

    @GetMapping("/transport")
    public ResponseEntity<List<Map<String, Object>>> getTransport(@RequestParam Map<String, Object> params) {
        return ResponseEntity.ok(service.getTransport(params));
    }
}

