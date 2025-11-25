package com.healthtourism.chatservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "chat_rooms")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoom {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long participant1Id; // User ID veya Doctor ID

    @Column(nullable = false)
    private Long participant2Id; // User ID veya Doctor ID

    @Column(nullable = false)
    private String participant1Type; // USER, DOCTOR

    @Column(nullable = false)
    private String participant2Type; // USER, DOCTOR

    @Column(nullable = false)
    private LocalDateTime createdAt;

    private LocalDateTime lastMessageAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}

