package com.healthtourism.authservice.repository;

import com.healthtourism.authservice.entity.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Password Reset Token Repository
 */
@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    
    /**
     * Find reset token by token string (only if not used)
     */
    Optional<PasswordResetToken> findByTokenAndIsUsedFalse(String token);
    
    /**
     * Find reset token by token string (including used tokens)
     */
    Optional<PasswordResetToken> findByToken(String token);
    
    /**
     * Delete all reset tokens for a user
     * Used when generating a new reset token (invalidate old ones)
     */
    void deleteByUserId(Long userId);
    
    /**
     * Find reset token by user ID (only if not used and not expired)
     */
    Optional<PasswordResetToken> findByUserIdAndIsUsedFalse(Long userId);
}
