package com.healthtourism.chatservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "messages")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long senderId; // User ID veya Doctor ID

    @Column(nullable = false)
    private Long receiverId; // User ID veya Doctor ID

    @Column(nullable = false)
    private String senderType; // USER, DOCTOR

    @Column(nullable = false)
    private String receiverType; // USER, DOCTOR

    @Column(nullable = false, length = 5000)
    private String content;

    @Column(nullable = false)
    private String messageType; // TEXT, FILE, IMAGE

    @Column(nullable = false)
    private Boolean isRead;

    @Column(nullable = false)
    private LocalDateTime sentAt;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    private String fileUrl; // Dosya varsa URL

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (sentAt == null) {
            sentAt = LocalDateTime.now();
        }
        if (isRead == null) {
            isRead = false;
        }
    }
}

