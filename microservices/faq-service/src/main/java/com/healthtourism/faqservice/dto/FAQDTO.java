package com.healthtourism.faqservice.dto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FAQDTO {
    private Long id;
    private String question;
    private String answer;
    private String category;
    private Integer displayOrder;
    private Boolean isActive;
}

