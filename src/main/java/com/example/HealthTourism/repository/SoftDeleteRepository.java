package com.example.HealthTourism.repository;

import com.example.HealthTourism.entity.BaseEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Soft Delete Repository Interface
 * Tüm repository'ler için ortak soft delete metodları
 * 
 * Özellikler:
 * - Soft delete işlemleri (fiziksel silme yerine deleted flag)
 * - Restore işlemleri (silinmiş kayıtları geri getirme)
 * - Aktif kayıtları getirme (deleted = false)
 * - Silinmiş kayıtları getirme (deleted = true)
 * 
 * Kullanım:
 * Repository'ler bu interface'i extend ederek soft delete metodlarını kazanır.
 * 
 * Örnek:
 * public interface HospitalRepository extends JpaRepository<Hospital, Long>, SoftDeleteRepository<Hospital> {
 *     // Hospital-specific queries
 * }
 * 
 * @param <T> BaseEntity'yi extend eden entity tipi
 */
@NoRepositoryBean
public interface SoftDeleteRepository<T extends BaseEntity, ID extends java.io.Serializable> 
        extends JpaRepository<T, ID>, JpaSpecificationExecutor<T> {
    
    /**
     * Tüm Aktif Kayıtları Getir
     * deleted = false olan kayıtları döner
     * 
     * @return Aktif kayıt listesi
     */
    @Query("SELECT e FROM #{#entityName} e WHERE e.deleted = false")
    List<T> findAllActive();
    
    /**
     * ID ile Aktif Kayıt Getir
     * deleted = false olan kaydı döner
     * 
     * @param id Entity ID'si
     * @return Aktif kayıt (eğer varsa)
     */
    @Query("SELECT e FROM #{#entityName} e WHERE e.id = :id AND e.deleted = false")
    Optional<T> findByIdActive(@Param("id") ID id);
    
    /**
     * Tüm Silinmiş Kayıtları Getir
     * deleted = true olan kayıtları döner
     * 
     * @return Silinmiş kayıt listesi
     */
    @Query("SELECT e FROM #{#entityName} e WHERE e.deleted = true")
    List<T> findAllDeleted();
    
    /**
     * Soft Delete İşlemi
     * Kaydı fiziksel olarak silmek yerine deleted flag'ini true yapar
     * 
     * @param id Entity ID'si
     * @param deletedBy Silen kullanıcının email'i
     * @return Silinen kayıt sayısı
     */
    @Modifying
    @Query("UPDATE #{#entityName} e SET e.deleted = true, e.deletedAt = :deletedAt, e.deletedBy = :deletedBy WHERE e.id = :id")
    int softDeleteById(@Param("id") ID id, @Param("deletedAt") LocalDateTime deletedAt, @Param("deletedBy") String deletedBy);
    
    /**
     * Restore İşlemi
     * Silinmiş bir kaydı geri getirir (soft delete'i geri alır)
     * 
     * @param id Entity ID'si
     * @return Restore edilen kayıt sayısı
     */
    @Modifying
    @Query("UPDATE #{#entityName} e SET e.deleted = false, e.deletedAt = null, e.deletedBy = null WHERE e.id = :id")
    int restoreById(@Param("id") ID id);
    
    /**
     * Tüm Kayıtları Getir (Silinmişler Dahil)
     * Hem aktif hem silinmiş kayıtları döner
     * 
     * Not: JpaRepository.findAll() zaten tüm kayıtları döner, bu metod sadece açıklama amaçlıdır.
     * Normal kullanımda findAllActive() kullanın.
     * 
     * @return Tüm kayıt listesi (hem aktif hem silinmiş)
     */
    default List<T> findAllIncludingDeleted() {
        return findAll();
    }
    
    /**
     * ID ile Kayıt Getir (Silinmişler Dahil)
     * Hem aktif hem silinmiş kayıtları döner
     * 
     * Not: JpaRepository.findById() zaten tüm kayıtları döner, bu metod sadece açıklama amaçlıdır.
     * Normal kullanımda findByIdActive() kullanın.
     * 
     * @param id Entity ID'si
     * @return Kayıt (eğer varsa, silinmiş olsa bile)
     */
    default Optional<T> findByIdIncludingDeleted(ID id) {
        return findById(id);
    }
}

