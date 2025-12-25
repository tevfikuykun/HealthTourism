package com.healthtourism.jpa.repository.seed;

import com.healthtourism.jpa.entity.seed.Country;
import com.healthtourism.jpa.repository.BaseRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CountryRepository extends BaseRepository<Country> {
    Optional<Country> findByCode(String code);
    Optional<Country> findByName(String name);
    boolean existsByCode(String code);
}

