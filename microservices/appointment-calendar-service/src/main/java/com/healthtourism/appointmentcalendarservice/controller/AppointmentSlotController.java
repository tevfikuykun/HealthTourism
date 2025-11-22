package com.healthtourism.appointmentcalendarservice.controller;
import com.healthtourism.appointmentcalendarservice.dto.AppointmentSlotDTO;
import com.healthtourism.appointmentcalendarservice.entity.AppointmentSlot;
import com.healthtourism.appointmentcalendarservice.service.AppointmentSlotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/appointments")
@CrossOrigin(origins = "*")
public class AppointmentSlotController {
    @Autowired
    private AppointmentSlotService appointmentSlotService;
    
    @GetMapping("/doctor/{doctorId}/available")
    public ResponseEntity<List<AppointmentSlotDTO>> getAvailableSlotsByDoctor(@PathVariable Long doctorId) {
        return ResponseEntity.ok(appointmentSlotService.getAvailableSlotsByDoctor(doctorId));
    }
    
    @GetMapping("/doctor/{doctorId}/range")
    public ResponseEntity<List<AppointmentSlotDTO>> getSlotsByDateRange(
            @PathVariable Long doctorId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        return ResponseEntity.ok(appointmentSlotService.getSlotsByDoctorAndDateRange(doctorId, startDate, endDate));
    }
    
    @PostMapping
    public ResponseEntity<AppointmentSlotDTO> createSlot(@RequestBody AppointmentSlot slot) {
        return ResponseEntity.ok(appointmentSlotService.createSlot(slot));
    }
}

