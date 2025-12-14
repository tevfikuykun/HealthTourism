package com.healthtourism.whatsappservice.controller;
import com.healthtourism.whatsappservice.service.WhatsAppService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/whatsapp")
@CrossOrigin(origins = "*")
public class WhatsAppController {
    @Autowired
    private WhatsAppService service;

    @PostMapping("/send")
    public ResponseEntity<Map<String, Object>> sendMessage(@RequestBody Map<String, String> request) {
        return ResponseEntity.ok(service.sendMessage(request.get("to"), request.get("message")));
    }

    @PostMapping("/appointment-reminder")
    public ResponseEntity<Map<String, Object>> sendAppointmentReminder(@RequestBody Map<String, Object> request) {
        String phoneNumber = request.get("phoneNumber").toString();
        Map<String, String> appointment = (Map<String, String>) request.get("appointment");
        return ResponseEntity.ok(service.sendAppointmentReminder(phoneNumber, appointment));
    }

    @PostMapping("/welcome")
    public ResponseEntity<Map<String, Object>> sendWelcomeMessage(@RequestBody Map<String, String> request) {
        return ResponseEntity.ok(service.sendWelcomeMessage(request.get("phoneNumber"), request.get("name")));
    }
}

