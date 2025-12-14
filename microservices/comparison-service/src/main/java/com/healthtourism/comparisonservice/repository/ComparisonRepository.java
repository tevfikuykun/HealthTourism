package com.healthtourism.comparisonservice.repository;

import com.healthtourism.comparisonservice.entity.Comparison;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ComparisonRepository extends JpaRepository<Comparison, Long> {
    List<Comparison> findByUserId(Long userId);
    List<Comparison> findByUserIdAndType(Long userId, String type);
    Optional<Comparison> findByUserIdAndTypeAndItemIds(Long userId, String type, String itemIds);
}

