package com.healthtourism.notificationservice.controller;

import com.healthtourism.notificationservice.integration.TwilioSMSService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/notifications/sms")
@CrossOrigin(origins = "*")
public class SMSController {
    
    @Autowired
    private TwilioSMSService twilioSMSService;
    
    @PostMapping("/send")
    public ResponseEntity<Map<String, Object>> sendSMS(@RequestBody Map<String, String> request) {
        String to = request.get("to");
        String message = request.get("message");
        twilioSMSService.sendSMS(to, message);
        return ResponseEntity.ok(Map.of("success", true, "message", "SMS sent successfully"));
    }
    
    @PostMapping("/otp")
    public ResponseEntity<Map<String, Object>> sendOTP(@RequestBody Map<String, String> request) {
        String phoneNumber = request.get("phoneNumber");
        String otp = request.get("otp");
        twilioSMSService.sendOTP(phoneNumber, otp);
        return ResponseEntity.ok(Map.of("success", true, "message", "OTP sent successfully"));
    }
    
    @PostMapping("/appointment-reminder")
    public ResponseEntity<Map<String, Object>> sendAppointmentReminder(@RequestBody Map<String, String> request) {
        String phoneNumber = request.get("phoneNumber");
        String hospitalName = request.get("hospitalName");
        String date = request.get("date");
        twilioSMSService.sendAppointmentReminder(phoneNumber, hospitalName, date);
        return ResponseEntity.ok(Map.of("success", true, "message", "Reminder sent successfully"));
    }
}

