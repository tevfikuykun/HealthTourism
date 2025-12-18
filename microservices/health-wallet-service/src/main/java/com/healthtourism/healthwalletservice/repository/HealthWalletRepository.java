package com.healthtourism.healthwalletservice.repository;

import com.healthtourism.healthwalletservice.entity.HealthWallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface HealthWalletRepository extends JpaRepository<HealthWallet, Long> {
    Optional<HealthWallet> findByUserId(Long userId);
    Optional<HealthWallet> findByWalletId(String walletId);
    Optional<HealthWallet> findByQrCodeHash(String qrCodeHash);
}
