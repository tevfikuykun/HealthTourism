package com.example.HealthTourism.controller;

import com.example.HealthTourism.dto.TransferServiceDTO;
import com.example.HealthTourism.service.TransferServiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/transfers")
@CrossOrigin(origins = "*")
public class TransferServiceController {
    
    @Autowired
    private TransferServiceService transferServiceService;
    
    @GetMapping
    public ResponseEntity<List<TransferServiceDTO>> getAllTransfers() {
        return ResponseEntity.ok(transferServiceService.getAllAvailableTransfers());
    }
    
    @GetMapping("/type/{serviceType}")
    public ResponseEntity<List<TransferServiceDTO>> getTransfersByType(@PathVariable String serviceType) {
        return ResponseEntity.ok(transferServiceService.getTransfersByType(serviceType));
    }
    
    @GetMapping("/price")
    public ResponseEntity<List<TransferServiceDTO>> getTransfersByPrice(
            @RequestParam(defaultValue = "500") BigDecimal maxPrice) {
        return ResponseEntity.ok(transferServiceService.getTransfersByPrice(maxPrice));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<TransferServiceDTO> getTransferById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(transferServiceService.getTransferById(id));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}

