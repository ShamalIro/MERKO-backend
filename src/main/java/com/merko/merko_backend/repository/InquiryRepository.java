package com.merko.merko_backend.repository;

import com.merko.merko_backend.entity.Inquiry;
import com.merko.merko_backend.entity.InquiryStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface InquiryRepository extends JpaRepository<Inquiry, Long> {

    // SIMPLIFIED: Get inquiries by user email
    @Query("SELECT i FROM Inquiry i WHERE i.user.email = :email ORDER BY i.createdAt DESC")
    List<Inquiry> findByUserEmail(@Param("email") String email);

    // For admin - all inquiries
    List<Inquiry> findAllByOrderByCreatedAtDesc();

    // For admin - inquiries by type
    @Query("SELECT i FROM Inquiry i WHERE i.userType = :userType ORDER BY i.createdAt DESC")
    List<Inquiry> findByUserTypeOrderByCreatedAtDesc(@Param("userType") String userType);

    // Status filtering
    @Query("SELECT i FROM Inquiry i WHERE CAST(i.status AS string) = :status ORDER BY i.createdAt DESC")
    List<Inquiry> findByStatusOrderByCreatedAtDesc(@Param("status") String status);

    // Alternative method using enum
    List<Inquiry> findByStatusOrderByCreatedAtDesc(InquiryStatus status);

    // Additional methods
    @Query("SELECT COUNT(i) FROM Inquiry i")
    Long countAllInquiries();

    @Query("SELECT COUNT(i) FROM Inquiry i WHERE i.user.email = :email")
    Long countByUserEmail(@Param("email") String email);

    // Get all inquiries with user details for debugging
    @Query("SELECT i FROM Inquiry i LEFT JOIN FETCH i.user u ORDER BY i.createdAt DESC")
    List<Inquiry> findAllWithUserDetails();

    // Simple method to verify data exists
    @Query("SELECT i.id, i.userType, i.user.email, i.topic FROM Inquiry i")
    List<Object[]> findAllBasicInfo();

    // Find inquiries by user ID (optional)
    List<Inquiry> findByUserIdOrderByCreatedAtDesc(Long userId);
}