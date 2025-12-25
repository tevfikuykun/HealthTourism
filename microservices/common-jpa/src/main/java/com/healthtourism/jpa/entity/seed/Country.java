package com.healthtourism.jpa.entity.seed;

import com.healthtourism.jpa.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Country Entity - Seed Data
 * 
 * Represents countries in the system.
 * This is reference data that should be seeded during initial setup.
 */
@Entity
@Table(
    name = "countries",
    indexes = {
        @Index(name = "idx_countries_code", columnList = "code", unique = true),
        @Index(name = "idx_countries_name", columnList = "name")
    }
)
@Data
@EqualsAndHashCode(callSuper = true)
public class Country extends BaseEntity {
    
    /**
     * ISO 3166-1 alpha-2 country code (e.g., "US", "TR", "GB")
     */
    @Column(name = "code", nullable = false, unique = true, length = 2)
    private String code;
    
    /**
     * Country name (e.g., "United States", "Türkiye", "United Kingdom")
     */
    @Column(name = "name", nullable = false, length = 100)
    private String name;
    
    /**
     * Country name in native language
     */
    @Column(name = "native_name", length = 100)
    private String nativeName;
    
    /**
     * ISO 3166-1 alpha-3 country code (e.g., "USA", "TUR", "GBR")
     */
    @Column(name = "code_alpha3", length = 3)
    private String codeAlpha3;
    
    /**
     * Numeric country code (e.g., 840 for USA, 792 for Turkey)
     */
    @Column(name = "numeric_code", length = 3)
    private String numericCode;
    
    /**
     * Phone code (e.g., "+1" for USA, "+90" for Turkey)
     */
    @Column(name = "phone_code", length = 5)
    private String phoneCode;
    
    /**
     * Whether the country is active/available
     */
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;
    
    /**
     * Display order for UI
     */
    @Column(name = "display_order")
    private Integer displayOrder;
}

