package com.healthtourism.currencyservice.controller;
import com.healthtourism.currencyservice.service.CurrencyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/currency")
@CrossOrigin(origins = "*")
public class CurrencyController {
    @Autowired
    private CurrencyService service;

    @GetMapping("/rates")
    public ResponseEntity<Map<String, Double>> getRates() {
        return ResponseEntity.ok(service.getRates());
    }

    @PostMapping("/convert")
    public ResponseEntity<Map<String, Object>> convert(@RequestBody Map<String, Object> request) {
        String from = request.get("from").toString();
        String to = request.get("to").toString();
        Double amount = Double.valueOf(request.get("amount").toString());
        return ResponseEntity.ok(service.convert(from, to, amount));
    }
}

