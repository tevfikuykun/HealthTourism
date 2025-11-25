package com.healthtourism.visaconsultationservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VisaConsultancyDTO {
    private Long id;
    private String companyName;
    private String visaType;
    private String country;
    private BigDecimal serviceFee;
    private Integer processingDays;
    private Boolean includesTranslation;
    private Boolean includesDocumentPreparation;
    private Boolean includesAppointmentBooking;
    private String phone;
    private String email;
    private String description;
    private Double rating;
    private Integer totalReviews;
    private Boolean isAvailable;
}

