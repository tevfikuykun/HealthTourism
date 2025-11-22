package com.example.HealthTourism.controller;

import com.example.HealthTourism.dto.FlightBookingDTO;
import com.example.HealthTourism.service.FlightBookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/flights")
@CrossOrigin(origins = "*")
public class FlightBookingController {
    
    @Autowired
    private FlightBookingService flightBookingService;
    
    @GetMapping
    public ResponseEntity<List<FlightBookingDTO>> getAllFlights() {
        return ResponseEntity.ok(flightBookingService.getAllAvailableFlights());
    }
    
    @GetMapping("/search")
    public ResponseEntity<List<FlightBookingDTO>> searchFlights(
            @RequestParam String departureCity,
            @RequestParam String arrivalCity) {
        return ResponseEntity.ok(flightBookingService.searchFlights(departureCity, arrivalCity));
    }
    
    @GetMapping("/class/{flightClass}")
    public ResponseEntity<List<FlightBookingDTO>> getFlightsByClass(@PathVariable String flightClass) {
        return ResponseEntity.ok(flightBookingService.getFlightsByClass(flightClass));
    }
    
    @GetMapping("/price")
    public ResponseEntity<List<FlightBookingDTO>> getFlightsByPrice(
            @RequestParam(defaultValue = "5000") BigDecimal maxPrice) {
        return ResponseEntity.ok(flightBookingService.getFlightsByPrice(maxPrice));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<FlightBookingDTO> getFlightById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(flightBookingService.getFlightById(id));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}

