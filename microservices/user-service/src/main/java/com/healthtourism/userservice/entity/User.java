package com.healthtourism.userservice.entity;

import com.healthtourism.jpa.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * User Entity - Professional Enterprise Implementation
 * 
 * Best Practices Applied:
 * - Extends BaseEntity for audit fields (createdAt, updatedAt, createdBy, updatedBy, UUID)
 * - Implements UserDetails for Spring Security integration
 * - Role management with Set<Role> for multiple roles support
 * - Soft delete (@SQLDelete, @Where) for data preservation
 * - Healthcare-specific fields (passport, birthDate, gender, language)
 * - Database indexes for performance
 * - GDPR/KVKK compliance considerations
 * 
 * Security:
 * - Password is hashed with BCrypt (handled in service layer)
 * - Sensitive data (passport number) should be encrypted
 * - Soft delete prevents data loss
 */
@Entity
@Table(name = "users", indexes = {
    @Index(name = "idx_user_email", columnList = "email", unique = true),
    @Index(name = "idx_user_phone", columnList = "phone"),
    @Index(name = "idx_user_is_active", columnList = "is_active"),
    @Index(name = "idx_user_is_deleted", columnList = "is_deleted"),
    @Index(name = "idx_user_email_active", columnList = "email, is_active"),
    @Index(name = "idx_user_country", columnList = "country")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
@SQLDelete(sql = "UPDATE users SET is_deleted = true, is_active = false, updated_at = CURRENT_TIMESTAMP WHERE id = ?")
@Where(clause = "is_deleted = false")
@ToString(exclude = {"password", "roles"}) // Exclude sensitive data from toString
public class User extends BaseEntity implements UserDetails {
    
    /**
     * Email address (unique identifier, used as username)
     */
    @Column(nullable = false, unique = true, length = 255)
    private String email;
    
    /**
     * Password (hashed with BCrypt - never store plain text)
     * This field should always contain a BCrypt hash, never plain text
     */
    @Column(nullable = false, length = 255)
    private String password;
    
    /**
     * First name
     */
    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;
    
    /**
     * Last name
     */
    @Column(name = "last_name", nullable = false, length = 100)
    private String lastName;
    
    /**
     * Phone number (international format recommended)
     */
    @Column(nullable = false, length = 50)
    private String phone;
    
    /**
     * Country code (ISO 3166-1 alpha-2, e.g., "TR", "US")
     */
    @Column(length = 2)
    private String country;
    
    /**
     * Date of birth (required for medical procedures)
     */
    @Column(name = "birth_date")
    private LocalDate birthDate;
    
    /**
     * Gender (important for medical procedures)
     */
    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private Gender gender;
    
    /**
     * Passport number (sensitive data - should be encrypted at rest)
     * Required for international healthcare tourism
     */
    @Column(name = "passport_number", length = 50)
    private String passportNumber;
    
    /**
     * User roles (ElementCollection for multiple roles support)
     * Allows users to have multiple roles (e.g., both DOCTOR and ADMIN)
     * Stored in user_roles join table
     */
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 50)
    @Builder.Default
    private Set<Role> roles = new HashSet<>();
    
    /**
     * Account status (active/inactive)
     */
    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;
    
    /**
     * Soft delete flag (managed by @SQLDelete and @Where)
     */
    @Column(name = "is_deleted", nullable = false)
    @Builder.Default
    private Boolean isDeleted = false;
    
    /**
     * Email verification status
     */
    @Column(name = "email_verified", nullable = false)
    @Builder.Default
    private Boolean emailVerified = false;
    
    /**
     * Email verification token
     */
    @Column(name = "verification_token", length = 255)
    private String verificationToken;
    
    /**
     * Email verification token expiry
     */
    @Column(name = "verification_token_expiry")
    private LocalDateTime verificationTokenExpiry;
    
    /**
     * Language preference for notifications and UI
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "language_preference", length = 10)
    @Builder.Default
    private Language languagePreference = Language.getDefault();
    
    /**
     * Last login timestamp
     */
    @Column(name = "last_login")
    private LocalDateTime lastLogin;
    
    /**
     * Profile image URL
     */
    @Column(name = "profile_image_url", length = 500)
    private String profileImageUrl;
    
    /**
     * Additional notes/remarks
     */
    @Column(columnDefinition = "TEXT")
    private String notes;
    
    // ==========================================
    // Spring Security UserDetails Implementation
    // ==========================================
    
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Convert roles to Spring Security authorities
        if (roles == null || roles.isEmpty()) {
            // Default role if no roles assigned
            return Set.of(new SimpleGrantedAuthority(Role.ROLE_USER.name()));
        }
        
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.name()))
                .collect(Collectors.toSet());
    }
    
    @Override
    public String getUsername() {
        return email; // Use email as username
    }
    
    @Override
    public boolean isAccountNonExpired() {
        return true; // Accounts don't expire
    }
    
    @Override
    public boolean isAccountNonLocked() {
        return isActive != null && isActive; // Account is locked if inactive
    }
    
    @Override
    public boolean isCredentialsNonExpired() {
        return true; // Credentials don't expire
    }
    
    @Override
    public boolean isEnabled() {
        return isActive != null && isActive && (isDeleted == null || !isDeleted);
    }
    
    // ==========================================
    // Business Logic Methods
    // ==========================================
    
    /**
     * Check if user has a specific role
     */
    public boolean hasRole(Role role) {
        return roles != null && roles.contains(role);
    }
    
    /**
     * Check if user has any of the specified roles
     */
    public boolean hasAnyRole(Role... roles) {
        if (this.roles == null || roles == null) {
            return false;
        }
        for (Role role : roles) {
            if (this.roles.contains(role)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Check if user is admin
     */
    public boolean isAdmin() {
        return hasRole(Role.ROLE_ADMIN);
    }
    
    /**
     * Check if user is doctor
     */
    public boolean isDoctor() {
        return hasRole(Role.ROLE_DOCTOR);
    }
    
    /**
     * Add role to user
     */
    public void addRole(Role role) {
        if (roles == null) {
            roles = new HashSet<>();
        }
        roles.add(role);
    }
    
    /**
     * Remove role from user
     */
    public void removeRole(Role role) {
        if (roles != null) {
            roles.remove(role);
        }
    }
    
    /**
     * Get full name (first name + last name)
     */
    public String getFullName() {
        return (firstName != null ? firstName : "") + " " + (lastName != null ? lastName : "");
    }
    
    /**
     * Calculate age from birth date
     */
    public Integer getAge() {
        if (birthDate == null) {
            return null;
        }
        return LocalDate.now().getYear() - birthDate.getYear();
    }
    
    /**
     * Soft delete user
     */
    public void softDelete() {
        this.isDeleted = true;
        this.isActive = false;
    }
    
    /**
     * Restore soft-deleted user
     */
    public void restore() {
        this.isDeleted = false;
        this.isActive = true;
    }
    
    /**
     * Verify email
     */
    public void verifyEmail() {
        this.emailVerified = true;
        this.verificationToken = null;
        this.verificationTokenExpiry = null;
    }
    
    /**
     * Pre-persist callback
     */
    @PrePersist
    protected void onCreate() {
        if (isActive == null) {
            isActive = true;
        }
        if (isDeleted == null) {
            isDeleted = false;
        }
        if (emailVerified == null) {
            emailVerified = false;
        }
        if (languagePreference == null) {
            languagePreference = Language.getDefault();
        }
        if (roles == null || roles.isEmpty()) {
            roles = new HashSet<>();
            roles.add(Role.ROLE_USER); // Default role
        }
    }
    
    /**
     * Pre-update callback
     */
    @PreUpdate
    protected void onUpdate() {
        // Ensure roles are never null
        if (roles == null) {
            roles = new HashSet<>();
        }
    }
}
