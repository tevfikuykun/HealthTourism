package com.healthtourism.multitenancyservice.repository;

import com.healthtourism.multitenancyservice.entity.Tenant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TenantRepository extends JpaRepository<Tenant, Long> {
    Tenant findBySubdomain(String subdomain);
    Optional<Tenant> findByIdAndActive(Long id, Boolean active);
}
