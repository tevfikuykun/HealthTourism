package com.healthtourism.userservice.repository;

import com.healthtourism.userservice.entity.Role;
import com.healthtourism.userservice.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * User Repository
 * 
 * Professional repository with:
 * - Pagination support
 * - Soft delete filtering (isDeleted = false) via @Where annotation on Entity
 * - Custom queries for business needs
 * - Role-based queries
 * 
 * Note: @Where annotation on User entity automatically filters deleted users,
 * so all queries implicitly exclude deleted users.
 */
@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    
    /**
     * Find user by email (only if active and not deleted)
     */
    Optional<User> findByEmail(String email);
    
    /**
     * Check if user exists by email (only if active and not deleted)
     */
    boolean existsByEmail(String email);
    
    /**
     * Find user by email ignoring deleted flag (for admin purposes)
     * Use with caution - only for admin operations
     */
    @Query("SELECT u FROM User u WHERE u.email = :email")
    Optional<User> findByEmailIncludingDeleted(@Param("email") String email);
    
    /**
     * Find users by role with pagination
     */
    @Query("SELECT DISTINCT u FROM User u JOIN u.roles r WHERE r = :role")
    Page<User> findByRole(@Param("role") Role role, Pageable pageable);
    
    /**
     * Find users by role (without pagination)
     */
    @Query("SELECT DISTINCT u FROM User u JOIN u.roles r WHERE r = :role")
    List<User> findByRole(@Param("role") Role role);
    
    /**
     * Find active users with pagination
     */
    Page<User> findByIsActiveTrue(Pageable pageable);
    
    /**
     * Find users by country with pagination
     */
    Page<User> findByCountry(String country, Pageable pageable);
    
    /**
     * Find users by country (without pagination)
     */
    List<User> findByCountry(String country);
    
    /**
     * Find users by language preference
     */
    List<User> findByLanguagePreference(com.healthtourism.userservice.entity.Language language);
    
    /**
     * Find users with unverified email
     */
    @Query("SELECT u FROM User u WHERE u.emailVerified = false AND u.verificationTokenExpiry > CURRENT_TIMESTAMP")
    List<User> findUnverifiedUsers();
    
    /**
     * Find users by verification token
     */
    Optional<User> findByVerificationToken(String verificationToken);
    
    /**
     * Count active users
     */
    long countByIsActiveTrue();
    
    /**
     * Count users by role
     */
    @Query("SELECT COUNT(DISTINCT u) FROM User u JOIN u.roles r WHERE r = :role")
    long countByRole(@Param("role") Role role);
    
    /**
     * Find users with roles (for admin dashboard)
     */
    @Query("SELECT u FROM User u JOIN FETCH u.roles WHERE u.id = :id")
    Optional<User> findByIdWithRoles(@Param("id") UUID id);
    
    /**
     * Find all users with roles (for admin dashboard)
     */
    @Query("SELECT DISTINCT u FROM User u JOIN FETCH u.roles")
    List<User> findAllWithRoles();
}
