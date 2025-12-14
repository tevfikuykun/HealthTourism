package com.healthtourism.calendarservice.controller;
import com.healthtourism.calendarservice.service.CalendarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/calendar")
@CrossOrigin(origins = "*")
public class CalendarController {
    @Autowired
    private CalendarService service;

    @GetMapping("/appointments")
    public ResponseEntity<List<Map<String, Object>>> getAppointments(@RequestParam Map<String, Object> params) {
        return ResponseEntity.ok(service.getAppointments(params));
    }

    @PostMapping("/check-conflict")
    public ResponseEntity<Map<String, Object>> checkConflict(@RequestBody Map<String, Object> appointment) {
        return ResponseEntity.ok(service.checkConflict(appointment));
    }

    @GetMapping("/upcoming")
    public ResponseEntity<List<Map<String, Object>>> getUpcoming() {
        return ResponseEntity.ok(service.getUpcoming());
    }
}

