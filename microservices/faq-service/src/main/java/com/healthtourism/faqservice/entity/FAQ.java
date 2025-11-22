package com.healthtourism.faqservice.entity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "faqs")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FAQ {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false) private String question;
    @Column(nullable = false, length = 2000) private String answer;
    @Column(nullable = false) private String category; // GENERAL, RESERVATION, PAYMENT, MEDICAL, TRAVEL
    @Column(nullable = false) private Integer displayOrder;
    @Column(nullable = false) private Boolean isActive;
}

