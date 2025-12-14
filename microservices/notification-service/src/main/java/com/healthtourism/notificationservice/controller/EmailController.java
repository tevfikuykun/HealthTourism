package com.healthtourism.notificationservice.controller;

import com.healthtourism.notificationservice.integration.SendGridEmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/notifications/email")
@CrossOrigin(origins = "*")
public class EmailController {
    
    @Autowired
    private SendGridEmailService sendGridEmailService;
    
    @PostMapping("/send")
    public ResponseEntity<Map<String, Object>> sendEmail(@RequestBody Map<String, String> request) {
        String to = request.get("to");
        String subject = request.get("subject");
        String htmlContent = request.get("htmlContent");
        sendGridEmailService.sendEmail(to, subject, htmlContent);
        return ResponseEntity.ok(Map.of("success", true, "message", "Email sent successfully"));
    }
    
    @PostMapping("/welcome")
    public ResponseEntity<Map<String, Object>> sendWelcomeEmail(@RequestBody Map<String, String> request) {
        String to = request.get("to");
        String name = request.get("name");
        sendGridEmailService.sendWelcomeEmail(to, name);
        return ResponseEntity.ok(Map.of("success", true, "message", "Welcome email sent successfully"));
    }
    
    @PostMapping("/appointment-confirmation")
    public ResponseEntity<Map<String, Object>> sendAppointmentConfirmation(@RequestBody Map<String, Object> request) {
        String to = request.get("to").toString();
        String name = request.get("name").toString();
        Map<String, String> appointmentDetails = (Map<String, String>) request.get("appointmentDetails");
        sendGridEmailService.sendAppointmentConfirmation(to, name, appointmentDetails);
        return ResponseEntity.ok(Map.of("success", true, "message", "Appointment confirmation sent successfully"));
    }
    
    @PostMapping("/password-reset")
    public ResponseEntity<Map<String, Object>> sendPasswordResetEmail(@RequestBody Map<String, String> request) {
        String to = request.get("to");
        String resetLink = request.get("resetLink");
        sendGridEmailService.sendPasswordResetEmail(to, resetLink);
        return ResponseEntity.ok(Map.of("success", true, "message", "Password reset email sent successfully"));
    }
}

