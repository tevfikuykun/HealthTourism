package com.healthtourism.videoconsultationservice.controller;
import com.healthtourism.videoconsultationservice.service.VideoConsultationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/video")
@CrossOrigin(origins = "*")
public class VideoConsultationController {
    @Autowired
    private VideoConsultationService service;

    @PostMapping("/start")
    public ResponseEntity<Map<String, Object>> start(@RequestBody Map<String, Object> consultation) {
        return ResponseEntity.ok(service.start(consultation));
    }

    @PostMapping("/end")
    public ResponseEntity<Map<String, Object>> end(@RequestBody Map<String, String> request) {
        return ResponseEntity.ok(service.end(request.get("sessionId")));
    }

    @GetMapping("/sessions")
    public ResponseEntity<List<Map<String, Object>>> getSessions() {
        return ResponseEntity.ok(service.getSessions());
    }
}

