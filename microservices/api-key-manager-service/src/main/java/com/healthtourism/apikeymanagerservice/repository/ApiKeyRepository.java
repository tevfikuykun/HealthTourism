package com.healthtourism.apikeymanagerservice.repository;

import com.healthtourism.apikeymanagerservice.entity.ApiKey;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ApiKeyRepository extends JpaRepository<ApiKey, Long> {
    ApiKey findByKeyValue(String keyValue);
    List<ApiKey> findByOrganization(String organization);
    List<ApiKey> findByActive(Boolean active);
    Optional<ApiKey> findByIdAndActive(Long id, Boolean active);
}
