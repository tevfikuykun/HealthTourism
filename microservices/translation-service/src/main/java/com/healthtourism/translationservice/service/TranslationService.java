package com.healthtourism.translationservice.service;

import com.healthtourism.translationservice.integration.GoogleTranslateService;
import com.healthtourism.translationservice.integration.YandexTranslateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class TranslationService {
    private final Map<String, Map<String, String>> translations = new HashMap<>();
    
    @Autowired(required = false)
    private GoogleTranslateService googleTranslateService;
    
    @Autowired(required = false)
    private YandexTranslateService yandexTranslateService;
    
    public TranslationService() {
        // Örnek çeviriler (gerçekte database'den gelecek)
        Map<String, String> tr = new HashMap<>();
        tr.put("welcome", "Hoş Geldiniz");
        tr.put("hospitals", "Hastaneler");
        translations.put("tr", tr);
        
        Map<String, String> en = new HashMap<>();
        en.put("welcome", "Welcome");
        en.put("hospitals", "Hospitals");
        translations.put("en", en);
        
        Map<String, String> ru = new HashMap<>();
        ru.put("welcome", "Добро пожаловать");
        ru.put("hospitals", "Больницы");
        translations.put("ru", ru);
        
        Map<String, String> ar = new HashMap<>();
        ar.put("welcome", "مرحبا");
        ar.put("hospitals", "المستشفيات");
        translations.put("ar", ar);
    }
    
    public String translate(String key, String targetLanguage) {
        return translate(key, targetLanguage, null);
    }
    
    public String translate(String key, String targetLanguage, String sourceLanguage) {
        // First try cached translations
        Map<String, String> langMap = translations.get(targetLanguage);
        if (langMap != null && langMap.containsKey(key)) {
            return langMap.get(key);
        }
        
        // If not found, try automatic translation
        if (googleTranslateService != null) {
            try {
                String translated = googleTranslateService.translate(key, targetLanguage, sourceLanguage);
                // Cache the translation
                if (langMap == null) {
                    langMap = new HashMap<>();
                    translations.put(targetLanguage, langMap);
                }
                langMap.put(key, translated);
                return translated;
            } catch (Exception e) {
                System.err.println("Google Translate error: " + e.getMessage());
            }
        }
        
        // Fallback to Yandex for Russian/CIS markets
        if (yandexTranslateService != null && 
            (targetLanguage.equals("ru") || targetLanguage.equals("uk") || targetLanguage.equals("kk"))) {
            try {
                String translated = yandexTranslateService.translate(key, targetLanguage, sourceLanguage);
                return translated;
            } catch (Exception e) {
                System.err.println("Yandex Translate error: " + e.getMessage());
            }
        }
        
        return key; // Fallback to key if translation not found
    }
    
    public Map<String, String> translateMultiple(List<String> keys, String targetLanguage) {
        return translateMultiple(keys, targetLanguage, null);
    }
    
    public Map<String, String> translateMultiple(List<String> keys, String targetLanguage, String sourceLanguage) {
        Map<String, String> result = new HashMap<>();
        for (String key : keys) {
            result.put(key, translate(key, targetLanguage, sourceLanguage));
        }
        return result;
    }
    
    public List<String> getSupportedLanguages() {
        // Return all supported languages (cached + Google Translate + Yandex)
        Set<String> languages = new HashSet<>();
        languages.addAll(translations.keySet());
        
        if (googleTranslateService != null) {
            languages.addAll(googleTranslateService.getSupportedLanguages());
        }
        
        if (yandexTranslateService != null) {
            languages.addAll(yandexTranslateService.getSupportedLanguages());
        }
        
        return new ArrayList<>(languages);
    }
    
    public String detectLanguage(String text) {
        if (googleTranslateService != null) {
            return googleTranslateService.detectLanguage(text);
        }
        return "en"; // Default to English
    }
}

