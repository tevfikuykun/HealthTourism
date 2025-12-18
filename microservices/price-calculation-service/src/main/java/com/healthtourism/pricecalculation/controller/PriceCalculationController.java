package com.healthtourism.pricecalculation.controller;

import com.healthtourism.pricecalculation.dto.PriceCalculationRequest;
import com.healthtourism.pricecalculation.dto.PriceCalculationResponse;
import com.healthtourism.pricecalculation.service.PriceCalculationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/price-calculation")
@CrossOrigin(origins = "*")
public class PriceCalculationController {
    
    @Autowired
    private PriceCalculationService priceCalculationService;
    
    @PostMapping("/calculate")
    public ResponseEntity<PriceCalculationResponse> calculatePrice(@RequestBody PriceCalculationRequest request) {
        PriceCalculationResponse response = priceCalculationService.calculatePrice(request);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/calculate-with-discount")
    public ResponseEntity<PriceCalculationResponse> calculatePriceWithDiscount(
            @RequestBody PriceCalculationRequest request,
            @RequestParam(required = false, defaultValue = "0") BigDecimal discountPercentage) {
        PriceCalculationResponse response = priceCalculationService.calculatePriceWithDiscount(request, discountPercentage);
        return ResponseEntity.ok(response);
    }
}
