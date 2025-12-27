package com.example.HealthTourism.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * Base Entity
 * Tüm entity'ler için ortak audit ve soft delete field'ları
 * 
 * Audit Fields:
 * - createdDate: Oluşturulma tarihi (otomatik)
 * - createdBy: Oluşturan kullanıcı email'i (Spring Security context'inden)
 * - lastModifiedDate: Son güncelleme tarihi (otomatik)
 * - lastModifiedBy: Son güncelleyen kullanıcı email'i (Spring Security context'inden)
 * 
 * Soft Delete:
 * - deleted: Silinme durumu (false = aktif, true = silinmiş)
 * - deletedAt: Silinme tarihi (null = silinmemiş)
 * - deletedBy: Silen kullanıcı email'i (null = silinmemiş)
 * 
 * Kullanım:
 * Entity'ler bu sınıfı extend ederek audit ve soft delete özelliklerini kazanır.
 * 
 * Örnek:
 * @Entity
 * public class Hospital extends BaseEntity {
 *     // Hospital-specific fields
 * }
 */
@Data
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity {
    
    /**
     * Oluşturulma Tarihi
     * Entity ilk kaydedildiğinde otomatik olarak doldurulur
     */
    @CreatedDate
    @Column(name = "created_date", nullable = false, updatable = false)
    private LocalDateTime createdDate;
    
    /**
     * Oluşturan Kullanıcı
     * Spring Security context'inden otomatik olarak doldurulur
     * JWT token'dan gelen email bilgisi kullanılır
     */
    @CreatedBy
    @Column(name = "created_by", nullable = false, updatable = false, length = 255)
    private String createdBy;
    
    /**
     * Son Güncelleme Tarihi
     * Entity her güncellendiğinde otomatik olarak güncellenir
     */
    @LastModifiedDate
    @Column(name = "last_modified_date")
    private LocalDateTime lastModifiedDate;
    
    /**
     * Son Güncelleyen Kullanıcı
     * Spring Security context'inden otomatik olarak doldurulur
     * JWT token'dan gelen email bilgisi kullanılır
     */
    @LastModifiedBy
    @Column(name = "last_modified_by", length = 255)
    private String lastModifiedBy;
    
    /**
     * Soft Delete Flag
     * false = Aktif kayıt
     * true = Silinmiş kayıt (veritabanından fiziksel olarak silinmez)
     * 
     * Güvenlik Notu: Sağlık turizmi sistemlerinde verilerin fiziksel olarak
     * silinmesi veri bütünlüğünü bozar (tıbbi kayıtlar, ödemeler, vb.).
     * Bu yüzden soft delete kullanılmalıdır.
     */
    @Column(name = "deleted", nullable = false)
    private Boolean deleted = false;
    
    /**
     * Silinme Tarihi
     * Kayıt silindiğinde (soft delete) otomatik olarak doldurulur
     * null = Aktif kayıt
     */
    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;
    
    /**
     * Silen Kullanıcı
     * Soft delete işlemini yapan kullanıcının email'i
     * null = Aktif kayıt
     */
    @Column(name = "deleted_by", length = 255)
    private String deletedBy;
    
    /**
     * Soft Delete İşlemi
     * Kaydı fiziksel olarak silmek yerine deleted flag'ini true yapar
     * 
     * @param deletedBy Silen kullanıcının email'i
     */
    public void softDelete(String deletedBy) {
        this.deleted = true;
        this.deletedAt = LocalDateTime.now();
        this.deletedBy = deletedBy;
    }
    
    /**
     * Restore İşlemi
     * Silinmiş bir kaydı geri getirir (soft delete'i geri alır)
     */
    public void restore() {
        this.deleted = false;
        this.deletedAt = null;
        this.deletedBy = null;
    }
    
    /**
     * Kaydın Aktif Olup Olmadığını Kontrol Eder
     * 
     * @return true eğer kayıt aktifse (deleted = false)
     */
    public boolean isActive() {
        return !Boolean.TRUE.equals(deleted);
    }
}

