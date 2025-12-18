package com.healthtourism.transferservice.controller;

import com.healthtourism.transferservice.dto.TransferServiceDTO;
import com.healthtourism.transferservice.service.TransferServiceService;
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
@RequestMapping("/api/transfers")
@CrossOrigin(origins = "*")
@Tag(name = "Transfer Service", description = "Transfer service management APIs")
public class TransferServiceController {
    @Autowired
    private TransferServiceService transferServiceService;
    
    @GetMapping
    @Operation(summary = "Get all available transfer services")
    public ResponseEntity<List<TransferServiceDTO>> getAllTransfers() {
        return ResponseEntity.ok(transferServiceService.getAllAvailableTransfers());
    }
    
    @GetMapping("/type/{serviceType}")
    @Operation(summary = "Get transfer services by type")
    public ResponseEntity<List<TransferServiceDTO>> getTransfersByType(
            @Parameter(description = "Service type", required = true) @PathVariable String serviceType) {
        return ResponseEntity.ok(transferServiceService.getTransfersByType(serviceType));
    }
    
    @GetMapping("/price")
    @Operation(summary = "Get transfer services by maximum price")
    public ResponseEntity<List<TransferServiceDTO>> getTransfersByPrice(
            @Parameter(description = "Maximum price") @RequestParam(defaultValue = "500") BigDecimal maxPrice) {
        return ResponseEntity.ok(transferServiceService.getTransfersByPrice(maxPrice));
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get transfer service by ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Transfer service found"),
        @ApiResponse(responseCode = "404", description = "Transfer service not found")
    })
    public ResponseEntity<TransferServiceDTO> getTransferById(
            @Parameter(description = "Transfer service ID", required = true) @PathVariable Long id) {
        return ResponseEntity.ok(transferServiceService.getTransferById(id));
    }
}

