package com.example.HealthTourism.repository;

import com.example.HealthTourism.entity.TranslationService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Enterprise-grade TranslationServiceRepository with language-based search,
 * certification filtering, performance optimization, and advanced search capabilities.
 * 
 * Özellikler:
 * - Dil bazlı arama (LIKE operatörü ile - "English" arandığında "English, German" olan kayıtları da getirir)
 * - Sertifikasyon filtresi (tıbbi çeviri hataları malpraktis riskini artırır - kritik)
 * - Fiyat ve puan bazlı filtreleme
 * - Pagination desteği
 * - Hastane ve muayene tercümanlığı filtreleme
 * 
 * Operasyonel Not: "Language Normalization"
 * Eğer ileride languages alanını ayrı bir tabloda (@ElementCollection) tutmaya karar verirsen,
 * sorgu şu şekilde çok daha profesyonel bir hale gelir:
 * SELECT t FROM TranslationService t JOIN t.languages l WHERE l = :language
 */
@Repository
public interface TranslationServiceRepository extends JpaRepository<TranslationService, Long> {
    
    // ==================== En Kritik: Dil Bazlı Arama ====================
    
    /**
     * En kritik sorgu: Dil bazlı arama.
     * "English" arandığında "English, German" olan kayıtları da getirir.
     * LIKE operatörü kullanılarak languages alanı içinde arama yapılır.
     * 
     * Örnek: "Almanca bilenleri getir" dediğinde sistem sonuç döner.
     * 
     * @param language Aranacak dil (örn: "English", "German", "Turkish")
     * @return İlgili dili bilen müsait tercümanlar
     */
    @Query("SELECT t FROM TranslationService t WHERE t.isAvailable = true " +
           "AND LOWER(t.languages) LIKE LOWER(CONCAT('%', :language, '%')) " +
           "ORDER BY t.rating DESC, t.pricePerHour ASC")
    List<TranslationService> findByLanguage(@Param("language") String language);
    
    /**
     * Dil bazlı arama (pagination ile).
     */
    @Query("SELECT t FROM TranslationService t WHERE t.isAvailable = true " +
           "AND LOWER(t.languages) LIKE LOWER(CONCAT('%', :language, '%')) " +
           "ORDER BY t.rating DESC, t.pricePerHour ASC")
    Page<TranslationService> findByLanguage(
            @Param("language") String language, 
            Pageable pageable);
    
    /**
     * Dil ve sertifikasyon bazlı arama.
     * Tıbbi çeviri için kritik - sadece sertifikalı tercümanlar.
     */
    @Query("SELECT t FROM TranslationService t WHERE t.isAvailable = true " +
           "AND t.isCertified = true " +
           "AND LOWER(t.languages) LIKE LOWER(CONCAT('%', :language, '%')) " +
           "ORDER BY t.rating DESC, t.pricePerHour ASC")
    List<TranslationService> findByLanguageAndCertified(@Param("language") String language);
    
    /**
     * Dil ve hizmet tipi bazlı arama.
     */
    @Query("SELECT t FROM TranslationService t WHERE t.isAvailable = true " +
           "AND t.serviceType = :serviceType " +
           "AND LOWER(t.languages) LIKE LOWER(CONCAT('%', :language, '%')) " +
           "ORDER BY t.rating DESC")
    List<TranslationService> findByLanguageAndServiceType(
            @Param("language") String language,
            @Param("serviceType") String serviceType);
    
    // ==================== Sertifikasyon Filtresi (Tıbbi Çeviri İçin Kritik) ====================
    
    /**
     * Sertifikalı ve hastane uygunluğu olan tercümanlar.
     * Tıbbi çeviri hataları malpraktis riskini artırır - bu yüzden isCertified filtresi önceliklidir.
     */
    @Query("SELECT t FROM TranslationService t WHERE t.isAvailable = true " +
           "AND t.isCertified = true " +
           "AND t.isAvailableForHospital = true " +
           "ORDER BY t.rating DESC, t.pricePerHour ASC")
    List<TranslationService> findCertifiedHospitalTranslators();
    
    /**
     * Sertifikalı ve hastane uygunluğu olan tercümanlar (pagination ile).
     */
    @Query("SELECT t FROM TranslationService t WHERE t.isAvailable = true " +
           "AND t.isCertified = true " +
           "AND t.isAvailableForHospital = true " +
           "ORDER BY t.rating DESC, t.pricePerHour ASC")
    Page<TranslationService> findCertifiedHospitalTranslators(Pageable pageable);
    
    /**
     * Sadece sertifikalı tercümanlar.
     */
    Page<TranslationService> findByIsCertifiedTrueAndIsAvailableTrue(Pageable pageable);
    
    /**
     * Sertifikalı ve muayene tercümanlığı yapabilenler.
     */
    @Query("SELECT t FROM TranslationService t WHERE t.isAvailable = true " +
           "AND t.isCertified = true " +
           "AND t.isAvailableForConsultation = true " +
           "ORDER BY t.rating DESC")
    List<TranslationService> findCertifiedConsultationTranslators();
    
    // ==================== Fiyat ve Puan Odaklı Sıralama ====================
    
    /**
     * Fiyat limitine göre filtreleme (sayfalama ile).
     * Sağlık turizminde hasta bütçesine uygun tercüman bulmak için kritik.
     */
    @Query("SELECT t FROM TranslationService t WHERE t.isAvailable = true " +
           "AND t.pricePerHour <= :maxPrice " +
           "ORDER BY t.rating DESC, t.pricePerHour ASC")
    Page<TranslationService> findByPriceLimit(
            @Param("maxPrice") BigDecimal maxPrice, 
            Pageable pageable);
    
    /**
     * Fiyat aralığına göre filtreleme.
     */
    @Query("SELECT t FROM TranslationService t WHERE t.isAvailable = true " +
           "AND t.pricePerHour BETWEEN :minPrice AND :maxPrice " +
           "ORDER BY t.rating DESC, t.pricePerHour ASC")
    Page<TranslationService> findByPriceRange(
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice,
            Pageable pageable);
    
    /**
     * En ucuz tercümanlar (belirli bir minimum puan ile).
     */
    @Query("SELECT t FROM TranslationService t WHERE t.isAvailable = true " +
           "AND t.rating >= :minRating " +
           "ORDER BY t.pricePerHour ASC, t.rating DESC")
    Page<TranslationService> findCheapestTranslators(
            @Param("minRating") Double minRating,
            Pageable pageable);
    
    // ==================== Hizmet Tipine Göre Filtreleme ====================
    
    /**
     * Hizmet tipine göre puanı en yüksek olanlar.
     * Medical, Legal, General, Document gibi tiplere göre filtreleme.
     */
    @Query("SELECT t FROM TranslationService t WHERE t.serviceType = :type " +
           "AND t.isAvailable = true " +
           "ORDER BY t.rating DESC, t.pricePerHour ASC")
    List<TranslationService> findTopRatedByType(@Param("type") String type);
    
    /**
     * Hizmet tipine göre puanı en yüksek olanlar (pagination ile).
     */
    @Query("SELECT t FROM TranslationService t WHERE t.serviceType = :type " +
           "AND t.isAvailable = true " +
           "ORDER BY t.rating DESC, t.pricePerHour ASC")
    Page<TranslationService> findTopRatedByType(
            @Param("type") String type, 
            Pageable pageable);
    
    /**
     * Tıbbi çeviri yapan sertifikalı tercümanlar (en yüksek puanlı).
     */
    @Query("SELECT t FROM TranslationService t WHERE t.serviceType = 'Medical' " +
           "AND t.isCertified = true " +
           "AND t.isAvailable = true " +
           "ORDER BY t.rating DESC, t.pricePerHour ASC")
    List<TranslationService> findTopRatedMedicalTranslators();
    
    // ==================== Hastane ve Muayene Tercümanlığı ====================
    
    /**
     * Hastanede tercümanlık yapabilenler.
     */
    Page<TranslationService> findByIsAvailableForHospitalTrueAndIsAvailableTrue(Pageable pageable);
    
    /**
     * Muayene tercümanlığı yapabilenler.
     */
    Page<TranslationService> findByIsAvailableForConsultationTrueAndIsAvailableTrue(Pageable pageable);
    
    /**
     * Hem hastane hem muayene tercümanlığı yapabilenler.
     */
    @Query("SELECT t FROM TranslationService t WHERE t.isAvailable = true " +
           "AND t.isAvailableForHospital = true " +
           "AND t.isAvailableForConsultation = true " +
           "ORDER BY t.rating DESC")
    Page<TranslationService> findVersatileTranslators(Pageable pageable);
    
    // ==================== Gelişmiş Filtreleme (Çoklu Kriter) ====================
    
    /**
     * Dil, sertifikasyon ve fiyat bazlı gelişmiş arama.
     * Sağlık turizminde en yaygın kullanım senaryosu.
     */
    @Query("SELECT t FROM TranslationService t WHERE t.isAvailable = true " +
           "AND t.isCertified = :isCertified " +
           "AND LOWER(t.languages) LIKE LOWER(CONCAT('%', :language, '%')) " +
           "AND t.pricePerHour <= :maxPrice " +
           "ORDER BY t.rating DESC, t.pricePerHour ASC")
    Page<TranslationService> findByLanguageCertifiedAndPrice(
            @Param("language") String language,
            @Param("isCertified") Boolean isCertified,
            @Param("maxPrice") BigDecimal maxPrice,
            Pageable pageable);
    
    /**
     * Dil, hizmet tipi ve sertifikasyon bazlı arama.
     */
    @Query("SELECT t FROM TranslationService t WHERE t.isAvailable = true " +
           "AND t.serviceType = :serviceType " +
           "AND t.isCertified = :isCertified " +
           "AND LOWER(t.languages) LIKE LOWER(CONCAT('%', :language, '%')) " +
           "ORDER BY t.rating DESC")
    Page<TranslationService> findByLanguageServiceTypeAndCertified(
            @Param("language") String language,
            @Param("serviceType") String serviceType,
            @Param("isCertified") Boolean isCertified,
            Pageable pageable);
    
    // ==================== Pagination Desteği ====================
    
    /**
     * Tüm müsait tercümanlar (pagination ile).
     */
    Page<TranslationService> findByIsAvailableTrue(Pageable pageable);
    
    /**
     * Puan sırasına göre müsait tercümanlar.
     */
    Page<TranslationService> findByIsAvailableTrueOrderByRatingDesc(Pageable pageable);
    
    /**
     * Fiyat sırasına göre müsait tercümanlar.
     */
    Page<TranslationService> findByIsAvailableTrueOrderByPricePerHourAsc(Pageable pageable);
    
    /**
     * Hizmet tipine göre filtreleme (pagination ile).
     */
    Page<TranslationService> findByServiceTypeAndIsAvailableTrue(String serviceType, Pageable pageable);
    
    // ==================== İstatistik Sorguları ====================
    
    /**
     * Belirli dil için ortalama fiyat hesaplama.
     */
    @Query("SELECT AVG(t.pricePerHour) FROM TranslationService t " +
           "WHERE t.isAvailable = true " +
           "AND LOWER(t.languages) LIKE LOWER(CONCAT('%', :language, '%'))")
    Double calculateAveragePriceByLanguage(@Param("language") String language);
    
    /**
     * Belirli dil için ortalama puan hesaplama.
     */
    @Query("SELECT AVG(t.rating) FROM TranslationService t " +
           "WHERE t.isAvailable = true " +
           "AND LOWER(t.languages) LIKE LOWER(CONCAT('%', :language, '%'))")
    Double calculateAverageRatingByLanguage(@Param("language") String language);
    
    // ==================== Legacy methods (kept for backward compatibility) ====================
    
    /**
     * @deprecated Use findByIsAvailableTrue(Pageable pageable) instead.
     * This method loads all records into memory which is not scalable.
     */
    @Deprecated
    List<TranslationService> findByIsAvailableTrue();
    
    /**
     * @deprecated Use findByServiceTypeAndIsAvailableTrue(String serviceType, Pageable pageable) instead.
     */
    @Deprecated
    List<TranslationService> findByServiceTypeAndIsAvailableTrue(String serviceType);
    
    /**
     * @deprecated Use findByIsCertifiedTrueAndIsAvailableTrue(Pageable pageable) instead.
     */
    @Deprecated
    List<TranslationService> findByIsCertifiedTrueAndIsAvailableTrue();
    
    /**
     * @deprecated Use findByIsAvailableForHospitalTrueAndIsAvailableTrue(Pageable pageable) instead.
     */
    @Deprecated
    List<TranslationService> findByIsAvailableForHospitalTrueAndIsAvailableTrue();
    
    /**
     * @deprecated Use findByIsAvailableForConsultationTrueAndIsAvailableTrue(Pageable pageable) instead.
     */
    @Deprecated
    List<TranslationService> findByIsAvailableForConsultationTrueAndIsAvailableTrue();
    
    /**
     * @deprecated Use findByLanguage(String language) instead.
     * This method may not handle case sensitivity correctly.
     */
    @Deprecated
    @Query("SELECT t FROM TranslationService t WHERE t.isAvailable = true AND t.languages LIKE %:language%")
    List<TranslationService> findByLanguageContaining(@Param("language") String language);
    
    /**
     * @deprecated Use findByIsAvailableTrueOrderByRatingDesc(Pageable pageable) instead.
     */
    @Deprecated
    @Query("SELECT t FROM TranslationService t WHERE t.isAvailable = true ORDER BY t.rating DESC")
    List<TranslationService> findAllActiveOrderByRatingDesc();
    
    /**
     * Find by ID and available status.
     */
    Optional<TranslationService> findByIdAndIsAvailableTrue(Long id);
}
