package com.healthtourism.medicaldocumentservice.repository;
import com.healthtourism.medicaldocumentservice.entity.MedicalDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MedicalDocumentRepository extends JpaRepository<MedicalDocument, Long> {
    List<MedicalDocument> findByUserId(Long userId);
    List<MedicalDocument> findByDoctorId(Long doctorId);
    List<MedicalDocument> findByReservationId(Long reservationId);
    List<MedicalDocument> findByDocumentTypeAndIsActiveTrue(String documentType);
    @Query("SELECT m FROM MedicalDocument m WHERE m.userId = :userId AND m.isActive = true ORDER BY m.uploadedAt DESC")
    List<MedicalDocument> findByUserIdOrderByUploadedAtDesc(@Param("userId") Long userId);
}

