package com.healthtourism.notificationservice.controller;

import com.healthtourism.notificationservice.dto.NotificationDTO;
import com.healthtourism.notificationservice.dto.NotificationRequestDTO;
import com.healthtourism.notificationservice.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@CrossOrigin(origins = "*")
public class NotificationController {
    
    @Autowired
    private NotificationService notificationService;
    
    @PostMapping
    public ResponseEntity<NotificationDTO> sendNotification(@RequestBody NotificationRequestDTO request) {
        return ResponseEntity.ok(notificationService.sendNotification(request));
    }
    
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<NotificationDTO>> getNotificationsByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(notificationService.getNotificationsByUser(userId));
    }
}

