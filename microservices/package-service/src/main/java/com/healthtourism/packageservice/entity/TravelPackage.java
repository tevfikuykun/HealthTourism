package com.healthtourism.packageservice.entity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Entity
@Table(name = "travel_packages")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TravelPackage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false) private String packageName;
    @Column(nullable = false) private String packageType;
    @Column(nullable = false) private Integer durationDays;
    @Column(nullable = false) private BigDecimal totalPrice;
    @Column(nullable = false) private BigDecimal discountPercentage;
    @Column(nullable = false) private BigDecimal finalPrice;
    @Column(nullable = false) private Boolean includesFlight;
    @Column(nullable = false) private Boolean includesAccommodation;
    @Column(nullable = false) private Boolean includesTransfer;
    @Column(nullable = false) private Boolean includesCarRental;
    @Column(nullable = false) private Boolean includesVisaService;
    @Column(nullable = false) private Boolean includesTranslation;
    @Column(nullable = false) private Boolean includesInsurance;
    @Column(length = 2000) private String description;
    @Column(nullable = false) private Double rating;
    @Column(nullable = false) private Integer totalReviews;
    @Column(nullable = false) private Boolean isActive;
    @Column(nullable = false) private Long hospitalId;
    @Column private Long doctorId;
    @Column private Long accommodationId;
}

