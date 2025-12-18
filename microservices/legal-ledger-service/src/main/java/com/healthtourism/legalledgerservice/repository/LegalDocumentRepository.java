package com.healthtourism.legalledgerservice.repository;

import com.healthtourism.legalledgerservice.entity.LegalDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LegalDocumentRepository extends JpaRepository<LegalDocument, Long> {
    List<LegalDocument> findByUserIdOrderByCreatedAtDesc(Long userId);
    List<LegalDocument> findByReservationIdOrderByCreatedAtDesc(Long reservationId);
    List<LegalDocument> findByDocumentType(String documentType);
    List<LegalDocument> findByStatus(String status);
}
