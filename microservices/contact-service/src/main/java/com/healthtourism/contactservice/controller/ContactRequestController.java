package com.healthtourism.contactservice.controller;
import com.healthtourism.contactservice.dto.ContactRequestDTO;
import com.healthtourism.contactservice.entity.ContactRequest;
import com.healthtourism.contactservice.service.ContactRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/contact")
@CrossOrigin(origins = "*")
public class ContactRequestController {
    @Autowired
    private ContactRequestService contactRequestService;
    
    @GetMapping
    public ResponseEntity<List<ContactRequestDTO>> getAllRequests() {
        return ResponseEntity.ok(contactRequestService.getAllRequests());
    }
    
    @GetMapping("/status/{status}")
    public ResponseEntity<List<ContactRequestDTO>> getRequestsByStatus(@PathVariable String status) {
        return ResponseEntity.ok(contactRequestService.getRequestsByStatus(status));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ContactRequestDTO> getRequestById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(contactRequestService.getRequestById(id));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @PostMapping
    public ResponseEntity<ContactRequestDTO> createRequest(@RequestBody ContactRequest request) {
        return ResponseEntity.ok(contactRequestService.createRequest(request));
    }
    
    @PostMapping("/{id}/respond")
    public ResponseEntity<?> respondToRequest(@PathVariable Long id, @RequestParam String response) {
        try {
            return ResponseEntity.ok(contactRequestService.respondToRequest(id, response));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}

