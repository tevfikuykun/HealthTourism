package com.healthtourism.contactservice.entity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "contact_requests")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContactRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false) private String name;
    @Column(nullable = false) private String email;
    @Column(nullable = false) private String phone;
    @Column(nullable = false) private String subject;
    @Column(nullable = false, length = 2000) private String message;
    @Column(nullable = false) private String status; // PENDING, RESPONDED, CLOSED
    @Column(nullable = false) private LocalDateTime createdAt;
    @Column private LocalDateTime respondedAt;
    @Column(length = 2000) private String response;
}

