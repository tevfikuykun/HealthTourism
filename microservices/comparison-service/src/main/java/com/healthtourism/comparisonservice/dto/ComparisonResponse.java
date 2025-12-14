package com.healthtourism.comparisonservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ComparisonResponse {
    private String type;
    private List<Long> itemIds;
    private List<Map<String, Object>> items;
    private Map<String, Object> comparison;
}

