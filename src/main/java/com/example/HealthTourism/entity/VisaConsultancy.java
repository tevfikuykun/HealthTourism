package com.example.HealthTourism.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "visa_consultancies", indexes = {
    @Index(name = "idx_company_name", columnList = "company_name"),
    @Index(name = "idx_visa_type", columnList = "visa_type"),
    @Index(name = "idx_country", columnList = "country"),
    @Index(name = "idx_is_available", columnList = "is_available"),
    @Index(name = "idx_rating", columnList = "rating"),
    @Index(name = "idx_success_rate", columnList = "success_rate"),
    @Index(name = "idx_processing_days", columnList = "processing_days"),
    @Index(name = "idx_emergency", columnList = "is_emergency_service_available"),
    @Index(name = "idx_consultancy_fee", columnList = "consultancy_fee"),
    // Composite indexes for common query patterns
    @Index(name = "idx_country_type_available", columnList = "country,visa_type,is_available"),
    @Index(name = "idx_type_success_available", columnList = "visa_type,success_rate,is_available"),
    @Index(name = "idx_emergency_available", columnList = "is_emergency_service_available,is_available"),
    @Index(name = "idx_processing_available", columnList = "processing_days,is_available")
})
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
    
    /**
     * Desteklenen ülkeler (virgülle ayrılmış).
     * Bir danışman birden fazla ülkeye bakıyorsa bu alan kullanılır.
     * Örnek: "Turkey, Germany, USA"
     */
    @Column(name = "supported_countries", length = 500)
    private String supportedCountries;

    @Column(nullable = false)
    private BigDecimal serviceFee;
    
    /**
     * Danışmanlık ücreti (serviceFee ile aynı, tutarlılık için).
     * Sorgularda consultancyFee kullanılabilir, entity'de serviceFee ile eşlenir.
     */
    @Column(name = "consultancy_fee")
    private BigDecimal consultancyFee;

    @Column(nullable = false)
    private Integer processingDays; // İşlem süresi (gün)
    
    /**
     * Ortalama işlem süresi (gün) - istatistiksel veri.
     * processingDays ile aynı olabilir veya geçmiş işlemlerin ortalaması olabilir.
     */
    @Column(name = "average_processing_days")
    private Integer averageProcessingDays;

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
    
    /**
     * Başarı oranı (0-100 arası yüzde).
     * Kurumsal güvenilirlik için kritik - danışmanın başarılı vize başvurusu oranı.
     */
    @Column(name = "success_rate", nullable = false)
    private Double successRate = 0.0;

    @Column(nullable = false)
    private Integer totalReviews;
    
    /**
     * Acil vize hizmeti mevcut mu (Emergency Visa).
     * Sağlık turizminde bazen acil müdahale gerekir - hayati vakalarda
     * doğru danışmana ulaşmayı hızlandırır.
     */
    @Column(name = "is_emergency_service_available", nullable = false)
    private Boolean isEmergencyServiceAvailable = false;

    @Column(nullable = false)
    private Boolean isAvailable;
    
    @PrePersist
    protected void onCreate() {
        // consultancyFee serviceFee ile aynıysa otomatik doldur
        if (consultancyFee == null && serviceFee != null) {
            consultancyFee = serviceFee;
        }
        if (averageProcessingDays == null && processingDays != null) {
            averageProcessingDays = processingDays;
        }
        if (successRate == null) {
            successRate = 0.0;
        }
        if (isEmergencyServiceAvailable == null) {
            isEmergencyServiceAvailable = false;
        }
    }
}
