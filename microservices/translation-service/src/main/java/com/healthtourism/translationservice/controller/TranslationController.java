package com.healthtourism.translationservice.controller;
import com.healthtourism.translationservice.service.TranslationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/translation")
@CrossOrigin(origins = "*")
public class TranslationController {
    @Autowired
    private TranslationService service;

    @GetMapping("/translate")
    public ResponseEntity<Map<String, String>> translate(
            @RequestParam String key,
            @RequestParam String targetLanguage) {
        return ResponseEntity.ok(Map.of("key", key, "translation", service.translate(key, targetLanguage)));
    }

    @PostMapping("/translate-multiple")
    public ResponseEntity<Map<String, String>> translateMultiple(
            @RequestBody Map<String, Object> request) {
        List<String> keys = (List<String>) request.get("keys");
        String targetLanguage = request.get("targetLanguage").toString();
        return ResponseEntity.ok(service.translateMultiple(keys, targetLanguage));
    }

    @GetMapping("/languages")
    public ResponseEntity<List<String>> getSupportedLanguages() {
        return ResponseEntity.ok(service.getSupportedLanguages());
    }
}

