package com.merko.merko_backend.repository;

import com.merko.merko_backend.entity.User;
import com.merko.merko_backend.entity.UserRole;
import com.merko.merko_backend.entity.UserStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    // Find user by email
    Optional<User> findByEmail(String email);
    
    // Check if email exists
    boolean existsByEmail(String email);
    
    // Find users by role
    List<User> findByRole(UserRole role);
    
    // Find users by status
    List<User> findByStatus(UserStatus status);
    
    // Find users by role and status
    List<User> findByRoleAndStatus(UserRole role, UserStatus status);
    
    // Find approved users
    @Query("SELECT u FROM User u WHERE u.status = 'APPROVED'")
    List<User> findApprovedUsers();
    
    // Find pending users
    @Query("SELECT u FROM User u WHERE u.status = 'PENDING' ORDER BY u.registrationDate ASC")
    List<User> findPendingUsers();
    
    // Find users by company name (case insensitive)
    List<User> findByCompanyNameContainingIgnoreCase(String companyName);
    
    // Find users by business type
    List<User> findByBusinessType(String businessType);
    
    // Count users by status
    long countByStatus(UserStatus status);
    
    // Count users by role
    long countByRole(UserRole role);
}