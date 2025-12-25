package com.healthtourism.jpa.repository.seed;

import com.healthtourism.jpa.entity.seed.Role;
import com.healthtourism.jpa.repository.BaseRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface RoleRepository extends BaseRepository<Role> {
    Optional<Role> findByCode(String code);
    Optional<Role> findByName(String name);
    Optional<Role> findByIsDefaultTrue();
    boolean existsByCode(String code);
}

