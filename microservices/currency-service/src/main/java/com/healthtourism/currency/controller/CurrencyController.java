package com.healthtourism.currency.controller;

import com.healthtourism.currency.service.CurrencyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/api/currency")
@CrossOrigin(origins = "*")
public class CurrencyController {
    
    @Autowired
    private CurrencyService currencyService;
    
    @GetMapping("/rates/{baseCurrency}")
    public ResponseEntity<Map<String, BigDecimal>> getExchangeRates(@PathVariable String baseCurrency) {
        return ResponseEntity.ok(currencyService.getExchangeRates(baseCurrency));
    }
    
    @GetMapping("/convert")
    public ResponseEntity<BigDecimal> convertCurrency(
            @RequestParam BigDecimal amount,
            @RequestParam String from,
            @RequestParam String to) {
        BigDecimal result = currencyService.convertCurrency(amount, from, to);
        return ResponseEntity.ok(result);
    }
    
    @GetMapping("/rate")
    public ResponseEntity<BigDecimal> getExchangeRate(
            @RequestParam String from,
            @RequestParam String to) {
        BigDecimal rate = currencyService.getExchangeRate(from, to);
        return ResponseEntity.ok(rate);
    }
}
