package com.healthtourism.faqservice.controller;
import com.healthtourism.faqservice.dto.FAQDTO;
import com.healthtourism.faqservice.entity.FAQ;
import com.healthtourism.faqservice.service.FAQService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/faq")
@CrossOrigin(origins = "*")
public class FAQController {
    @Autowired
    private FAQService faqService;
    
    @GetMapping
    public ResponseEntity<List<FAQDTO>> getAllFAQs() {
        return ResponseEntity.ok(faqService.getAllActiveFAQs());
    }
    
    @GetMapping("/category/{category}")
    public ResponseEntity<List<FAQDTO>> getFAQsByCategory(@PathVariable String category) {
        return ResponseEntity.ok(faqService.getFAQsByCategory(category));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<FAQDTO> getFAQById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(faqService.getFAQById(id));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @PostMapping
    public ResponseEntity<FAQDTO> createFAQ(@RequestBody FAQ faq) {
        return ResponseEntity.ok(faqService.createFAQ(faq));
    }
}

