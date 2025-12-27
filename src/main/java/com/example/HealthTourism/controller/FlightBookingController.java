package com.example.HealthTourism.controller;

import com.example.HealthTourism.dto.FlightBookingDTO;
import com.example.HealthTourism.service.FlightBookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Flight Booking Controller
 * Uçuş rezervasyon yönetimi endpoint'lerini işler
 * 
 * Hata yönetimi GlobalExceptionHandler'a devredilmiştir:
 * - FlightNotFoundException -> 404 NOT_FOUND
 * - IllegalArgumentException -> 400 BAD_REQUEST
 * 
 * Not: Uçuş sınıfları (flightClass) için gelecekte Enum kullanımı önerilir
 * (ECONOMY, BUSINESS, FIRST) - yanlış veri girişlerini API seviyesinde engellemek için.
 */
@RestController
@RequestMapping("/api/v1/flights")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class FlightBookingController {
    
    private final FlightBookingService flightBookingService;
    
    /**
     * Tüm müsait uçuşları getir
     * GET /api/v1/flights
     */
    @GetMapping
    public ResponseEntity<List<FlightBookingDTO>> getAllFlights() {
        return ResponseEntity.ok(flightBookingService.getAllAvailableFlights());
    }
    
    /**
     * Uçuş ara (şehir ve opsiyonel tarih ile)
     * GET /api/v1/flights/search?departureCity=İstanbul&arrivalCity=Ankara&departureDate=2024-06-15
     * 
     * Sağlık turizmi için kritik: Hastalar genellikle belirli bir tedavi randevusuna
     * yetişmek için tarih bazlı arama yaparlar.
     * 
     * @param departureCity Kalkış şehri
     * @param arrivalCity Varış şehri
     * @param departureDate Opsiyonel kalkış tarihi (format: yyyy-MM-dd)
     */
    @GetMapping("/search")
    public ResponseEntity<List<FlightBookingDTO>> searchFlights(
            @RequestParam String departureCity,
            @RequestParam String arrivalCity,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate departureDate) {
        
        if (departureDate != null) {
            return ResponseEntity.ok(flightBookingService.searchFlights(departureCity, arrivalCity, departureDate));
        } else {
            return ResponseEntity.ok(flightBookingService.searchFlights(departureCity, arrivalCity));
        }
    }
    
    /**
     * Uçuş sınıfına göre filtrele
     * GET /api/v1/flights/class/{flightClass}
     * 
     * @param flightClass Uçuş sınıfı (Economy, Business, First)
     *                    Not: Gelecekte Enum kullanımı önerilir
     */
    @GetMapping("/class/{flightClass}")
    public ResponseEntity<List<FlightBookingDTO>> getFlightsByClass(@PathVariable String flightClass) {
        return ResponseEntity.ok(flightBookingService.getFlightsByClass(flightClass));
    }
    
    /**
     * Maksimum fiyata göre filtrele
     * GET /api/v1/flights/price?maxPrice=5000
     * 
     * @param maxPrice Maksimum fiyat (varsayılan: 5000)
     */
    @GetMapping("/price")
    public ResponseEntity<List<FlightBookingDTO>> getFlightsByPrice(
            @RequestParam(defaultValue = "5000") BigDecimal maxPrice) {
        return ResponseEntity.ok(flightBookingService.getFlightsByPrice(maxPrice));
    }
    
    /**
     * ID ile uçuş getir
     * GET /api/v1/flights/{id}
     * 
     * Hata yönetimi GlobalExceptionHandler'a devredilmiştir
     */
    @GetMapping("/{id}")
    public ResponseEntity<FlightBookingDTO> getFlightById(@PathVariable Long id) {
        return ResponseEntity.ok(flightBookingService.getFlightById(id));
    }
}

