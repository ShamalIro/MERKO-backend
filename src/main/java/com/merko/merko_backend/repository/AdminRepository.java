package com.merko.merko_backend.repository;

import com.merko.merko_backend.entity.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface AdminRepository extends JpaRepository<Admin, Long> {
    
    // Find admin by email
    Optional<Admin> findByEmail(String email);
    
    // Check if admin exists by email
    boolean existsByEmail(String email);
}
