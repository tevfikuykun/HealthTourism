package com.healthtourism.currencyconversionservice.controller;

import com.healthtourism.currencyconversionservice.dto.CurrencyConversionRequest;
import com.healthtourism.currencyconversionservice.dto.CurrencyConversionResponse;
import com.healthtourism.currencyconversionservice.service.CurrencyConversionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/currency")
@CrossOrigin(origins = "*")
public class CurrencyConversionController {
    @Autowired
    private CurrencyConversionService currencyConversionService;
    
    @GetMapping("/rate")
    public ResponseEntity<BigDecimal> getExchangeRate(
            @RequestParam String fromCurrency,
            @RequestParam String toCurrency) {
        BigDecimal rate = currencyConversionService.getExchangeRate(fromCurrency, toCurrency);
        return ResponseEntity.ok(rate);
    }
    
    @PostMapping("/convert")
    public ResponseEntity<CurrencyConversionResponse> convertCurrency(
            @RequestBody CurrencyConversionRequest request) {
        CurrencyConversionResponse response = currencyConversionService.convertCurrency(request);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/convert")
    public ResponseEntity<CurrencyConversionResponse> convertCurrencyGet(
            @RequestParam BigDecimal amount,
            @RequestParam String fromCurrency,
            @RequestParam String toCurrency) {
        CurrencyConversionRequest request = new CurrencyConversionRequest(amount, fromCurrency, toCurrency);
        CurrencyConversionResponse response = currencyConversionService.convertCurrency(request);
        return ResponseEntity.ok(response);
    }
}

