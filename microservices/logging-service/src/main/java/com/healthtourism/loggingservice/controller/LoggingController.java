package com.healthtourism.loggingservice.controller;

import com.healthtourism.loggingservice.dto.LogEntryDTO;
import com.healthtourism.loggingservice.service.LoggingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/logging")
@CrossOrigin(origins = "*")
public class LoggingController {
    
    @Autowired
    private LoggingService loggingService;
    
    @PostMapping
    public ResponseEntity<LogEntryDTO> log(@RequestBody LogEntryDTO logEntry) {
        return ResponseEntity.ok(loggingService.log(logEntry));
    }
    
    @GetMapping("/{serviceName}")
    public ResponseEntity<List<LogEntryDTO>> getLogs(
            @PathVariable String serviceName,
            @RequestParam(required = false) String level,
            @RequestParam(required = false) LocalDateTime start,
            @RequestParam(required = false) LocalDateTime end) {
        if (start == null) start = LocalDateTime.now().minusDays(1);
        if (end == null) end = LocalDateTime.now();
        return ResponseEntity.ok(loggingService.getLogs(serviceName, level, start, end));
    }
    
    @GetMapping("/errors")
    public ResponseEntity<List<LogEntryDTO>> getErrorLogs(
            @RequestParam(required = false) LocalDateTime start,
            @RequestParam(required = false) LocalDateTime end) {
        if (start == null) start = LocalDateTime.now().minusDays(1);
        if (end == null) end = LocalDateTime.now();
        return ResponseEntity.ok(loggingService.getErrorLogs(start, end));
    }
}

