package com.healthtourism.doctorservice.entity;

/**
 * Specialization Enum
 * 
 * Medical specializations for doctors.
 * Using enum prevents typos and ensures data consistency.
 * 
 * Note: If you need to add more specializations dynamically, consider using a TreatmentBranch table instead.
 */
public enum Specialization {
    // Cardiology
    CARDIOLOGY("Kardiyoloji"),
    CARDIOVASCULAR_SURGERY("Kalp Damar Cerrahisi"),
    
    // Neurology
    NEUROLOGY("Nöroloji"),
    NEUROSURGERY("Nöroşirurji"),
    
    // Orthopedics
    ORTHOPEDICS("Ortopedi"),
    
    // Surgery
    GENERAL_SURGERY("Genel Cerrahi"),
    PLASTIC_SURGERY("Plastik Cerrahi"),
    AESTHETIC_SURGERY("Estetik Cerrahi"),
    
    // Eye
    OPHTHALMOLOGY("Göz Hastalıkları"),
    
    // ENT
    ENT("Kulak Burun Boğaz"),
    
    // Dermatology
    DERMATOLOGY("Dermatoloji"),
    
    // Urology
    UROLOGY("Üroloji"),
    
    // Gynecology
    GYNECOLOGY("Jinekoloji"),
    
    // Oncology
    ONCOLOGY("Onkoloji"),
    
    // Radiology
    RADIOLOGY("Radyoloji"),
    
    // Anesthesiology
    ANESTHESIOLOGY("Anestezi"),
    
    // Physical Therapy
    PHYSICAL_THERAPY("Fizik Tedavi"),
    
    // Psychiatry
    PSYCHIATRY("Psikiyatri"),
    
    // Pediatrics
    PEDIATRICS("Çocuk Sağlığı"),
    
    // Internal Medicine
    INTERNAL_MEDICINE("İç Hastalıkları"),
    
    // Gastroenterology
    GASTROENTEROLOGY("Gastroenteroloji"),
    
    // Endocrinology
    ENDOCRINOLOGY("Endokrinoloji"),
    
    // Rheumatology
    RHEUMATOLOGY("Romatoloji"),
    
    // Hematology
    HEMATOLOGY("Hematoloji"),
    
    // Nephrology
    NEPHROLOGY("Nefroloji"),
    
    // Pulmonology
    PULMONOLOGY("Pulmonoloji");
    
    private final String displayName;
    
    Specialization(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    /**
     * Find specialization by display name (case-insensitive)
     */
    public static Specialization fromDisplayName(String displayName) {
        if (displayName == null || displayName.trim().isEmpty()) {
            return null;
        }
        for (Specialization spec : values()) {
            if (spec.displayName.equalsIgnoreCase(displayName.trim())) {
                return spec;
            }
        }
        return null;
    }
    
    /**
     * Find specialization by enum name (case-insensitive)
     */
    public static Specialization fromName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return null;
        }
        try {
            return valueOf(name.toUpperCase().replace(" ", "_"));
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}

