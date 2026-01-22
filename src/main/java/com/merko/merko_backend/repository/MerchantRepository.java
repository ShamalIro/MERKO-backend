package com.merko.merko_backend.repository;

import com.merko.merko_backend.entity.Merchant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MerchantRepository extends JpaRepository<Merchant, Long> {
    
    // Find merchant by email
    Optional<Merchant> findByEmail(String email);
    
    // Find merchant by username
    Optional<Merchant> findByUsername(String username);
    
    // Check if email exists
    boolean existsByEmail(String email);
    
    // Check if username exists
    boolean existsByUsername(String username);
    
    // Find merchants by status
    List<Merchant> findByStatus(String status);
    
    // Find merchants by business type
    List<Merchant> findByBusinessType(String businessType);
    
    // Find merchants by company name (case insensitive)
    @Query("SELECT m FROM Merchant m WHERE LOWER(m.companyName) LIKE LOWER(CONCAT('%', :companyName, '%'))")
    List<Merchant> findByCompanyNameContainingIgnoreCase(@Param("companyName") String companyName);
    
    // Find merchants pending approval
    @Query("SELECT m FROM Merchant m WHERE m.status = 'PENDING_APPROVAL' ORDER BY m.createdAt ASC")
    List<Merchant> findPendingApprovalMerchants();
    
    // Find active merchants
    @Query("SELECT m FROM Merchant m WHERE m.status = 'ACTIVE' ORDER BY m.companyName ASC")
    List<Merchant> findActiveMerchants();
    
    // Count merchants by status
    @Query("SELECT COUNT(m) FROM Merchant m WHERE m.status = :status")
    long countByStatus(@Param("status") String status);
    
    // Find recent merchants ordered by creation date (limit 3)
    @Query("SELECT m FROM Merchant m ORDER BY m.createdAt DESC")
    List<Merchant> findTop3ByOrderByCreatedAtDesc();
}
