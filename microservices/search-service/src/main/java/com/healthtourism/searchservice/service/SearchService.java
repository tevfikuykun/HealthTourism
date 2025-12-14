package com.healthtourism.searchservice.service;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class SearchService {
    public List<Map<String, Object>> search(String query, Map<String, Object> params) {
        return new ArrayList<>();
    }

    public List<Map<String, Object>> getHistory() {
        return new ArrayList<>();
    }

    public Map<String, Object> saveSearch(Map<String, Object> search) {
        return Map.of("saved", true, "search", search);
    }
}
