package com.healthtourism.insuranceservice.entity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Entity
@Table(name = "insurances")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Insurance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false) private String companyName;
    @Column(nullable = false) private String insuranceType; // TRAVEL, MEDICAL, COMPREHENSIVE
    @Column(nullable = false) private String coverageArea; // TURKEY, INTERNATIONAL, GLOBAL
    @Column(nullable = false) private BigDecimal coverageAmount;
    @Column(nullable = false) private BigDecimal premium;
    @Column(nullable = false) private Integer validityDays;
    @Column(length = 2000) private String coverageDetails;
    @Column(length = 2000) private String exclusions;
    @Column(nullable = false) private String phone;
    @Column(nullable = false) private String email;
    @Column(length = 2000) private String description;
    @Column(nullable = false) private Double rating;
    @Column(nullable = false) private Integer totalReviews;
    @Column(nullable = false) private Boolean isActive;
}

