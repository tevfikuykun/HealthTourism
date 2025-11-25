package com.healthtourism.translationservice.repository;

import com.healthtourism.translationservice.entity.TranslationServiceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TranslationServiceRepository extends JpaRepository<TranslationServiceEntity, Long> {
    List<TranslationServiceEntity> findByIsAvailableTrue();
    List<TranslationServiceEntity> findByServiceTypeAndIsAvailableTrue(String serviceType);
    List<TranslationServiceEntity> findByIsCertifiedTrueAndIsAvailableTrue();
    List<TranslationServiceEntity> findByIsAvailableForHospitalTrueAndIsAvailableTrue();
    List<TranslationServiceEntity> findByIsAvailableForConsultationTrueAndIsAvailableTrue();
    @Query("SELECT t FROM TranslationServiceEntity t WHERE t.isAvailable = true AND t.languages LIKE %:language%")
    List<TranslationServiceEntity> findByLanguageContaining(@Param("language") String language);
    @Query("SELECT t FROM TranslationServiceEntity t WHERE t.isAvailable = true ORDER BY t.rating DESC")
    List<TranslationServiceEntity> findAllActiveOrderByRatingDesc();
    Optional<TranslationServiceEntity> findByIdAndIsAvailableTrue(Long id);
}

