package com.healthtourism.medicaldocumentservice.repository;
import com.healthtourism.medicaldocumentservice.entity.MedicalDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MedicalDocumentRepository extends MongoRepository<MedicalDocument, String> {
    List<MedicalDocument> findByUserId(Long userId);
    List<MedicalDocument> findByDoctorId(Long doctorId);
    List<MedicalDocument> findByReservationId(Long reservationId);
    List<MedicalDocument> findByDocumentTypeAndIsActiveTrue(String documentType);
    List<MedicalDocument> findByUserIdAndIsActiveTrueOrderByUploadedAtDesc(Long userId);
}

