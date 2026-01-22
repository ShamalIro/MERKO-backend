package com.merko.merko_backend.repository;

import com.merko.merko_backend.entity.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SupplierRepository extends JpaRepository<Supplier, Long> {
    
    // Find supplier by email
    Optional<Supplier> findByEmail(String email);
    
    // Find supplier by username
    Optional<Supplier> findByUsername(String username);
    
    // Check if email exists
    boolean existsByEmail(String email);
    
    // Check if username exists
    boolean existsByUsername(String username);
    
    // Find suppliers by status
    List<Supplier> findByStatus(String status);
    
    // Find suppliers by role
    List<Supplier> findByRole(String role);
    
    // Find all suppliers ordered by creation date
    List<Supplier> findAllByOrderByCreatedAtDesc();
    
    // Find suppliers pending approval
    @Query("SELECT s FROM Supplier s WHERE s.status = 'PENDING_APPROVAL' ORDER BY s.createdAt DESC")
    List<Supplier> findPendingApprovalSuppliers();
    
    // Find approved suppliers
    @Query("SELECT s FROM Supplier s WHERE s.status = 'APPROVED' ORDER BY s.companyName ASC")
    List<Supplier> findApprovedSuppliers();
    
    // Search suppliers by company name (case insensitive)
    @Query("SELECT s FROM Supplier s WHERE LOWER(s.companyName) LIKE LOWER(CONCAT('%', :companyName, '%'))")
    List<Supplier> findByCompanyNameContainingIgnoreCase(@Param("companyName") String companyName);
    
    // Find recent suppliers ordered by creation date (limit 3)
    @Query("SELECT s FROM Supplier s ORDER BY s.createdAt DESC")
    List<Supplier> findTop3ByOrderByCreatedAtDesc();
}
