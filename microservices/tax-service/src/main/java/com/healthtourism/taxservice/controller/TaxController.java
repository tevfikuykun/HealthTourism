package com.healthtourism.taxservice.controller;
import com.healthtourism.taxservice.service.TaxService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/tax")
@CrossOrigin(origins = "*")
public class TaxController {
    @Autowired
    private TaxService service;

    @PostMapping("/calculate")
    public ResponseEntity<Map<String, Object>> calculate(@RequestBody Map<String, Object> request) {
        Double amount = Double.valueOf(request.get("amount").toString());
        Double taxRate = Double.valueOf(request.get("taxRate").toString());
        Boolean includeTax = request.get("includeTax") != null ? Boolean.valueOf(request.get("includeTax").toString()) : false;
        return ResponseEntity.ok(service.calculate(amount, taxRate, includeTax));
    }
}

