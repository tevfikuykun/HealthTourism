package com.healthtourism.virtualtourservice.repository;

import com.healthtourism.virtualtourservice.entity.VirtualTour;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VirtualTourRepository extends JpaRepository<VirtualTour, Long> {
    List<VirtualTour> findByTourTypeAndStatus(String tourType, String status);
    List<VirtualTour> findByEntityIdAndTourType(Long entityId, String tourType);
    Optional<VirtualTour> findByEntityIdAndTourTypeAndStatus(Long entityId, String tourType, String status);
    List<VirtualTour> findByStatusOrderByViewCountDesc(String status);
    List<VirtualTour> findByStatusOrderByAverageRatingDesc(String status);
}
