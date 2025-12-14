package com.healthtourism.translationservice.service;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class TranslationService {
    private final Map<String, Map<String, String>> translations = new HashMap<>();
    
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
        Map<String, String> langMap = translations.get(targetLanguage);
        if (langMap != null && langMap.containsKey(key)) {
            return langMap.get(key);
        }
        return key; // Fallback to key if translation not found
    }
    
    public Map<String, String> translateMultiple(List<String> keys, String targetLanguage) {
        Map<String, String> result = new HashMap<>();
        for (String key : keys) {
            result.put(key, translate(key, targetLanguage));
        }
        return result;
    }
    
    public List<String> getSupportedLanguages() {
        return List.of("tr", "en", "ru", "ar", "de", "fr", "es", "it", "pt", "zh", "ja", "ko");
    }
}

