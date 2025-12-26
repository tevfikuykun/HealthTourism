package com.example.HealthTourism.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VisaConsultancyDTO {
    private Long id;
    private String companyName;
    private String visaType; // Tourist, Medical, Business
    private String country; // Hangi ülke için vize
    private String supportedCountries; // Desteklenen ülkeler (virgülle ayrılmış)
    private BigDecimal serviceFee;
    private BigDecimal consultancyFee;
    private Integer processingDays; // İşlem süresi (gün)
    private Integer averageProcessingDays; // Ortalama işlem süresi (gün)
    private Boolean includesTranslation;
    private Boolean includesDocumentPreparation;
    private Boolean includesAppointmentBooking;
    private String phone;
    private String email;
    private String description;
    private Double rating;
    private Double successRate; // Başarı oranı (0-100)
    private Integer totalReviews;
    private Boolean isEmergencyServiceAvailable; // Acil vize hizmeti mevcut mu
    private Boolean isAvailable;
    
    /**
     * Gerekli belgeler (virgülle ayrılmış string).
     * Frontend için parse edilmiş liste versiyonu da sağlanabilir.
     */
    private String requiredDocuments;
    
    /**
     * Gerekli belgeler (liste formatında).
     * Frontend tarafında kullanıcı deneyimini iyileştirmek için parse edilmiş versiyon.
     * Service katmanında String.split(",") ile oluşturulur.
     */
    private List<String> requiredDocumentsList;
}

