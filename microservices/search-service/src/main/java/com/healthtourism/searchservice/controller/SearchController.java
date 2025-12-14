package com.healthtourism.searchservice.controller;
import com.healthtourism.searchservice.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/search")
@CrossOrigin(origins = "*")
public class SearchController {
    @Autowired
    private SearchService service;

    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> search(
            @RequestParam String q,
            @RequestParam Map<String, Object> params) {
        return ResponseEntity.ok(service.search(q, params));
    }

    @GetMapping("/history")
    public ResponseEntity<List<Map<String, Object>>> getHistory() {
        return ResponseEntity.ok(service.getHistory());
    }

    @PostMapping("/save")
    public ResponseEntity<Map<String, Object>> saveSearch(@RequestBody Map<String, Object> search) {
        return ResponseEntity.ok(service.saveSearch(search));
    }
}
