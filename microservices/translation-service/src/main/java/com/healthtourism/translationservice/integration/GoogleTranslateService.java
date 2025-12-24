package com.healthtourism.translationservice.integration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * Google Translate API Integration Service
 * Provides automatic translation using Google Cloud Translation API
 */
@Service
public class GoogleTranslateService {
    
    @Value("${google.translate.api.key:}")
    private String apiKey;
    
    @Value("${google.translate.api.url:https://translation.googleapis.com/language/translate/v2}")
    private String apiUrl;
    
    private final RestTemplate restTemplate = new RestTemplate();
    
    /**
     * Translate text using Google Translate API
     */
    public String translate(String text, String targetLanguage, String sourceLanguage) {
        if (apiKey == null || apiKey.isEmpty() || apiKey.equals("your_google_translate_api_key")) {
            // Fallback to basic translation or return original text
            return text;
        }
        
        try {
            String url = apiUrl + "?key=" + apiKey;
            
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("q", text);
            requestBody.put("target", targetLanguage);
            if (sourceLanguage != null && !sourceLanguage.isEmpty()) {
                requestBody.put("source", sourceLanguage);
            }
            requestBody.put("format", "text");
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
            
            ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);
            
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                Map<String, Object> data = (Map<String, Object>) response.getBody().get("data");
                if (data != null) {
                    java.util.List<Map<String, Object>> translations = 
                        (java.util.List<Map<String, Object>>) data.get("translations");
                    if (translations != null && !translations.isEmpty()) {
                        return (String) translations.get(0).get("translatedText");
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Google Translate API error: " + e.getMessage());
        }
        
        return text; // Fallback to original text
    }
    
    /**
     * Translate multiple texts
     */
    public java.util.List<String> translateMultiple(java.util.List<String> texts, String targetLanguage, String sourceLanguage) {
        java.util.List<String> results = new java.util.ArrayList<>();
        for (String text : texts) {
            results.add(translate(text, targetLanguage, sourceLanguage));
        }
        return results;
    }
    
    /**
     * Detect language of text
     */
    public String detectLanguage(String text) {
        if (apiKey == null || apiKey.isEmpty() || apiKey.equals("your_google_translate_api_key")) {
            return "en"; // Default to English
        }
        
        try {
            String url = apiUrl.replace("/translate/v2", "/detect") + "?key=" + apiKey;
            
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("q", text);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
            
            ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);
            
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                Map<String, Object> data = (Map<String, Object>) response.getBody().get("data");
                if (data != null) {
                    java.util.List<Map<String, Object>> detections = 
                        (java.util.List<Map<String, Object>>) data.get("detections");
                    if (detections != null && !detections.isEmpty()) {
                        java.util.List<Map<String, Object>> firstDetection = 
                            (java.util.List<Map<String, Object>>) detections.get(0);
                        if (firstDetection != null && !firstDetection.isEmpty()) {
                            return (String) firstDetection.get(0).get("language");
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Google Translate language detection error: " + e.getMessage());
        }
        
        return "en"; // Default to English
    }
    
    /**
     * Get supported languages
     */
    public java.util.List<String> getSupportedLanguages() {
        // Google Translate supports 100+ languages
        return java.util.List.of(
            "af", "sq", "am", "ar", "hy", "az", "eu", "be", "bn", "bs", "bg", "ca", "ceb", "zh", "co", 
            "hr", "cs", "da", "nl", "en", "eo", "et", "fi", "fr", "fy", "gl", "ka", "de", "el", "gu", 
            "ht", "ha", "haw", "he", "hi", "hmn", "hu", "is", "ig", "id", "ga", "it", "ja", "jw", "kn", 
            "kk", "km", "rw", "ko", "ku", "ky", "lo", "la", "lv", "lt", "lb", "mk", "mg", "ms", "ml", 
            "mt", "mi", "mr", "mn", "my", "ne", "no", "ny", "or", "ps", "fa", "pl", "pt", "pa", "ro", 
            "ru", "sm", "gd", "sr", "st", "sn", "sd", "si", "sk", "sl", "so", "es", "su", "sw", "sv", 
            "tl", "tg", "ta", "tt", "te", "th", "tr", "tk", "uk", "ur", "ug", "uz", "vi", "cy", "xh", 
            "yi", "yo", "zu"
        );
    }
}

