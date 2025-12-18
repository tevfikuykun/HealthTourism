package com.healthtourism.elasticsearch.controller;

import com.healthtourism.elasticsearch.document.SearchableDocument;
import com.healthtourism.elasticsearch.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/search")
@CrossOrigin(origins = "*")
public class SearchController {
    
    @Autowired
    private SearchService searchService;
    
    @GetMapping
    public ResponseEntity<List<SearchableDocument>> search(@RequestParam String q) {
        return ResponseEntity.ok(searchService.search(q));
    }
    
    @GetMapping("/type/{type}")
    public ResponseEntity<List<SearchableDocument>> searchByType(@PathVariable String type) {
        return ResponseEntity.ok(searchService.searchByType(type));
    }
    
    @PostMapping("/index")
    public ResponseEntity<SearchableDocument> indexDocument(@RequestBody SearchableDocument document) {
        searchService.indexDocument(document);
        return ResponseEntity.ok(document);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDocument(@PathVariable String id) {
        searchService.deleteDocument(id);
        return ResponseEntity.ok().build();
    }
}
