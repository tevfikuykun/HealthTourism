package com.healthtourism.doctorservice.entity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Entity
@Table(name = "doctors")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Doctor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String firstName;
    @Column(nullable = false)
    private String lastName;
    @Column(nullable = false)
    private String specialization;
    @Column(nullable = false)
    private String title;
    @Column(length = 2000)
    private String bio;
    @Column(nullable = false)
    private Integer experienceYears;
    @Column(nullable = false)
    private String languages;
    @Column(nullable = false)
    private Double rating;
    @Column(nullable = false)
    private Integer totalReviews;
    @Column(nullable = false)
    private Double consultationFee;
    @Column(nullable = false)
    private Boolean isAvailable;
    @Column(nullable = false)
    private Long hospitalId;
    @Column(length = 500)
    private String imageUrl; // URL to doctor image stored in file-storage-service
    @Column(length = 500)
    private String thumbnailUrl; // URL to thumbnail image
}

