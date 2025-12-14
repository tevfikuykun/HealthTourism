package com.healthtourism.comparisonservice.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.healthtourism.comparisonservice.dto.ComparisonRequest;
import com.healthtourism.comparisonservice.dto.ComparisonResponse;
import com.healthtourism.comparisonservice.entity.Comparison;
import com.healthtourism.comparisonservice.repository.ComparisonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ComparisonService {

    @Autowired
    private ComparisonRepository comparisonRepository;

    @Autowired
    private ObjectMapper objectMapper;

    public ComparisonResponse compare(ComparisonRequest request, Long userId) {
        try {
            // Convert item IDs to JSON string
            String itemIdsJson = objectMapper.writeValueAsString(request.getItems());

            // Check if comparison already exists
            Optional<Comparison> existing = comparisonRepository
                    .findByUserIdAndTypeAndItemIds(userId, request.getType(), itemIdsJson);

            Comparison comparison;
            if (existing.isPresent()) {
                comparison = existing.get();
            } else {
                // Create new comparison
                comparison = new Comparison();
                comparison.setUserId(userId);
                comparison.setType(request.getType());
                comparison.setItemIds(itemIdsJson);
                comparison = comparisonRepository.save(comparison);
            }

            // Build comparison response
            ComparisonResponse response = new ComparisonResponse();
            response.setType(request.getType());
            response.setItemIds(request.getItems());

            // Fetch items from respective services (would need Feign clients)
            List<Map<String, Object>> items = fetchItems(request.getType(), request.getItems());
            response.setItems(items);

            // Build comparison map
            Map<String, Object> comparisonMap = buildComparison(items);
            response.setComparison(comparisonMap);

            // Update comparison data
            comparison.setComparisonData(objectMapper.writeValueAsString(comparisonMap));
            comparisonRepository.save(comparison);

            return response;
        } catch (Exception e) {
            throw new RuntimeException("Karşılaştırma yapılırken hata oluştu: " + e.getMessage());
        }
    }

    public ComparisonResponse getComparison(String type, Long userId) {
        List<Comparison> comparisons = comparisonRepository.findByUserIdAndType(userId, type);
        if (comparisons.isEmpty()) {
            return new ComparisonResponse();
        }

        Comparison latest = comparisons.get(comparisons.size() - 1);
        try {
            ComparisonResponse response = new ComparisonResponse();
            response.setType(latest.getType());
            response.setItemIds(objectMapper.readValue(latest.getItemIds(), new TypeReference<List<Long>>() {}));
            response.setComparison(objectMapper.readValue(latest.getComparisonData(), new TypeReference<Map<String, Object>>() {}));
            return response;
        } catch (Exception e) {
            throw new RuntimeException("Karşılaştırma getirilirken hata oluştu: " + e.getMessage());
        }
    }

    private List<Map<String, Object>> fetchItems(String type, List<Long> itemIds) {
        // This would use Feign clients to fetch from other services
        // For now, return empty list
        return new ArrayList<>();
    }

    private Map<String, Object> buildComparison(List<Map<String, Object>> items) {
        Map<String, Object> comparison = new HashMap<>();
        // Build comparison logic here
        return comparison;
    }
}

