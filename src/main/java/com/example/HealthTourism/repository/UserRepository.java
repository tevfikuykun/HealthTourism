package com.example.HealthTourism.repository;

import com.example.HealthTourism.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Enterprise-grade UserRepository with RBAC support, audit & tracking,
 * security performance optimization, and soft delete handling.
 * 
 * Özellikler:
 * - Rol yönetimi (RBAC) - JOIN FETCH ile roller (gelecekte Role entity'si eklendiğinde)
 * - Audit & Tracking (son giriş tarihi, hesap kilit durumu)
 * - Email üzerinde UNIQUE INDEX (performans için kritik)
 * - Hesap güvenliği sorguları (Spring Security entegrasyonu için)
 * - Soft delete desteği (isActive ile)
 * 
 * Güvenlik Notu: "Soft Delete"
 * Sağlık turizmi sistemlerinde bir kullanıcıyı tamamen silmek (DELETE) veri bütünlüğünü bozar.
 * Silme işlemlerini her zaman isActive = false çekerek yapmalısın.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    // ==================== Güvenlik Performansı: Email Bazlı Sorgular ====================
    
    /**
     * Email ile kullanıcı bulma (temel sorgu).
     * Email alanı üzerinde UNIQUE INDEX olduğundan emin olunmalıdır,
     * çünkü tüm sistem bu alan üzerinden döner.
     */
    Optional<User> findByEmail(String email);
    
    /**
     * Email ile aktif kullanıcı bulma.
     */
    Optional<User> findByEmailAndIsActiveTrue(String email);
    
    /**
     * Email varlık kontrolü (kayıt öncesi kontrol için).
     */
    Boolean existsByEmail(String email);
    
    /**
     * Güvenlik Performansı: Kullanıcıyı rolleriyle birlikte tek sorguda getir.
     * 
     * Not: Şu anda role String alanı. Gelecekte Role entity'si oluşturup
     * @ManyToMany ilişki kurulursa, sorgu şu şekilde olmalıdır:
     * SELECT u FROM User u LEFT JOIN FETCH u.roles WHERE u.email = :email
     * 
     * Bu sayede Spring Security UserDetailsService içinde roller
     * tek sorguda yüklenir ve performans artar.
     */
    @Query("SELECT u FROM User u WHERE u.email = :email AND u.isActive = true")
    Optional<User> findByEmailWithRoles(@Param("email") String email);
    
    // ==================== Sosyal Medya veya Telefon ile Giriş Senaryosu ====================
    
    /**
     * Telefon numarası ile kullanıcı bulma (sosyal medya veya telefon ile giriş için).
     * Genişletilebilirlik için kritik.
     */
    Optional<User> findByPhoneNumber(String phoneNumber);
    
    /**
     * Telefon numarası ile aktif kullanıcı bulma.
     */
    Optional<User> findByPhoneNumberAndIsActiveTrue(String phoneNumber);
    
    /**
     * Telefon veya email ile kullanıcı bulma (alternatif giriş yöntemleri için).
     */
    @Query("SELECT u FROM User u WHERE " +
           "(u.email = :identifier OR u.phoneNumber = :identifier) " +
           "AND u.isActive = true")
    Optional<User> findByEmailOrPhoneNumber(@Param("identifier") String identifier);
    
    // ==================== Hesap Güvenliği Sorguları (Spring Security) ====================
    
    /**
     * Hesap güvenliği: Kilitli olmayan aktif kullanıcılar.
     * JWT (JSON Web Token) tabanlı bir güvenlik mimarisi için kritik.
     * 
     * Giriş işlemi sırasında sadece aktif, kilitlenmemiş ve süresi dolmamış
     * hesaplara erişim izni verilmelidir.
     */
    @Query("SELECT u FROM User u WHERE u.email = :email " +
           "AND u.isActive = true " +
           "AND u.accountNonLocked = true " +
           "AND u.accountNonExpired = true " +
           "AND u.credentialsNonExpired = true")
    Optional<User> findValidUserForLogin(@Param("email") String email);
    
    /**
     * Email doğrulanmış aktif kullanıcılar.
     */
    @Query("SELECT u FROM User u WHERE u.email = :email " +
           "AND u.isActive = true " +
           "AND u.emailVerified = true " +
           "AND u.accountNonLocked = true")
    Optional<User> findVerifiedUserForLogin(@Param("email") String email);
    
    // ==================== Admin Paneli: Rol Bazlı Kullanıcı Listeleme ====================
    
    /**
     * Rol bazlı kullanıcı listeleme (RBAC).
     * Örn: Tüm doktorları getir, tüm adminleri getir.
     * 
     * Not: Şu anda role String. Gelecekte Role entity'si oluşturulursa:
     * SELECT u FROM User u JOIN u.roles r WHERE r.name = :roleName
     */
    @Query("SELECT u FROM User u WHERE u.role = :roleName AND u.isActive = true")
    Page<User> findByRoleName(@Param("roleName") String roleName, Pageable pageable);
    
    /**
     * Tüm doktorları getir.
     */
    @Query("SELECT u FROM User u WHERE u.role = 'DOCTOR' AND u.isActive = true " +
           "ORDER BY u.lastName ASC, u.firstName ASC")
    Page<User> findAllDoctors(Pageable pageable);
    
    /**
     * Tüm adminleri getir.
     */
    @Query("SELECT u FROM User u WHERE u.role = 'ADMIN' AND u.isActive = true " +
           "ORDER BY u.lastName ASC, u.firstName ASC")
    Page<User> findAllAdmins(Pageable pageable);
    
    /**
     * Tüm hastaları getir (USER rolü).
     */
    @Query("SELECT u FROM User u WHERE u.role = 'USER' AND u.isActive = true " +
           "ORDER BY u.lastName ASC, u.firstName ASC")
    Page<User> findAllPatients(Pageable pageable);
    
    // ==================== Audit & Tracking Sorguları ====================
    
    /**
     * Son giriş tarihine göre aktif kullanıcılar.
     * Kullanıcı aktivite takibi için kritik.
     */
    @Query("SELECT u FROM User u WHERE u.isActive = true " +
           "AND u.lastLoginDate IS NOT NULL " +
           "ORDER BY u.lastLoginDate DESC")
    Page<User> findRecentlyActiveUsers(Pageable pageable);
    
    /**
     * Belirli tarihten sonra giriş yapmış kullanıcılar.
     */
    @Query("SELECT u FROM User u WHERE u.isActive = true " +
           "AND u.lastLoginDate >= :since " +
           "ORDER BY u.lastLoginDate DESC")
    List<User> findUsersLoggedInSince(@Param("since") LocalDateTime since);
    
    /**
     * Hiç giriş yapmamış kullanıcılar (yeni kayıtlar).
     */
    @Query("SELECT u FROM User u WHERE u.isActive = true " +
           "AND u.lastLoginDate IS NULL")
    List<User> findUsersNeverLoggedIn();
    
    /**
     * Kilitli hesaplar (hesap güvenliği için).
     */
    @Query("SELECT u FROM User u WHERE u.accountNonLocked = false " +
           "AND u.isActive = true " +
           "ORDER BY u.updatedAt DESC")
    Page<User> findLockedAccounts(Pageable pageable);
    
    /**
     * Email doğrulanmamış hesaplar.
     */
    @Query("SELECT u FROM User u WHERE u.emailVerified = false " +
           "AND u.isActive = true " +
           "ORDER BY u.createdAt DESC")
    Page<User> findUnverifiedAccounts(Pageable pageable);
    
    // ==================== Ülke ve Bölge Bazlı Sorgular ====================
    
    /**
     * Ülke bazlı kullanıcı listeleme.
     * Sağlık turizmi için kritik - hangi ülkelerden hasta geldiğini görmek için.
     */
    Page<User> findByCountryAndIsActiveTrue(String country, Pageable pageable);
    
    /**
     * Ülke bazlı aktif kullanıcı sayısı.
     */
    long countByCountryAndIsActiveTrue(String country);
    
    // ==================== Pagination Desteği ====================
    
    /**
     * Tüm aktif kullanıcılar (pagination ile).
     */
    Page<User> findByIsActiveTrue(Pageable pageable);
    
    /**
     * Tüm kullanıcılar (pagination ile, aktif/pasif fark etmeksizin).
     */
    Page<User> findAll(Pageable pageable);
    
    // ==================== İstatistik Sorguları ====================
    
    /**
     * Aktif kullanıcı sayısı.
     */
    long countByIsActiveTrue();
    
    /**
     * Belirli rol için aktif kullanıcı sayısı.
     */
    long countByRoleAndIsActiveTrue(String role);
    
    /**
     * Email doğrulanmış kullanıcı sayısı.
     */
    long countByEmailVerifiedTrueAndIsActiveTrue();
    
    /**
     * Belirli tarih aralığında oluşturulmuş kullanıcı sayısı.
     */
    @Query("SELECT COUNT(u) FROM User u WHERE u.createdAt BETWEEN :start AND :end")
    long countByCreatedAtBetween(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);
    
    // ==================== Arama ve Filtreleme ====================
    
    /**
     * İsim ve soyisme göre arama.
     */
    @Query("SELECT u FROM User u WHERE u.isActive = true " +
           "AND (LOWER(u.firstName) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(u.lastName) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(CONCAT(u.firstName, ' ', u.lastName)) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<User> searchByName(@Param("keyword") String keyword, Pageable pageable);
    
    /**
     * Email, isim veya telefon ile arama.
     */
    @Query("SELECT u FROM User u WHERE u.isActive = true " +
           "AND (LOWER(u.email) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(u.firstName) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(u.lastName) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR u.phone LIKE CONCAT('%', :keyword, '%'))")
    Page<User> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);
}
