package com.example.HealthTourism.repository;

import com.example.HealthTourism.entity.VisaConsultancy;
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
 * Enterprise-grade VisaConsultancyRepository with country-based specifications,
 * processing time optimization, success rate filtering, and emergency visa support.
 * 
 * Özellikler:
 * - Ülke bazlı spesifikasyon sorguları (X ülkesinden gelen hasta için Y ülkesinin vize gereksinimleri)
 * - İşlem süresi (Processing Time) bazlı filtreleme (ameliyat tarihi yaklaşıyorsa en hızlı danışman)
 * - Başarı oranı (Success Rate) bazlı sıralama (kurumsal güvenilirlik için kritik)
 * - Çoklu ülke desteği (bir danışman birden fazla ülkeye bakıyorsa)
 * - Acil vize (Emergency Visa) desteği (hayati vakalarda doğru danışmana ulaşmayı hızlandırır)
 * - Pagination desteği
 */
@Repository
public interface VisaConsultancyRepository extends JpaRepository<VisaConsultancy, Long> {
    
    // ==================== Ülke Bazlı Spesifikasyon Sorguları ====================
    
    /**
     * Hedef ülke ve vize tipine göre en hızlı danışmanları getir.
     * Sağlık turizminde en çok ihtiyaç duyulan şey:
     * "X ülkesinden gelen hasta için Y ülkesinin vize gereksinimleri"
     * 
     * Hastanın ameliyat tarihi yaklaşıyorsa, vizeyi en hızlı alan danışmanı bulmak gerekir.
     * 
     * @param country Hedef ülke (örn: "Turkey" - Türkiye'ye vize almak için)
     * @param visaType Vize tipi (Medical, Tourist, Business)
     * @return En hızlı danışmanlar (işlem süresine göre sıralı)
     */
    @Query("SELECT v FROM VisaConsultancy v WHERE v.country = :country " +
           "AND v.visaType = :visaType AND v.isAvailable = true " +
           "ORDER BY v.averageProcessingDays ASC NULLS LAST, v.processingDays ASC, v.successRate DESC")
    List<VisaConsultancy> findFastestConsultants(
            @Param("country") String country, 
            @Param("visaType") String visaType);
    
    /**
     * Hedef ülke ve vize tipine göre danışmanlar (pagination ile).
     */
    @Query("SELECT v FROM VisaConsultancy v WHERE v.country = :country " +
           "AND v.visaType = :visaType AND v.isAvailable = true " +
           "ORDER BY v.averageProcessingDays ASC NULLS LAST, v.successRate DESC")
    Page<VisaConsultancy> findByCountryAndVisaType(
            @Param("country") String country,
            @Param("visaType") String visaType,
            Pageable pageable);
    
    /**
     * Hedef ülkeye göre tüm aktif danışmanlar.
     */
    Page<VisaConsultancy> findByCountryAndIsAvailableTrue(String country, Pageable pageable);
    
    /**
     * Vize tipine göre tüm aktif danışmanlar.
     */
    Page<VisaConsultancy> findByVisaTypeAndIsAvailableTrue(String visaType, Pageable pageable);
    
    // ==================== Çoklu Ülke Desteği ====================
    
    /**
     * Desteklenen ülkeye göre danışman bulma.
     * Bir danışman birden fazla ülkeye bakıyorsa bu sorgu kullanılır.
     * 
     * Not: Eğer country alanını String yerine bir Set/List yapsaydık bu sorgu çok güçlü olurdu:
     * SELECT v FROM VisaConsultancy v JOIN v.supportedCountries c WHERE c = :country
     */
    @Query("SELECT v FROM VisaConsultancy v WHERE v.isAvailable = true " +
           "AND (v.country = :country " +
           "OR LOWER(v.supportedCountries) LIKE LOWER(CONCAT('%', :country, '%'))) " +
           "ORDER BY v.successRate DESC, v.processingDays ASC")
    List<VisaConsultancy> findBySupportedCountry(@Param("country") String country);
    
    /**
     * Desteklenen ülkeye göre danışman bulma (pagination ile).
     */
    @Query("SELECT v FROM VisaConsultancy v WHERE v.isAvailable = true " +
           "AND (v.country = :country " +
           "OR LOWER(v.supportedCountries) LIKE LOWER(CONCAT('%', :country, '%')))")
    Page<VisaConsultancy> findBySupportedCountry(
            @Param("country") String country,
            Pageable pageable);
    
    // ==================== İşlem Süresi (Processing Time) Bazlı Filtreleme ====================
    
    /**
     * Belirli süre içinde işlem yapabilen danışmanlar.
     * Hastanın ameliyat tarihi yaklaşıyorsa kritik.
     */
    @Query("SELECT v FROM VisaConsultancy v WHERE v.isAvailable = true " +
           "AND v.processingDays <= :maxDays " +
           "ORDER BY v.processingDays ASC, v.successRate DESC")
    List<VisaConsultancy> findByMaxProcessingDays(@Param("maxDays") Integer maxDays);
    
    /**
     * En hızlı danışmanlar (belirli bir minimum başarı oranı ile).
     */
    @Query("SELECT v FROM VisaConsultancy v WHERE v.isAvailable = true " +
           "AND v.successRate >= :minSuccessRate " +
           "ORDER BY v.processingDays ASC, v.successRate DESC")
    Page<VisaConsultancy> findFastestWithMinSuccessRate(
            @Param("minSuccessRate") Double minSuccessRate,
            Pageable pageable);
    
    // ==================== Başarı Oranı (Success Rate) Bazlı Sıralama ====================
    
    /**
     * Başarı oranı ve fiyat dengesi (sayfalama ile).
     * Kurumsal güvenilirlik için danışmanların başarı oranına göre sıralanması hayati önem taşır.
     */
    @Query("SELECT v FROM VisaConsultancy v WHERE v.isAvailable = true " +
           "AND v.consultancyFee <= :maxFee " +
           "ORDER BY v.successRate DESC, v.rating DESC, v.consultancyFee ASC")
    Page<VisaConsultancy> findPremiumConsultants(
            @Param("maxFee") BigDecimal maxFee, 
            Pageable pageable);
    
    /**
     * En yüksek başarı oranına sahip danışmanlar.
     */
    @Query("SELECT v FROM VisaConsultancy v WHERE v.isAvailable = true " +
           "ORDER BY v.successRate DESC, v.totalReviews DESC, v.rating DESC")
    Page<VisaConsultancy> findTopSuccessRateConsultants(Pageable pageable);
    
    /**
     * Belirli bir minimum başarı oranına sahip danışmanlar.
     */
    @Query("SELECT v FROM VisaConsultancy v WHERE v.isAvailable = true " +
           "AND v.successRate >= :minSuccessRate " +
           "ORDER BY v.successRate DESC, v.processingDays ASC")
    Page<VisaConsultancy> findByMinSuccessRate(
            @Param("minSuccessRate") Double minSuccessRate,
            Pageable pageable);
    
    // ==================== Acil Vize (Emergency Visa) Desteği ====================
    
    /**
     * Acil vize hizmeti veren danışmanlar.
     * Sağlık turizminde bazen acil müdahale gerekir - hayati vakalarda
     * doğru danışmana ulaşmayı hızlandırır.
     */
    @Query("SELECT v FROM VisaConsultancy v WHERE v.isAvailable = true " +
           "AND v.isEmergencyServiceAvailable = true " +
           "ORDER BY v.processingDays ASC, v.successRate DESC")
    List<VisaConsultancy> findEmergencyVisaConsultants();
    
    /**
     * Acil vize hizmeti veren danışmanlar (belirli ülke ve vize tipi için).
     */
    @Query("SELECT v FROM VisaConsultancy v WHERE v.isAvailable = true " +
           "AND v.isEmergencyServiceAvailable = true " +
           "AND v.country = :country " +
           "AND v.visaType = :visaType " +
           "ORDER BY v.processingDays ASC, v.successRate DESC")
    List<VisaConsultancy> findEmergencyConsultantsByCountryAndType(
            @Param("country") String country,
            @Param("visaType") String visaType);
    
    /**
     * Acil vize hizmeti veren danışmanlar (pagination ile).
     */
    Page<VisaConsultancy> findByIsEmergencyServiceAvailableTrueAndIsAvailableTrue(Pageable pageable);
    
    // ==================== Fiyat Bazlı Filtreleme ====================
    
    /**
     * Fiyat aralığına göre danışmanlar.
     */
    @Query("SELECT v FROM VisaConsultancy v WHERE v.isAvailable = true " +
           "AND v.consultancyFee BETWEEN :minFee AND :maxFee " +
           "ORDER BY v.successRate DESC, v.consultancyFee ASC")
    Page<VisaConsultancy> findByFeeRange(
            @Param("minFee") BigDecimal minFee,
            @Param("maxFee") BigDecimal maxFee,
            Pageable pageable);
    
    // ==================== Gelişmiş Filtreleme (Çoklu Kriter) ====================
    
    /**
     * Ülke, vize tipi, işlem süresi ve başarı oranına göre gelişmiş arama.
     * Sağlık turizminde en yaygın kullanım senaryosu.
     */
    @Query("SELECT v FROM VisaConsultancy v WHERE v.isAvailable = true " +
           "AND (:country IS NULL OR v.country = :country " +
           "OR LOWER(v.supportedCountries) LIKE LOWER(CONCAT('%', :country, '%'))) " +
           "AND (:visaType IS NULL OR v.visaType = :visaType) " +
           "AND (:maxProcessingDays IS NULL OR v.processingDays <= :maxProcessingDays) " +
           "AND (:minSuccessRate IS NULL OR v.successRate >= :minSuccessRate) " +
           "AND (:maxFee IS NULL OR v.consultancyFee <= :maxFee) " +
           "ORDER BY v.successRate DESC, v.processingDays ASC, v.consultancyFee ASC")
    Page<VisaConsultancy> searchAdvanced(
            @Param("country") String country,
            @Param("visaType") String visaType,
            @Param("maxProcessingDays") Integer maxProcessingDays,
            @Param("minSuccessRate") Double minSuccessRate,
            @Param("maxFee") BigDecimal maxFee,
            Pageable pageable);
    
    // ==================== Özellik Bazlı Filtreleme ====================
    
    /**
     * Çeviri hizmeti dahil danışmanlar.
     */
    Page<VisaConsultancy> findByIncludesTranslationTrueAndIsAvailableTrue(Pageable pageable);
    
    /**
     * Belge hazırlama hizmeti dahil danışmanlar.
     */
    Page<VisaConsultancy> findByIncludesDocumentPreparationTrueAndIsAvailableTrue(Pageable pageable);
    
    /**
     * Randevu rezervasyonu hizmeti dahil danışmanlar.
     */
    Page<VisaConsultancy> findByIncludesAppointmentBookingTrueAndIsAvailableTrue(Pageable pageable);
    
    // ==================== Pagination Desteği ====================
    
    /**
     * Tüm aktif danışmanlar (pagination ile).
     */
    Page<VisaConsultancy> findByIsAvailableTrue(Pageable pageable);
    
    /**
     * Başarı oranına göre sıralı aktif danışmanlar.
     */
    Page<VisaConsultancy> findByIsAvailableTrueOrderBySuccessRateDesc(Pageable pageable);
    
    /**
     * İşlem süresine göre sıralı aktif danışmanlar.
     */
    Page<VisaConsultancy> findByIsAvailableTrueOrderByProcessingDaysAsc(Pageable pageable);
    
    /**
     * Fiyat sırasına göre aktif danışmanlar.
     */
    Page<VisaConsultancy> findByIsAvailableTrueOrderByConsultancyFeeAsc(Pageable pageable);
    
    // ==================== İstatistik Sorguları ====================
    
    /**
     * Belirli ülke için ortalama işlem süresi.
     */
    @Query("SELECT AVG(v.processingDays) FROM VisaConsultancy v " +
           "WHERE v.country = :country AND v.isAvailable = true")
    Double calculateAverageProcessingDaysByCountry(@Param("country") String country);
    
    /**
     * Belirli ülke için ortalama başarı oranı.
     */
    @Query("SELECT AVG(v.successRate) FROM VisaConsultancy v " +
           "WHERE v.country = :country AND v.isAvailable = true")
    Double calculateAverageSuccessRateByCountry(@Param("country") String country);
    
    // ==================== Legacy methods (kept for backward compatibility) ====================
    
    /**
     * @deprecated Use findByIsAvailableTrue(Pageable pageable) instead.
     * This method loads all records into memory which is not scalable.
     */
    @Deprecated
    List<VisaConsultancy> findByIsAvailableTrue();
    
    /**
     * @deprecated Use findByVisaTypeAndIsAvailableTrue(String visaType, Pageable pageable) instead.
     */
    @Deprecated
    List<VisaConsultancy> findByVisaTypeAndIsAvailableTrue(String visaType);
    
    /**
     * @deprecated Use findByCountryAndIsAvailableTrue(String country, Pageable pageable) instead.
     */
    @Deprecated
    List<VisaConsultancy> findByCountryAndIsAvailableTrue(String country);
    
    /**
     * Find by ID and available status.
     */
    Optional<VisaConsultancy> findByIdAndIsAvailableTrue(Long id);
}
