package com.healthtourism.jpa.repository.seed;

import com.healthtourism.jpa.entity.seed.TreatmentBranch;
import com.healthtourism.jpa.repository.BaseRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TreatmentBranchRepository extends BaseRepository<TreatmentBranch> {
    Optional<TreatmentBranch> findByCode(String code);
    Optional<TreatmentBranch> findByName(String name);
    List<TreatmentBranch> findByIsActiveTrueOrderByDisplayOrderAsc();
    List<TreatmentBranch> findByCategory(String category);
    boolean existsByCode(String code);
}

