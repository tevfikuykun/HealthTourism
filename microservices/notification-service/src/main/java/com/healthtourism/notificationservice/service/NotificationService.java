package com.healthtourism.notificationservice.service;

import com.healthtourism.notificationservice.dto.NotificationDTO;
import com.healthtourism.notificationservice.dto.NotificationRequestDTO;
import com.healthtourism.notificationservice.entity.Notification;
import com.healthtourism.notificationservice.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class NotificationService {
    
    @Autowired
    private NotificationRepository notificationRepository;
    
    @Autowired(required = false)
    private JavaMailSender mailSender;
    
    @Transactional
    public NotificationDTO sendNotification(NotificationRequestDTO request) {
        Notification notification = new Notification();
        notification.setUserId(request.getUserId());
        notification.setType(request.getType());
        notification.setCategory(request.getCategory());
        notification.setSubject(request.getSubject());
        notification.setMessage(request.getMessage());
        notification.setRecipient(request.getRecipient());
        notification.setStatus("PENDING");
        notification.setCreatedAt(LocalDateTime.now());
        
        // Email gönderimi
        if ("EMAIL".equals(request.getType()) && mailSender != null) {
            try {
                SimpleMailMessage message = new SimpleMailMessage();
                message.setTo(request.getRecipient());
                message.setSubject(request.getSubject());
                message.setText(request.getMessage());
                mailSender.send(message);
                notification.setStatus("SENT");
                notification.setSentAt(LocalDateTime.now());
            } catch (Exception e) {
                notification.setStatus("FAILED");
            }
        } else {
            // SMS veya PUSH için entegrasyon yapılabilir
            notification.setStatus("SENT");
            notification.setSentAt(LocalDateTime.now());
        }
        
        Notification saved = notificationRepository.save(notification);
        return convertToDTO(saved);
    }
    
    public List<NotificationDTO> getNotificationsByUser(Long userId) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    private NotificationDTO convertToDTO(Notification notification) {
        NotificationDTO dto = new NotificationDTO();
        dto.setId(notification.getId());
        dto.setUserId(notification.getUserId());
        dto.setType(notification.getType());
        dto.setCategory(notification.getCategory());
        dto.setSubject(notification.getSubject());
        dto.setMessage(notification.getMessage());
        dto.setRecipient(notification.getRecipient());
        dto.setStatus(notification.getStatus());
        dto.setSentAt(notification.getSentAt());
        dto.setCreatedAt(notification.getCreatedAt());
        return dto;
    }
}

