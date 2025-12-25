package com.healthtourism.userservice.entity;

/**
 * Gender Enum
 * 
 * Defines gender options for healthcare records.
 * Important for medical procedures and treatments.
 */
public enum Gender {
    
    MALE("Erkek"),
    FEMALE("Kadın"),
    OTHER("Diğer"),
    PREFER_NOT_TO_SAY("Belirtmek İstemiyorum");
    
    private final String displayName;
    
    Gender(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    /**
     * Find gender by display name (case-insensitive)
     */
    public static Gender fromDisplayName(String displayName) {
        if (displayName == null || displayName.trim().isEmpty()) {
            return null;
        }
        for (Gender gender : values()) {
            if (gender.displayName.equalsIgnoreCase(displayName.trim())) {
                return gender;
            }
        }
        return null;
    }
}

