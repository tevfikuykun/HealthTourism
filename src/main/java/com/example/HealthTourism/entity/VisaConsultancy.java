package com.example.HealthTourism.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "visa_consultancies")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VisaConsultancy {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String companyName;

    @Column(nullable = false)
    private String visaType; // Tourist, Medical, Business

    @Column(nullable = false)
    private String country; // Hangi ülke için vize

    @Column(nullable = false)
    private BigDecimal serviceFee;

    @Column(nullable = false)
    private Integer processingDays; // İşlem süresi (gün)

    @Column(nullable = false)
    private Boolean includesTranslation;

    @Column(nullable = false)
    private Boolean includesDocumentPreparation;

    @Column(nullable = false)
    private Boolean includesAppointmentBooking;

    @Column(nullable = false)
    private String phone;

    @Column(nullable = false)
    private String email;

    @Column(length = 2000)
    private String description;

    @Column(nullable = false)
    private Double rating;

    @Column(nullable = false)
    private Integer totalReviews;

    @Column(nullable = false)
    private Boolean isAvailable;
}

