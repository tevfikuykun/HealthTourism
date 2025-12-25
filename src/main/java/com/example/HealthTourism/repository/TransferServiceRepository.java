package com.example.HealthTourism.repository;

import com.example.HealthTourism.entity.TransferService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

/**
 * Enterprise-grade TransferServiceRepository with capacity control,
 * location filtering, pagination support, and advanced search capabilities.
 * 
 * Özellikler:
 * - Kapasite kontrolü (sağlık turizminde refakatçilerle gelen hastalar için kritik)
 * - Lokasyon filtreleme (pickup ve dropoff bazlı)
 * - Fiyat aralığı ve puan önceliği sorguları
 * - VIP hizmet filtreleme (karşılama hizmeti)
 * - Pagination desteği
 * - Dinamik filtreleme sorguları
 */
@Repository
public interface TransferServiceRepository extends JpaRepository<TransferService, Long> {
    
    // ==================== Lokasyon, Tip ve Kapasiteye Göre Gelişmiş Arama ====================
    
    /**
     * Lokasyon, tip ve kapasiteye göre gelişmiş arama.
     * Sağlık turizminde hastalar genellikle refakatçileriyle gelir.
     * Bir transferin sadece müsait olması yetmez, hastanın grubunu
     * (passengerCapacity) taşıyıp taşıyamayacağı sorgulanmalıdır.
     * 
     * @param pickup Alış noktası (örn: "İstanbul Havalimanı")
     * @param dropoff Bırakış noktası (örn: "Memorial Hastanesi")
     * @param minPassengers Minimum yolcu kapasitesi (hasta + refakatçiler)
     * @param vehicleType Araç tipi (opsiyonel, null ise tüm tipler)
     * @return Uygun transfer servisleri
     */
    @Query("SELECT t FROM TransferService t WHERE t.isAvailable = true " +
           "AND t.pickupLocation = :pickup " +
           "AND t.dropoffLocation = :dropoff " +
           "AND t.passengerCapacity >= :minPassengers " +
           "AND (:vehicleType IS NULL OR t.vehicleType = :vehicleType) " +
           "ORDER BY t.rating DESC, t.price ASC")
    List<TransferService> findSuitableTransfers(
            @Param("pickup") String pickup, 
            @Param("dropoff") String dropoff, 
            @Param("minPassengers") Integer minPassengers,
            @Param("vehicleType") String vehicleType);
    
    /**
     * Lokasyon ve kapasiteye göre arama (pagination ile).
     */
    @Query("SELECT t FROM TransferService t WHERE t.isAvailable = true " +
           "AND t.pickupLocation = :pickup " +
           "AND t.dropoffLocation = :dropoff " +
           "AND t.passengerCapacity >= :minPassengers " +
           "ORDER BY t.rating DESC, t.price ASC")
    Page<TransferService> findSuitableTransfersPageable(
            @Param("pickup") String pickup, 
            @Param("dropoff") String dropoff, 
            @Param("minPassengers") Integer minPassengers,
            Pageable pageable);
    
    /**
     * Alış noktasına göre filtreleme.
     * Kullanıcı "İstanbul Havalimanı" seçtiğinde sadece oraya hizmet veren servisler gelmelidir.
     */
    Page<TransferService> findByPickupLocationAndIsAvailableTrue(String pickupLocation, Pageable pageable);
    
    /**
     * Bırakış noktasına göre filtreleme.
     */
    Page<TransferService> findByDropoffLocationAndIsAvailableTrue(String dropoffLocation, Pageable pageable);
    
    /**
     * Alış ve bırakış noktasına göre filtreleme.
     */
    Page<TransferService> findByPickupLocationAndDropoffLocationAndIsAvailableTrue(
            String pickupLocation, 
            String dropoffLocation, 
            Pageable pageable);
    
    /**
     * Araç tipine göre filtreleme (pagination ile).
     */
    Page<TransferService> findByVehicleTypeAndIsAvailableTrue(String vehicleType, Pageable pageable);
    
    /**
     * Servis tipine göre filtreleme (Airport-Hospital, Airport-Hotel, vb.).
     */
    Page<TransferService> findByServiceTypeAndIsAvailableTrue(String serviceType, Pageable pageable);
    
    // ==================== Kapasite Bazlı Filtreleme ====================
    
    /**
     * Minimum yolcu kapasitesine göre filtreleme.
     * Sağlık turizminde refakatçilerle gelen hastalar için kritik.
     */
    @Query("SELECT t FROM TransferService t WHERE t.isAvailable = true " +
           "AND t.passengerCapacity >= :minPassengers " +
           "ORDER BY t.passengerCapacity ASC, t.price ASC")
    Page<TransferService> findByMinPassengerCapacity(
            @Param("minPassengers") Integer minPassengers, 
            Pageable pageable);
    
    /**
     * Belirli yolcu sayısı için uygun servisler (tam kapasite veya üzeri).
     */
    @Query("SELECT t FROM TransferService t WHERE t.isAvailable = true " +
           "AND t.passengerCapacity >= :passengerCount " +
           "ORDER BY t.passengerCapacity ASC, t.rating DESC")
    List<TransferService> findAvailableServicesForPassengerCount(@Param("passengerCount") Integer passengerCount);
    
    // ==================== VIP Hizmet Filtreleme ====================
    
    /**
     * VIP hizmet filtrelemesi (karşılama hizmeti olanlar).
     * Sağlık turizminde havalimanı karşılaması için kritik.
     */
    @Query("SELECT t FROM TransferService t WHERE t.hasMeetAndGreet = true " +
           "AND t.isAvailable = true " +
           "ORDER BY t.rating DESC, t.price ASC")
    List<TransferService> findByHasMeetAndGreetTrueAndIsAvailableTrue();
    
    /**
     * VIP hizmet filtrelemesi (pagination ile).
     */
    Page<TransferService> findByHasMeetAndGreetTrueAndIsAvailableTrueOrderByRatingDesc(Pageable pageable);
    
    /**
     * Karşılama hizmeti ve lokasyon filtrelemesi.
     */
    @Query("SELECT t FROM TransferService t WHERE t.hasMeetAndGreet = true " +
           "AND t.pickupLocation = :pickupLocation " +
           "AND t.isAvailable = true " +
           "ORDER BY t.rating DESC")
    List<TransferService> findVIPServicesByPickupLocation(@Param("pickupLocation") String pickupLocation);
    
    // ==================== Fiyat Aralığı ve Puan Önceliği ====================
    
    /**
     * Fiyat aralığı ve puan önceliği sorgusu (sayfalama ile).
     * Dinamik Pricing: Service katmanında bu taban fiyatı mesafeye göre çarpabilirsin.
     */
    @Query("SELECT t FROM TransferService t WHERE t.isAvailable = true " +
           "AND t.price BETWEEN :minPrice AND :maxPrice " +
           "ORDER BY t.rating DESC, t.price ASC")
    Page<TransferService> findByPriceRange(
            @Param("minPrice") BigDecimal minPrice, 
            @Param("maxPrice") BigDecimal maxPrice, 
            Pageable pageable);
    
    /**
     * En yüksek puanlı servisler (fiyat aralığı ile).
     */
    @Query("SELECT t FROM TransferService t WHERE t.isAvailable = true " +
           "AND t.rating >= :minRating " +
           "AND t.price BETWEEN :minPrice AND :maxPrice " +
           "ORDER BY t.rating DESC, t.price ASC")
    Page<TransferService> findTopRatedInPriceRange(
            @Param("minRating") Double minRating,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice,
            Pageable pageable);
    
    /**
     * En ucuz servisler (belirli bir minimum puan ile).
     */
    @Query("SELECT t FROM TransferService t WHERE t.isAvailable = true " +
           "AND t.rating >= :minRating " +
           "ORDER BY t.price ASC, t.rating DESC")
    Page<TransferService> findCheapestServices(
            @Param("minRating") Double minRating,
            Pageable pageable);
    
    // ==================== Şirket Adına Göre Arama ====================
    
    /**
     * Şirket adına göre arama (büyük/küçük harf duyarsız).
     */
    List<TransferService> findByCompanyNameContainingIgnoreCase(String companyName);
    
    /**
     * Şirket adına göre arama (pagination ile).
     */
    Page<TransferService> findByCompanyNameContainingIgnoreCase(String companyName, Pageable pageable);
    
    /**
     * Şirket adına göre arama (sadece müsait olanlar).
     */
    Page<TransferService> findByCompanyNameContainingIgnoreCaseAndIsAvailableTrue(
            String companyName, 
            Pageable pageable);
    
    // ==================== Özellik Bazlı Filtreleme ====================
    
    /**
     * Bagaj yardımı olan servisler.
     */
    Page<TransferService> findByHasLuggageAssistanceTrueAndIsAvailableTrue(Pageable pageable);
    
    /**
     * WiFi olan servisler.
     */
    Page<TransferService> findByHasWifiTrueAndIsAvailableTrue(Pageable pageable);
    
    /**
     * Çocuk koltuğu olan servisler.
     */
    Page<TransferService> findByHasChildSeatTrueAndIsAvailableTrue(Pageable pageable);
    
    // ==================== Gelişmiş Filtreleme (Çoklu Kriter) ====================
    
    /**
     * Çoklu özellik filtreleme (VIP + WiFi + Bagaj yardımı).
     */
    @Query("SELECT t FROM TransferService t WHERE t.isAvailable = true " +
           "AND t.hasMeetAndGreet = :hasMeetAndGreet " +
           "AND t.hasWifi = :hasWifi " +
           "AND t.hasLuggageAssistance = :hasLuggageAssistance " +
           "ORDER BY t.rating DESC, t.price ASC")
    Page<TransferService> findByMultipleFeatures(
            @Param("hasMeetAndGreet") Boolean hasMeetAndGreet,
            @Param("hasWifi") Boolean hasWifi,
            @Param("hasLuggageAssistance") Boolean hasLuggageAssistance,
            Pageable pageable);
    
    /**
     * Mesafe bazlı filtreleme (belirli mesafe içindeki servisler).
     * Dynamic Pricing için: Bu servislerin price değeri mesafeye göre hesaplanabilir.
     */
    @Query("SELECT t FROM TransferService t WHERE t.isAvailable = true " +
           "AND t.distanceKm <= :maxDistance " +
           "AND t.pickupLocation = :pickupLocation " +
           "ORDER BY t.distanceKm ASC, t.price ASC")
    List<TransferService> findNearbyServices(
            @Param("pickupLocation") String pickupLocation,
            @Param("maxDistance") Double maxDistance);
    
    // ==================== Pagination Desteği ====================
    
    /**
     * Tüm müsait servisler (pagination ile).
     */
    Page<TransferService> findByIsAvailableTrue(Pageable pageable);
    
    /**
     * Puan sırasına göre müsait servisler.
     */
    Page<TransferService> findByIsAvailableTrueOrderByRatingDesc(Pageable pageable);
    
    /**
     * Fiyat sırasına göre müsait servisler.
     */
    Page<TransferService> findByIsAvailableTrueOrderByPriceAsc(Pageable pageable);
    
    // ==================== Legacy methods (kept for backward compatibility) ====================
    
    /**
     * @deprecated Use findByIsAvailableTrue(Pageable pageable) instead.
     * This method loads all records into memory which is not scalable.
     */
    @Deprecated
    List<TransferService> findByIsAvailableTrue();
    
    /**
     * @deprecated Use findByServiceTypeAndIsAvailableTrue(String serviceType, Pageable pageable) instead.
     */
    @Deprecated
    List<TransferService> findByServiceTypeAndIsAvailableTrue(String serviceType);
    
    /**
     * @deprecated Use findByPriceRange(BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable) instead.
     */
    @Deprecated
    @Query("SELECT t FROM TransferService t WHERE t.isAvailable = true AND t.price <= :maxPrice ORDER BY t.price ASC")
    List<TransferService> findByPriceLessThanEqual(@Param("maxPrice") BigDecimal maxPrice);
    
    /**
     * @deprecated Use findByIsAvailableTrueOrderByRatingDesc(Pageable pageable) instead.
     */
    @Deprecated
    @Query("SELECT t FROM TransferService t WHERE t.isAvailable = true ORDER BY t.rating DESC")
    List<TransferService> findAllAvailableOrderByRatingDesc();
    
    /**
     * Find by ID and available status.
     */
    java.util.Optional<TransferService> findByIdAndIsAvailableTrue(Long id);
}
