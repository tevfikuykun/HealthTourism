package com.example.HealthTourism.repository;

import com.example.HealthTourism.entity.Hospital;
import com.example.HealthTourism.repository.projection.HospitalStats;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Enterprise-grade HospitalRepository with specialty-based search,
 * accreditation filtering, JOIN FETCH optimization, and analytics support.
 * 
 * Özellikler:
 * - Branş bazlı arama (doktorların uzmanlıkları üzerinden hastane bulma)
 * - Akreditasyon filtrelemesi (JCI, ISO vb.)
 * - JOIN FETCH ile N+1 problemi çözümü
 * - Havalimanına yakın ve yüksek puanlı hastane sorguları
 * - Şehir ve ilçe bazlı pagination
 * - Veri analitiği için Projection desteği
 */
@Repository
public interface HospitalRepository extends JpaRepository<Hospital, Long>, JpaSpecificationExecutor<Hospital> {
    
    // ==================== Performans Odaklı Sorgular (N+1 Problemini Engeller) ====================
    
    /**
     * Hastane ve doktorlarını tek sorguda getir (JOIN FETCH ile).
     * Performans odaklı: N+1 problemini engeller.
     */
    @Query("SELECT DISTINCT h FROM Hospital h LEFT JOIN FETCH h.doctors WHERE h.id = :id AND h.isActive = true")
    Optional<Hospital> findByIdWithDoctors(@Param("id") Long id);
    
    /**
     * Hastane, doktorlar ve konaklamaları tek sorguda getir.
     */
    @Query("SELECT DISTINCT h FROM Hospital h " +
           "LEFT JOIN FETCH h.doctors " +
           "LEFT JOIN FETCH h.accommodations " +
           "WHERE h.id = :id AND h.isActive = true")
    Optional<Hospital> findByIdWithDetails(@Param("id") Long id);
    
    /**
     * Şehir bazlı hastaneleri doktorlarıyla birlikte getir (pagination ile).
     */
    @Query(value = "SELECT DISTINCT h FROM Hospital h LEFT JOIN FETCH h.doctors " +
           "WHERE h.city = :city AND h.isActive = true",
           countQuery = "SELECT COUNT(DISTINCT h) FROM Hospital h WHERE h.city = :city AND h.isActive = true")
    Page<Hospital> findByCityWithDoctors(@Param("city") String city, Pageable pageable);
    
    // ==================== Branş Bazlı Arama (Tıbbi Uzmanlık) ====================
    
    /**
     * Tıbbi branş ve şehir bazlı hastane arama.
     * Doktorların uzmanlıkları üzerinden hastane bulur.
     * Örnek: "İstanbul'da Diş Tedavisi yapan hastaneler"
     * 
     * @param specialty Uzmanlık alanı (örn: "Diş", "Kardiyoloji", "Ortopedi")
     * @param city Şehir
     * @return Belirtilen branşta hizmet veren hastaneler
     */
    @Query("SELECT DISTINCT h FROM Hospital h JOIN h.doctors d " +
           "WHERE LOWER(d.specialization) LIKE LOWER(CONCAT('%', :spec, '%')) " +
           "AND h.city = :city AND h.isActive = true " +
           "ORDER BY h.rating DESC, h.totalReviews DESC")
    List<Hospital> findHospitalsBySpecialtyAndCity(
            @Param("spec") String specialty, 
            @Param("city") String city);
    
    /**
     * Branş bazlı arama (pagination ile).
     */
    @Query("SELECT DISTINCT h FROM Hospital h JOIN h.doctors d " +
           "WHERE LOWER(d.specialization) LIKE LOWER(CONCAT('%', :spec, '%')) " +
           "AND h.city = :city AND h.isActive = true " +
           "ORDER BY h.rating DESC, h.totalReviews DESC")
    Page<Hospital> findHospitalsBySpecialtyAndCity(
            @Param("spec") String specialty, 
            @Param("city") String city,
            Pageable pageable);
    
    /**
     * Branş bazlı arama (şehir filtresi olmadan).
     */
    @Query("SELECT DISTINCT h FROM Hospital h JOIN h.doctors d " +
           "WHERE LOWER(d.specialization) LIKE LOWER(CONCAT('%', :spec, '%')) " +
           "AND h.isActive = true " +
           "ORDER BY h.rating DESC, h.totalReviews DESC")
    Page<Hospital> findHospitalsBySpecialty(
            @Param("spec") String specialty,
            Pageable pageable);
    
    // ==================== Akreditasyon Bazlı Güven Sorguları ====================
    
    /**
     * Akreditasyon bazlı hastane arama.
     * Sağlık turizminde güven her şeydir - JCI veya ISO onaylı hastaneleri ön plana çıkarır.
     * 
     * @param accreditation Akreditasyon türü (örn: "JCI", "ISO", "TÜV")
     * @return Belirtilen akreditasyona sahip hastaneler
     */
    @Query("SELECT DISTINCT h FROM Hospital h " +
           "WHERE :accreditation MEMBER OF h.accreditations " +
           "AND h.isActive = true " +
           "ORDER BY h.rating DESC")
    List<Hospital> findByAccreditation(@Param("accreditation") String accreditation);
    
    /**
     * Akreditasyon bazlı arama (pagination ile).
     */
    @Query("SELECT DISTINCT h FROM Hospital h " +
           "WHERE :accreditation MEMBER OF h.accreditations " +
           "AND h.isActive = true " +
           "ORDER BY h.rating DESC")
    Page<Hospital> findByAccreditation(@Param("accreditation") String accreditation, Pageable pageable);
    
    /**
     * Birden fazla akreditasyona sahip hastaneler (premium hastaneler).
     */
    @Query("SELECT DISTINCT h FROM Hospital h " +
           "WHERE SIZE(h.accreditations) >= :minCount " +
           "AND h.isActive = true " +
           "ORDER BY SIZE(h.accreditations) DESC, h.rating DESC")
    Page<Hospital> findHospitalsWithMultipleAccreditations(
            @Param("minCount") Integer minCount,
            Pageable pageable);
    
    // ==================== Havalimanına Yakın ve Yüksek Puanlı Hastaneler ====================
    
    /**
     * En hızlı transfer: Havalimanına yakın ve puanı yüksek hastaneler.
     * Sağlık turizmi hastaları için kritik - havalimanından hızlı ulaşım önemlidir.
     * 
     * @param maxDist Maksimum mesafe (km)
     * @param pageable Pagination
     * @return Havalimanına yakın ve yüksek puanlı hastaneler
     */
    @Query("SELECT h FROM Hospital h WHERE h.isActive = true " +
           "AND h.airportDistance <= :maxDist " +
           "ORDER BY h.rating DESC, h.airportDistance ASC")
    Page<Hospital> findTopRatedAndNearAirport(
            @Param("maxDist") Double maxDist, 
            Pageable pageable);
    
    /**
     * Havalimanına yakın hastaneler (mesafe bazlı sıralama).
     */
    @Query("SELECT h FROM Hospital h WHERE h.isActive = true " +
           "AND h.airportDistance <= :maxDist " +
           "ORDER BY h.airportDistance ASC, h.rating DESC")
    Page<Hospital> findNearAirport(
            @Param("maxDist") Double maxDist,
            Pageable pageable);
    
    // ==================== Şehir ve İlçe Bazlı Sayfalama ====================
    
    /**
     * Şehir ve ilçe bazlı hastane arama (pagination ile).
     */
    Page<Hospital> findByCityAndDistrictAndIsActiveTrue(String city, String district, Pageable pageable);
    
    /**
     * Şehir bazlı hastane arama (pagination ile).
     */
    Page<Hospital> findByCityAndIsActiveTrue(String city, Pageable pageable);
    
    /**
     * İlçe bazlı hastane arama (pagination ile).
     */
    Page<Hospital> findByDistrictAndIsActiveTrue(String district, Pageable pageable);
    
    /**
     * Tüm aktif hastaneler (pagination ile).
     */
    Page<Hospital> findByIsActiveTrue(Pageable pageable);
    
    /**
     * En yüksek puanlı hastaneler (pagination ile).
     */
    @Query("SELECT h FROM Hospital h WHERE h.isActive = true " +
           "ORDER BY h.rating DESC, h.totalReviews DESC")
    Page<Hospital> findAllActiveOrderByRatingDesc(Pageable pageable);
    
    // ==================== Veri Analitiği (Reporting) ====================
    
    /**
     * Şehir bazlı hastane istatistikleri.
     * Kurumsal yönetim paneli için: Hangi şehirlerde yoğunluk var?
     */
    @Query("SELECT h.city as city, COUNT(h) as hospitalCount, AVG(h.rating) as averageRating " +
           "FROM Hospital h WHERE h.isActive = true " +
           "GROUP BY h.city " +
           "ORDER BY hospitalCount DESC")
    List<HospitalStats> getHospitalStatsByCity();
    
    /**
     * Akreditasyon bazlı istatistikler.
     */
    @Query("SELECT ha.accreditation as accreditation, COUNT(DISTINCT h.id) as hospitalCount " +
           "FROM Hospital h JOIN h.accreditations ha " +
           "WHERE h.isActive = true " +
           "GROUP BY ha.accreditation " +
           "ORDER BY hospitalCount DESC")
    List<Object[]> getHospitalStatsByAccreditation();
    
    /**
     * Şehir ve ilçe bazlı detaylı istatistikler.
     */
    @Query("SELECT h.city as city, h.district as district, COUNT(h) as hospitalCount, " +
           "AVG(h.rating) as averageRating, AVG(h.airportDistance) as avgAirportDistance " +
           "FROM Hospital h WHERE h.isActive = true " +
           "GROUP BY h.city, h.district " +
           "ORDER BY hospitalCount DESC")
    List<Object[]> getDetailedHospitalStats();
    
    // ==================== Legacy methods (kept for backward compatibility) ====================
    
    /**
     * @deprecated Use findByCityAndIsActiveTrue(String city, Pageable pageable) instead.
     * This method loads all records into memory which is not scalable.
     */
    @Deprecated
    List<Hospital> findByCityAndIsActiveTrue(String city);
    
    /**
     * @deprecated Use findByDistrictAndIsActiveTrue(String district, Pageable pageable) instead.
     */
    @Deprecated
    List<Hospital> findByDistrictAndIsActiveTrue(String district);
    
    /**
     * @deprecated Use findAllActiveOrderByRatingDesc(Pageable pageable) instead.
     */
    @Deprecated
    @Query("SELECT h FROM Hospital h WHERE h.isActive = true ORDER BY h.rating DESC")
    List<Hospital> findAllActiveOrderByRatingDesc();
    
    /**
     * @deprecated Use findNearAirport or findTopRatedAndNearAirport instead.
     */
    @Deprecated
    @Query("SELECT h FROM Hospital h WHERE h.isActive = true AND h.airportDistance <= :maxDistance ORDER BY h.airportDistance ASC")
    List<Hospital> findByAirportDistanceLessThanEqual(@Param("maxDistance") Double maxDistance);
    
    /**
     * Basic find by ID and active status.
     * For better performance when doctor data is needed, use findByIdWithDoctors instead.
     */
    Optional<Hospital> findByIdAndIsActiveTrue(Long id);
}

