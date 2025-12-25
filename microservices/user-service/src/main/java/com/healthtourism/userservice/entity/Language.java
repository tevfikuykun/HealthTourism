package com.healthtourism.userservice.entity;

/**
 * Language Enum
 * 
 * Defines supported languages for communication.
 * Used for notifications (email, SMS) and UI preferences.
 */
public enum Language {
    
    TR("Türkçe", "tr"),
    EN("English", "en"),
    DE("Deutsch", "de"),
    FR("Français", "fr"),
    AR("العربية", "ar"),
    RU("Русский", "ru"),
    ES("Español", "es"),
    IT("Italiano", "it");
    
    private final String displayName;
    private final String code;
    
    Language(String displayName, String code) {
        this.displayName = displayName;
        this.code = code;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public String getCode() {
        return code;
    }
    
    /**
     * Find language by code (case-insensitive)
     */
    public static Language fromCode(String code) {
        if (code == null || code.trim().isEmpty()) {
            return null;
        }
        for (Language language : values()) {
            if (language.code.equalsIgnoreCase(code.trim())) {
                return language;
            }
        }
        return null;
    }
    
    /**
     * Default language
     */
    public static Language getDefault() {
        return EN;
    }
}

