package com.healthtourism.healthrecordsservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HealthRecordDTO {
    private Long id;
    private Long userId;
    private String type;
    private LocalDate date;
    private String doctorName;
    private String hospitalName;
    private String description;
    private Integer documentCount;
}

