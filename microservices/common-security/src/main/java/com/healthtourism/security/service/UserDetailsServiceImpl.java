package com.healthtourism.security.service;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * User Details Service Implementation
 * 
 * Loads user information for Spring Security authentication.
 * 
 * In production, this should:
 * - Load user from database
 * - Load user roles/permissions
 * - Handle user not found scenarios
 * 
 * This is a placeholder implementation.
 * Actual implementation should be in auth-service or user-service.
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    
    /**
     * Load user by username
     * 
     * @param username Username
     * @return UserDetails with username, password, and authorities
     * @throws UsernameNotFoundException if user not found
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // TODO: Implement actual user loading from database
        // Example:
        // UserEntity user = userRepository.findByUsername(username)
        //     .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
        // 
        // return User.builder()
        //     .username(user.getUsername())
        //     .password(user.getPassword()) // Already hashed with BCrypt
        //     .roles(user.getRoles().toArray(new String[0]))
        //     .build();
        
        // Placeholder implementation
        // In production, replace with actual database lookup
        if ("admin".equals(username)) {
            return User.builder()
                    .username("admin")
                    .password("$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy") // "password" hashed
                    .roles("ADMIN")
                    .build();
        }
        
        throw new UsernameNotFoundException("User not found: " + username);
    }
}

