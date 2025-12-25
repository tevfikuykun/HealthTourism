package com.example.HealthTourism.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * User Entity - Enterprise Implementation
 * 
 * Güvenlik Notu: "Soft Delete"
 * Sağlık turizmi sistemlerinde bir kullanıcıyı (veya doktoru) sistemden tamamen silmek (DELETE)
 * veri bütünlüğünü bozar (çünkü o kullanıcıya ait tıbbi kayıtlar ve ödemeler vardır).
 * Bu yüzden isActive alanı çok değerli. Silme işlemlerini her zaman bu alanı false çekerek yapmalısın.
 */
@Entity
@Table(name = "users", indexes = {
    @Index(name = "idx_user_email", columnList = "email", unique = true),
    @Index(name = "idx_user_phone", columnList = "phone"),
    @Index(name = "idx_user_role", columnList = "role"),
    @Index(name = "idx_user_is_active", columnList = "is_active"),
    @Index(name = "idx_user_country", columnList = "country"),
    @Index(name = "idx_user_last_login", columnList = "last_login_date"),
    // Composite indexes for common query patterns
    @Index(name = "idx_role_active", columnList = "role,is_active"),
    @Index(name = "idx_email_active", columnList = "email,is_active")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(nullable = false)
    private String phone;
    
    /**
     * Telefon numarası (phone ile aynı alan, alternatif sorgulama için).
     * Sosyal medya veya telefon ile giriş senaryosu için kullanılabilir.
     */
    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(nullable = false)
    private String country; // Ülke

    @Column(nullable = false)
    private String role; // USER, ADMIN, DOCTOR
    
    /**
     * Not: Gelecekte çoklu rol desteği için Role entity'si oluşturup
     * @ManyToMany ilişki kurulabilir. Bu durumda JOIN FETCH ile
     * rolleri tek sorguda çekebilirsin: JOIN FETCH u.roles
     */

    @Column(nullable = false)
    private Boolean isActive;
    
    /**
     * Hesap kilit durumu (Spring Security için).
     * Güvenlik ihlali durumunda hesabı kilitlemek için kullanılır.
     */
    @Column(nullable = false)
    private Boolean accountNonLocked = true;
    
    /**
     * Hesap süresi dolmuş mu (Spring Security için).
     */
    @Column(nullable = false)
    private Boolean accountNonExpired = true;
    
    /**
     * Şifre süresi dolmuş mu (Spring Security için).
     */
    @Column(nullable = false)
    private Boolean credentialsNonExpired = true;
    
    /**
     * Email doğrulandı mı.
     */
    @Column(nullable = false)
    private Boolean emailVerified = false;

    /**
     * Son giriş tarihi (Audit & Tracking için).
     * Güvenlik ve kullanıcı aktivite takibi için kritik.
     */
    @Column(name = "last_login_date")
    private LocalDateTime lastLoginDate;
    
    /**
     * Hesap oluşturulma tarihi (Audit için).
     */
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    /**
     * Son güncelleme tarihi (Audit için).
     */
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Reservation> reservations;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Review> reviews;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (isActive == null) {
            isActive = true;
        }
        if (accountNonLocked == null) {
            accountNonLocked = true;
        }
        if (accountNonExpired == null) {
            accountNonExpired = true;
        }
        if (credentialsNonExpired == null) {
            credentialsNonExpired = true;
        }
        if (emailVerified == null) {
            emailVerified = false;
        }
        // phoneNumber phone ile aynıysa otomatik doldur
        if (phoneNumber == null && phone != null) {
            phoneNumber = phone;
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}

