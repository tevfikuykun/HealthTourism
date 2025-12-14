package com.healthtourism.comparisonservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ComparisonRequest {
    private List<Long> items;
    private String type; // hospital, doctor, package
}

