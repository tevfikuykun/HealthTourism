package com.example.HealthTourism.repository;

import com.example.HealthTourism.entity.TravelPackage;
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
 * Enterprise-grade TravelPackageRepository with JOIN FETCH optimization,
 * content-based filtering, pagination support, and dynamic search capabilities.
 * 
 * Özellikler:
 * - JOIN FETCH ile N+1 problemi çözümü (hospital, doctor, accommodation)
 * - İçerik bazlı filtreleme (uçuş dahil, VIP paketler, vb.)
 * - Dinamik arama (keyword bazlı - paket adı veya hastane adı)
 * - Fiyat ve popülerlik bazlı sıralama desteği
 * - Pagination desteği
 * 
 * Operasyonel Strateji: "Soft Selection"
 * Kurumsal bir sağlık turizmi portalında paketlerin "popülerlik" veya "fiyat/performans"
 * skoruna göre sıralanması genellikle Pageable üzerinden dinamik olarak gönderilir.
 * Bu repository yapısı, Service katmanından gelecek olan Sort.by("finalPrice").ascending()
 * komutlarını başarıyla işleyecektir.
 */
@Repository
public interface TravelPackageRepository extends JpaRepository<TravelPackage, Long> {
    
    // ==================== Performans Odaklı Sorgular (N+1 Problemini Engeller) ====================
    
    /**
     * Paketi tüm alt bileşenleriyle tek sorguda çek (N+1'i bitirir).
     * Paket listesi sayfasında doktorun adını ve otelin adını göstermek için
     * performansın düşmemesi için JOIN FETCH şarttır.
     */
    @Query("SELECT t FROM TravelPackage t " +
           "LEFT JOIN FETCH t.hospital " +
           "LEFT JOIN FETCH t.doctor " +
           "LEFT JOIN FETCH t.accommodation " +
           "WHERE t.id = :id AND t.isActive = true")
    Optional<TravelPackage> findByIdWithDetails(@Param("id") Long id);
    
    /**
     * Tüm aktif paketleri ilişkilerle birlikte getir (JOIN FETCH ile).
     */
    @Query("SELECT DISTINCT t FROM TravelPackage t " +
           "LEFT JOIN FETCH t.hospital " +
           "LEFT JOIN FETCH t.doctor " +
           "LEFT JOIN FETCH t.accommodation " +
           "WHERE t.isActive = true")
    List<TravelPackage> findAllActiveWithDetails();
    
    /**
     * Hastane bazlı paketleri ilişkilerle birlikte getir (JOIN FETCH ile).
     */
    @Query("SELECT DISTINCT t FROM TravelPackage t " +
           "LEFT JOIN FETCH t.hospital " +
           "LEFT JOIN FETCH t.doctor " +
           "LEFT JOIN FETCH t.accommodation " +
           "WHERE t.hospital.id = :hospitalId AND t.isActive = true")
    List<TravelPackage> findByHospitalIdWithDetails(@Param("hospitalId") Long hospitalId);
    
    // ==================== İçerik Bazlı Filtreleme ====================
    
    /**
     * İçerik bazlı filtreleme (örn: Uçuş dahil olan Premium paketler).
     * Bazı hastalar sadece uçuşu olan paketleri veya sadece VIP paketleri görmek ister.
     */
    @Query("SELECT t FROM TravelPackage t WHERE t.isActive = true " +
           "AND t.packageType = :type " +
           "AND t.includesFlight = :flight " +
           "AND t.finalPrice <= :maxPrice " +
           "ORDER BY t.rating DESC, t.finalPrice ASC")
    List<TravelPackage> searchPackages(
            @Param("type") String type, 
            @Param("flight") Boolean flight, 
            @Param("maxPrice") BigDecimal maxPrice);
    
    /**
     * İçerik bazlı filtreleme (pagination ile).
     */
    @Query("SELECT t FROM TravelPackage t WHERE t.isActive = true " +
           "AND (:type IS NULL OR t.packageType = :type) " +
           "AND (:includesFlight IS NULL OR t.includesFlight = :includesFlight) " +
           "AND (:includesAccommodation IS NULL OR t.includesAccommodation = :includesAccommodation) " +
           "AND (:includesTransfer IS NULL OR t.includesTransfer = :includesTransfer) " +
           "AND (:maxPrice IS NULL OR t.finalPrice <= :maxPrice)")
    Page<TravelPackage> searchPackagesAdvanced(
            @Param("type") String type,
            @Param("includesFlight") Boolean includesFlight,
            @Param("includesAccommodation") Boolean includesAccommodation,
            @Param("includesTransfer") Boolean includesTransfer,
            @Param("maxPrice") BigDecimal maxPrice,
            Pageable pageable);
    
    /**
     * Uçuş dahil olan paketler.
     */
    Page<TravelPackage> findByIncludesFlightTrueAndIsActiveTrue(Pageable pageable);
    
    /**
     * Konaklama dahil olan paketler.
     */
    Page<TravelPackage> findByIncludesAccommodationTrueAndIsActiveTrue(Pageable pageable);
    
    /**
     * Transfer dahil olan paketler.
     */
    Page<TravelPackage> findByIncludesTransferTrueAndIsActiveTrue(Pageable pageable);
    
    /**
     * VIP paketler (Premium veya VIP tipindeki paketler).
     */
    @Query("SELECT t FROM TravelPackage t WHERE t.isActive = true " +
           "AND t.packageType IN ('Premium', 'VIP') " +
           "ORDER BY t.rating DESC, t.finalPrice ASC")
    Page<TravelPackage> findVIPPackages(Pageable pageable);
    
    /**
     * Tam paketler (uçuş + konaklama + transfer).
     */
    @Query("SELECT t FROM TravelPackage t WHERE t.isActive = true " +
           "AND t.includesFlight = true " +
           "AND t.includesAccommodation = true " +
           "AND t.includesTransfer = true " +
           "ORDER BY t.rating DESC, t.finalPrice ASC")
    Page<TravelPackage> findCompletePackages(Pageable pageable);
    
    // ==================== Dinamik Arama ====================
    
    /**
     * Dinamik arama: Hastane veya paket adına göre.
     * Paket listesi sayfasında arama özelliği için kritik.
     */
    @Query("SELECT DISTINCT t FROM TravelPackage t " +
           "LEFT JOIN FETCH t.hospital " +
           "WHERE t.isActive = true " +
           "AND (LOWER(t.packageName) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(t.hospital.name) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
           "ORDER BY t.rating DESC")
    List<TravelPackage> searchByKeyword(@Param("keyword") String keyword);
    
    /**
     * Dinamik arama (pagination ile).
     */
    @Query("SELECT t FROM TravelPackage t " +
           "WHERE t.isActive = true " +
           "AND (LOWER(t.packageName) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR EXISTS (SELECT h FROM Hospital h WHERE h.id = t.hospital.id " +
           "AND LOWER(h.name) LIKE LOWER(CONCAT('%', :keyword, '%'))))")
    Page<TravelPackage> searchByKeyword(
            @Param("keyword") String keyword, 
            Pageable pageable);
    
    // ==================== Paket Tipine Göre Filtreleme ====================
    
    /**
     * Paket tipine göre filtreleme (pagination ile).
     */
    Page<TravelPackage> findByPackageTypeAndIsActiveTrue(String packageType, Pageable pageable);
    
    /**
     * Hastane bazlı filtreleme (pagination ile).
     */
    Page<TravelPackage> findByHospitalIdAndIsActiveTrue(Long hospitalId, Pageable pageable);
    
    /**
     * Doktor bazlı filtreleme (pagination ile).
     */
    Page<TravelPackage> findByDoctorIdAndIsActiveTrue(Long doctorId, Pageable pageable);
    
    // ==================== Fiyat Bazlı Filtreleme ====================
    
    /**
     * Fiyat aralığına göre filtreleme.
     */
    @Query("SELECT t FROM TravelPackage t WHERE t.isActive = true " +
           "AND t.finalPrice BETWEEN :minPrice AND :maxPrice " +
           "ORDER BY t.finalPrice ASC, t.rating DESC")
    Page<TravelPackage> findByPriceRange(
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice,
            Pageable pageable);
    
    /**
     * En ucuz paketler (belirli bir minimum puan ile).
     */
    @Query("SELECT t FROM TravelPackage t WHERE t.isActive = true " +
           "AND t.rating >= :minRating " +
           "ORDER BY t.finalPrice ASC, t.rating DESC")
    Page<TravelPackage> findCheapestPackages(
            @Param("minRating") Double minRating,
            Pageable pageable);
    
    // ==================== Popülerlik ve Puan Bazlı Sıralama ====================
    
    /**
     * En popüler paketler (puan ve yorum sayısına göre).
     */
    @Query("SELECT t FROM TravelPackage t WHERE t.isActive = true " +
           "ORDER BY t.rating DESC, t.totalReviews DESC")
    Page<TravelPackage> findMostPopularPackages(Pageable pageable);
    
    /**
     * En yüksek indirimli paketler.
     */
    @Query("SELECT t FROM TravelPackage t WHERE t.isActive = true " +
           "ORDER BY t.discountPercentage DESC, t.rating DESC")
    Page<TravelPackage> findHighestDiscountPackages(Pageable pageable);
    
    // ==================== Pagination Desteği ====================
    
    /**
     * Tüm aktif paketler (pagination ile).
     * Paket sayısı arttığında tüm listeyi dönmek yerine sayfalı bir yapıya geçmek,
     * sunucu kaynaklarını korur.
     */
    Page<TravelPackage> findByIsActiveTrue(Pageable pageable);
    
    /**
     * Puan sırasına göre aktif paketler.
     */
    Page<TravelPackage> findByIsActiveTrueOrderByRatingDesc(Pageable pageable);
    
    /**
     * Fiyat sırasına göre aktif paketler.
     */
    Page<TravelPackage> findByIsActiveTrueOrderByFinalPriceAsc(Pageable pageable);
    
    /**
     * İndirim yüzdesine göre aktif paketler.
     */
    Page<TravelPackage> findByIsActiveTrueOrderByDiscountPercentageDesc(Pageable pageable);
    
    // ==================== İstatistik Sorguları ====================
    
    /**
     * Belirli hastane için ortalama paket fiyatı.
     */
    @Query("SELECT AVG(t.finalPrice) FROM TravelPackage t " +
           "WHERE t.hospital.id = :hospitalId AND t.isActive = true")
    Double calculateAveragePriceByHospital(@Param("hospitalId") Long hospitalId);
    
    /**
     * Belirli paket tipi için ortalama fiyat.
     */
    @Query("SELECT AVG(t.finalPrice) FROM TravelPackage t " +
           "WHERE t.packageType = :type AND t.isActive = true")
    Double calculateAveragePriceByType(@Param("type") String type);
    
    // ==================== Legacy methods (kept for backward compatibility) ====================
    
    /**
     * @deprecated Use findByIsActiveTrue(Pageable pageable) instead.
     * This method loads all records into memory which is not scalable.
     */
    @Deprecated
    List<TravelPackage> findByIsActiveTrue();
    
    /**
     * @deprecated Use findByHospitalIdAndIsActiveTrue(Long hospitalId, Pageable pageable) instead.
     */
    @Deprecated
    List<TravelPackage> findByHospitalIdAndIsActiveTrue(Long hospitalId);
    
    /**
     * @deprecated Use findByPackageTypeAndIsActiveTrue(String packageType, Pageable pageable) instead.
     */
    @Deprecated
    List<TravelPackage> findByPackageTypeAndIsActiveTrue(String packageType);
    
    /**
     * @deprecated Use findByPriceRange(BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable) instead.
     */
    @Deprecated
    @Query("SELECT t FROM TravelPackage t WHERE t.isActive = true AND t.finalPrice <= :maxPrice ORDER BY t.finalPrice ASC")
    List<TravelPackage> findByFinalPriceLessThanEqual(@Param("maxPrice") BigDecimal maxPrice);
    
    /**
     * @deprecated Use findByIsActiveTrueOrderByRatingDesc(Pageable pageable) instead.
     */
    @Deprecated
    @Query("SELECT t FROM TravelPackage t WHERE t.isActive = true ORDER BY t.rating DESC")
    List<TravelPackage> findAllActiveOrderByRatingDesc();
    
    /**
     * Find by ID and active status.
     */
    Optional<TravelPackage> findByIdAndIsActiveTrue(Long id);
}
