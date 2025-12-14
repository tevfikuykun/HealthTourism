package com.healthtourism.posttreatmentservice.repository;
import com.healthtourism.posttreatmentservice.entity.CarePackage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CarePackageRepository extends JpaRepository<CarePackage, Long> {
    List<CarePackage> findByUserId(Long userId);
    List<CarePackage> findByReservationId(Long reservationId);
}

