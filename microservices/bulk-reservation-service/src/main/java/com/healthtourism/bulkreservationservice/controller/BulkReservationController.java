package com.healthtourism.bulkreservationservice.controller;
import com.healthtourism.bulkreservationservice.service.BulkReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/bulk-reservations")
@CrossOrigin(origins = "*")
public class BulkReservationController {
    @Autowired
    private BulkReservationService service;

    @PostMapping
    public ResponseEntity<Map<String, Object>> create(@RequestBody Map<String, Object> reservation) {
        return ResponseEntity.ok(service.create(reservation));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getById(@PathVariable String id) {
        return ResponseEntity.ok(service.getById(id));
    }
}

