package com.healthtourism.translationservice.integration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * Yandex Translate API Integration Service
 * Provides translation for Russian and CIS markets
 */
@Service
public class YandexTranslateService {
    
    @Value("${yandex.translate.api.key:}")
    private String apiKey;
    
    @Value("${yandex.translate.api.url:https://translate.yandex.net/api/v1.5/tr.json/translate}")
    private String apiUrl;
    
    private final RestTemplate restTemplate = new RestTemplate();
    
    /**
     * Translate text using Yandex Translate API
     */
    public String translate(String text, String targetLanguage, String sourceLanguage) {
        if (apiKey == null || apiKey.isEmpty() || apiKey.equals("your_yandex_translate_api_key")) {
            return text; // Fallback
        }
        
        try {
            String url = apiUrl + "?key=" + apiKey + "&text=" + java.net.URLEncoder.encode(text, "UTF-8") 
                + "&lang=" + (sourceLanguage != null ? sourceLanguage + "-" : "") + targetLanguage;
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            HttpEntity<String> request = new HttpEntity<>(headers);
            
            ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
            
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                Map<String, Object> data = response.getBody();
                java.util.List<String> translations = (java.util.List<String>) data.get("text");
                if (translations != null && !translations.isEmpty()) {
                    return translations.get(0);
                }
            }
        } catch (Exception e) {
            System.err.println("Yandex Translate API error: " + e.getMessage());
        }
        
        return text; // Fallback
    }
    
    /**
     * Get supported languages
     */
    public java.util.List<String> getSupportedLanguages() {
        // Yandex supports 90+ languages, especially good for Russian/CIS markets
        return java.util.List.of(
            "ru", "en", "tr", "uk", "kk", "az", "be", "hy", "ka", "ky", "lv", "lt", "et", "pl", 
            "cs", "sk", "bg", "mk", "sr", "hr", "sl", "ro", "hu", "fi", "sv", "da", "no", "de", 
            "fr", "es", "it", "pt", "nl", "ar", "zh", "ja", "ko", "hi", "th", "vi", "id", "ms", 
            "sw", "af", "sq", "am", "eu", "bn", "bs", "ca", "ceb", "co", "el", "gu", "ha", "haw", 
            "he", "hmn", "ig", "is", "jw", "kn", "km", "rw", "ku", "lo", "la", "lb", "mg", "ml", 
            "mt", "mi", "mr", "mn", "my", "ne", "ny", "or", "ps", "pa", "sm", "gd", "st", "sn", 
            "sd", "si", "so", "su", "tg", "ta", "tt", "te", "tk", "uz", "ug", "cy", "xh", "yi", 
            "yo", "zu"
        );
    }
}

