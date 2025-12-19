package com.healthtourism.migration.repository;

import com.healthtourism.migration.model.EntityVersion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EntityVersionRepository extends JpaRepository<EntityVersion, Long> {
    EntityVersion findByEntityName(String entityName);
}

