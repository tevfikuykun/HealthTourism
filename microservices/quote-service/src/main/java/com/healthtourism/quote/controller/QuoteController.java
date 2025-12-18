package com.healthtourism.quote.controller;

import com.healthtourism.quote.entity.Quote;
import com.healthtourism.quote.service.QuoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/quotes")
@CrossOrigin(origins = "*")
public class QuoteController {
    
    @Autowired
    private QuoteService quoteService;
    
    @PostMapping
    public ResponseEntity<Quote> createQuote(@RequestBody Quote quote) {
        return ResponseEntity.ok(quoteService.createQuote(quote));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Quote> getQuoteById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(quoteService.getQuoteById(id));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Quote>> getQuotesByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(quoteService.getQuotesByUser(userId));
    }
    
    @GetMapping("/status/{status}")
    public ResponseEntity<List<Quote>> getQuotesByStatus(@PathVariable Quote.QuoteStatus status) {
        return ResponseEntity.ok(quoteService.getQuotesByStatus(status));
    }
    
    @PostMapping("/{id}/submit")
    public ResponseEntity<Quote> submitQuote(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(quoteService.submitQuote(id));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PostMapping("/{id}/send")
    public ResponseEntity<Quote> sendToPatient(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(quoteService.sendToPatient(id));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PostMapping("/{id}/accept")
    public ResponseEntity<Quote> acceptQuote(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(quoteService.acceptQuote(id));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PostMapping("/{id}/reject")
    public ResponseEntity<Quote> rejectQuote(@PathVariable Long id, @RequestParam String reason) {
        try {
            return ResponseEntity.ok(quoteService.rejectQuote(id, reason));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PostMapping("/{id}/convert")
    public ResponseEntity<Quote> convertToReservation(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(quoteService.convertToReservation(id));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
