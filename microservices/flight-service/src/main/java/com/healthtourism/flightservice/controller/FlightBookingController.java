package com.healthtourism.flightservice.controller;

import com.healthtourism.flightservice.dto.FlightBookingDTO;
import com.healthtourism.flightservice.service.FlightBookingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/flights")
@CrossOrigin(origins = "*")
@Tag(name = "Flight", description = "Flight booking management APIs")
public class FlightBookingController {
    @Autowired
    private FlightBookingService flightBookingService;
    
    @GetMapping
    @Operation(summary = "Get all available flights")
    public ResponseEntity<List<FlightBookingDTO>> getAllFlights() {
        return ResponseEntity.ok(flightBookingService.getAllAvailableFlights());
    }
    
    @GetMapping("/search")
    @Operation(summary = "Search flights by departure and arrival cities")
    public ResponseEntity<List<FlightBookingDTO>> searchFlights(
            @Parameter(description = "Departure city", required = true) @RequestParam String departureCity,
            @Parameter(description = "Arrival city", required = true) @RequestParam String arrivalCity) {
        return ResponseEntity.ok(flightBookingService.searchFlights(departureCity, arrivalCity));
    }
    
    @GetMapping("/class/{flightClass}")
    @Operation(summary = "Get flights by class")
    public ResponseEntity<List<FlightBookingDTO>> getFlightsByClass(
            @Parameter(description = "Flight class", required = true) @PathVariable String flightClass) {
        return ResponseEntity.ok(flightBookingService.getFlightsByClass(flightClass));
    }
    
    @GetMapping("/price")
    @Operation(summary = "Get flights by maximum price")
    public ResponseEntity<List<FlightBookingDTO>> getFlightsByPrice(
            @Parameter(description = "Maximum price") @RequestParam(defaultValue = "5000") BigDecimal maxPrice) {
        return ResponseEntity.ok(flightBookingService.getFlightsByPrice(maxPrice));
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get flight by ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Flight found"),
        @ApiResponse(responseCode = "404", description = "Flight not found")
    })
    public ResponseEntity<FlightBookingDTO> getFlightById(
            @Parameter(description = "Flight ID", required = true) @PathVariable Long id) {
        return ResponseEntity.ok(flightBookingService.getFlightById(id));
    }
}

