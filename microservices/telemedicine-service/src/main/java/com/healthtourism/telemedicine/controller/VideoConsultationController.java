package com.healthtourism.telemedicine.controller;

import com.healthtourism.telemedicine.entity.VideoConsultation;
import com.healthtourism.telemedicine.service.VideoConsultationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/telemedicine")
@CrossOrigin(origins = "*")
public class VideoConsultationController {
    
    @Autowired
    private VideoConsultationService consultationService;
    
    @PostMapping("/schedule")
    public ResponseEntity<VideoConsultation> scheduleConsultation(
            @RequestParam Long patientId,
            @RequestParam Long doctorId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime scheduledAt) {
        return ResponseEntity.ok(consultationService.scheduleConsultation(patientId, doctorId, scheduledAt));
    }
    
    @PostMapping("/start/{roomId}")
    public ResponseEntity<VideoConsultation> startConsultation(@PathVariable String roomId) {
        try {
            return ResponseEntity.ok(consultationService.startConsultation(roomId));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PostMapping("/end/{roomId}")
    public ResponseEntity<VideoConsultation> endConsultation(
            @PathVariable String roomId,
            @RequestParam(required = false) String notes) {
        try {
            return ResponseEntity.ok(consultationService.endConsultation(roomId, notes));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PostMapping("/cancel/{consultationId}")
    public ResponseEntity<VideoConsultation> cancelConsultation(@PathVariable Long consultationId) {
        try {
            return ResponseEntity.ok(consultationService.cancelConsultation(consultationId));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<VideoConsultation>> getConsultationsByPatient(@PathVariable Long patientId) {
        return ResponseEntity.ok(consultationService.getConsultationsByPatient(patientId));
    }
    
    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<List<VideoConsultation>> getConsultationsByDoctor(@PathVariable Long doctorId) {
        return ResponseEntity.ok(consultationService.getConsultationsByDoctor(doctorId));
    }
    
    @GetMapping("/room/{roomId}")
    public ResponseEntity<VideoConsultation> getConsultationByRoomId(@PathVariable String roomId) {
        try {
            return ResponseEntity.ok(consultationService.getConsultationByRoomId(roomId));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/webrtc/signaling/{roomId}")
    public ResponseEntity<String> getWebRTCSignalingEndpoint(@PathVariable String roomId) {
        return ResponseEntity.ok(consultationService.getWebRTCSignalingEndpoint(roomId));
    }
}
