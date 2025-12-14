package com.healthtourism.medicationservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MedicationDTO {
    private Long id;
    private Long userId;
    private String name;
    private String dosage;
    private LocalTime time;
    private Integer frequency;
    private Boolean isActive;
}

