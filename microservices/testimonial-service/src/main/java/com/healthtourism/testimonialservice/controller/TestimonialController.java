package com.healthtourism.testimonialservice.controller;
import com.healthtourism.testimonialservice.dto.TestimonialDTO;
import com.healthtourism.testimonialservice.entity.Testimonial;
import com.healthtourism.testimonialservice.service.TestimonialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/testimonials")
@CrossOrigin(origins = "*")
public class TestimonialController {
    @Autowired
    private TestimonialService testimonialService;
    
    @GetMapping
    public ResponseEntity<List<TestimonialDTO>> getAllTestimonials() {
        return ResponseEntity.ok(testimonialService.getAllApprovedTestimonials());
    }
    
    @GetMapping("/hospital/{hospitalId}")
    public ResponseEntity<List<TestimonialDTO>> getTestimonialsByHospital(@PathVariable Long hospitalId) {
        return ResponseEntity.ok(testimonialService.getTestimonialsByHospital(hospitalId));
    }
    
    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<List<TestimonialDTO>> getTestimonialsByDoctor(@PathVariable Long doctorId) {
        return ResponseEntity.ok(testimonialService.getTestimonialsByDoctor(doctorId));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<TestimonialDTO> getTestimonialById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(testimonialService.getTestimonialById(id));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @PostMapping
    public ResponseEntity<TestimonialDTO> createTestimonial(@RequestBody Testimonial testimonial) {
        return ResponseEntity.ok(testimonialService.createTestimonial(testimonial));
    }
    
    @PostMapping("/{id}/approve")
    public ResponseEntity<?> approveTestimonial(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(testimonialService.approveTestimonial(id));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}

