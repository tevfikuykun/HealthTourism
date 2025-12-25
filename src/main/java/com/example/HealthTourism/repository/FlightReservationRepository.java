package com.example.HealthTourism.repository;

import com.example.HealthTourism.entity.FlightReservation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Enterprise-grade FlightReservationRepository with PNR querying,
 * status-based filtering, JOIN FETCH optimization, and operational tracking.
 * 
 * Özellikler:
 * - PNR kodu ile hızlı erişim (operasyonel sorgu)
 * - Statü bazlı filtreleme (operasyonel yük azaltma)
 * - JOIN FETCH ile N+1 problemi çözümü
 * - Tarihsel raporlama (belirli tarihte uçacak hastalar)
 * - Finans takibi (toplam ciro)
 * - Ticket issue durumu kontrolü (askıda kalan rezervasyonlar)
 * - Pagination desteği
 */
@Repository
public interface FlightReservationRepository extends JpaRepository<FlightReservation, Long> {
    
    // ==================== PNR Sorgulama (Operasyonel Kritik) ====================
    
    /**
     * PNR kodu ile rezervasyon bulma.
     * Havayolu şirketinden gelen PNR kodu ile arama yapmak,
     * müşteri hizmetleri ve havalimanı transfer ekibi için hayati önemdedir.
     * 
     * @param pnrCode PNR (Passenger Name Record) kodu
     * @return Rezervasyon bilgisi
     */
    Optional<FlightReservation> findByPnrCode(String pnrCode);
    
    /**
     * PNR kodu ile rezervasyon detaylarını getir (JOIN FETCH ile performans).
     */
    @Query("SELECT fr FROM FlightReservation fr " +
           "JOIN FETCH fr.flightBooking " +
           "JOIN FETCH fr.user " +
           "WHERE fr.pnrCode = :pnrCode")
    Optional<FlightReservation> findByPnrCodeWithDetails(@Param("pnrCode") String pnrCode);
    
    // ==================== Performans Odaklı Detay Sorguları (N+1 Problemini Engeller) ====================
    
    /**
     * Rezervasyon numarası ile detaylı rezervasyon bilgisi (JOIN FETCH ile).
     * Uçuş ve kullanıcı bilgileri tek sorguda getirilir.
     */
    @Query("SELECT fr FROM FlightReservation fr " +
           "JOIN FETCH fr.flightBooking " +
           "JOIN FETCH fr.user " +
           "WHERE fr.reservationNumber = :resNumber")
    Optional<FlightReservation> findByReservationNumberWithDetails(@Param("resNumber") String reservationNumber);
    
    /**
     * ID ile detaylı rezervasyon bilgisi (JOIN FETCH ile).
     */
    @Query("SELECT fr FROM FlightReservation fr " +
           "JOIN FETCH fr.flightBooking " +
           "JOIN FETCH fr.user " +
           "WHERE fr.id = :id")
    Optional<FlightReservation> findByIdWithDetails(@Param("id") Long id);
    
    /**
     * Kullanıcının rezervasyonlarını detaylı getir (JOIN FETCH ile pagination).
     */
    @Query(value = "SELECT fr FROM FlightReservation fr " +
           "JOIN FETCH fr.flightBooking " +
           "JOIN FETCH fr.user " +
           "WHERE fr.user.id = :userId",
           countQuery = "SELECT COUNT(fr) FROM FlightReservation fr WHERE fr.user.id = :userId")
    Page<FlightReservation> findByUserIdWithDetails(@Param("userId") Long userId, Pageable pageable);
    
    // ==================== Statü Bazlı Filtreleme ====================
    
    /**
     * Kullanıcının belirli statüdeki rezervasyonları.
     * Örn: Sadece CONFIRMED olanlar, sadece CANCELLED olanlar.
     * Operasyonel yükü azaltır.
     */
    Page<FlightReservation> findByUserIdAndStatus(Long userId, String status, Pageable pageable);
    
    /**
     * Belirli statüdeki tüm rezervasyonlar (pagination ile).
     */
    Page<FlightReservation> findByStatus(String status, Pageable pageable);
    
    /**
     * Kullanıcının aktif rezervasyonları (CONFIRMED durumunda).
     */
    @Query("SELECT fr FROM FlightReservation fr WHERE fr.user.id = :userId " +
           "AND fr.status = 'CONFIRMED' " +
           "ORDER BY fr.createdAt DESC")
    Page<FlightReservation> findActiveReservationsByUserId(@Param("userId") Long userId, Pageable pageable);
    
    // ==================== Tarihsel Raporlama ====================
    
    /**
     * Belirli bir tarih aralığında uçacak olan aktif rezervasyonlar.
     * Havalimanı transfer ekibi ve operasyon takibi için kritik.
     * 
     * @param start Tarih aralığı başlangıcı
     * @param end Tarih aralığı bitişi
     * @return CONFIRMED statüsündeki rezervasyonlar
     */
    @Query("SELECT fr FROM FlightReservation fr " +
           "JOIN FETCH fr.flightBooking " +
           "JOIN FETCH fr.user " +
           "WHERE fr.flightBooking.departureDateTime BETWEEN :start AND :end " +
           "AND fr.status = 'CONFIRMED' " +
           "ORDER BY fr.flightBooking.departureDateTime ASC")
    List<FlightReservation> findAllActiveFlightsForDate(
            @Param("start") LocalDateTime start, 
            @Param("end") LocalDateTime end);
    
    /**
     * Belirli bir tarihte uçacak olan rezervasyonlar (tarih bazlı operasyon takibi).
     */
    @Query("SELECT fr FROM FlightReservation fr " +
           "JOIN FETCH fr.flightBooking " +
           "JOIN FETCH fr.user " +
           "WHERE DATE(fr.flightBooking.departureDateTime) = DATE(:date) " +
           "AND fr.status = 'CONFIRMED' " +
           "ORDER BY fr.flightBooking.departureDateTime ASC")
    List<FlightReservation> findTodayActiveFlights(@Param("date") LocalDateTime date);
    
    // ==================== Finans Takibi ====================
    
    /**
     * Toplam ciro hesaplama (CONFIRMED rezervasyonlar).
     * Şirket finans takibi için kullanılır.
     */
    @Query("SELECT SUM(fr.totalPrice) FROM FlightReservation fr WHERE fr.status = 'CONFIRMED'")
    BigDecimal calculateTotalRevenue();
    
    /**
     * Belirli tarih aralığında toplam ciro.
     */
    @Query("SELECT SUM(fr.totalPrice) FROM FlightReservation fr " +
           "WHERE fr.status = 'CONFIRMED' " +
           "AND fr.createdAt BETWEEN :start AND :end")
    BigDecimal calculateTotalRevenueForPeriod(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);
    
    /**
     * Statü bazlı toplam ciro.
     */
    @Query("SELECT SUM(fr.totalPrice) FROM FlightReservation fr WHERE fr.status = :status")
    BigDecimal calculateTotalRevenueByStatus(@Param("status") String status);
    
    /**
     * Rezervasyon istatistikleri (statü bazlı sayım).
     */
    @Query("SELECT fr.status, COUNT(fr) FROM FlightReservation fr GROUP BY fr.status")
    List<Object[]> getReservationStatistics();
    
    // ==================== Ticket Issue Durumu Kontrolü (Operasyonel) ====================
    
    /**
     * Askıda kalan rezervasyonları bulur.
     * Belirli bir süreden fazla PENDING durumunda kalan ve iptal edilmesi gereken rezervasyonlar.
     * 
     * Örnek kullanım: 2 saatten fazla PENDING durumda kalan rezervasyonları bulmak için
     * findByStatusAndCreatedAtBefore("PENDING", LocalDateTime.now().minusHours(2))
     * 
     * @param status Rezervasyon statüsü (genellikle "PENDING")
     * @param threshold Eşik zamanı (bu tarihten önce oluşturulmuş olanlar)
     * @return Askıda kalan rezervasyonlar
     */
    @Query("SELECT fr FROM FlightReservation fr " +
           "WHERE fr.status = :status " +
           "AND fr.createdAt < :threshold " +
           "ORDER BY fr.createdAt ASC")
    List<FlightReservation> findByStatusAndCreatedAtBefore(
            @Param("status") String status,
            @Param("threshold") LocalDateTime threshold);
    
    /**
     * Ödeme alınmış ancak henüz biletlenmemiş rezervasyonlar.
     * Sağlık turizminde rezervasyon yapılmış olması, biletin kesildiği anlamına gelmeyebilir.
     */
    @Query("SELECT fr FROM FlightReservation fr " +
           "WHERE fr.status = 'PENDING' " +
           "AND fr.createdAt < :threshold " +
           "ORDER BY fr.createdAt ASC")
    List<FlightReservation> findPendingReservationsOlderThan(@Param("threshold") LocalDateTime threshold);
    
    /**
     * Onay bekleyen rezervasyonlar (operasyon ekibi için).
     */
    @Query("SELECT fr FROM FlightReservation fr " +
           "JOIN FETCH fr.flightBooking " +
           "WHERE fr.status = 'PENDING' " +
           "ORDER BY fr.createdAt ASC")
    List<FlightReservation> findAllPendingReservations();
    
    // ==================== Pagination Desteği ====================
    
    /**
     * Kullanıcının tüm rezervasyonları (pagination ile).
     */
    Page<FlightReservation> findByUserId(Long userId, Pageable pageable);
    
    /**
     * Tarih aralığında oluşturulmuş rezervasyonlar (pagination ile).
     */
    @Query("SELECT fr FROM FlightReservation fr " +
           "WHERE fr.createdAt BETWEEN :start AND :end " +
           "ORDER BY fr.createdAt DESC")
    Page<FlightReservation> findByCreatedAtBetween(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end,
            Pageable pageable);
    
    /**
     * Uçuş rezervasyonuna ait tüm rezervasyonlar.
     */
    Page<FlightReservation> findByFlightBookingId(Long flightBookingId, Pageable pageable);
    
    // ==================== Legacy methods (kept for backward compatibility) ====================
    
    /**
     * @deprecated Use findByUserId(Long userId, Pageable pageable) instead.
     * This method loads all records into memory which is not scalable.
     */
    @Deprecated
    List<FlightReservation> findByUserId(Long userId);
    
    /**
     * Basic find by reservation number (without JOIN FETCH).
     * For better performance, use findByReservationNumberWithDetails instead.
     */
    Optional<FlightReservation> findByReservationNumber(String reservationNumber);
}

