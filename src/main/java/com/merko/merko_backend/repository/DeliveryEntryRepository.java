package com.merko.merko_backend.repository;

import com.merko.merko_backend.entity.DeliveryEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface DeliveryEntryRepository extends JpaRepository<DeliveryEntry, Long> {
    
    // Find by order ID
    DeliveryEntry findByOrderId(Long orderId);
    
    // Find by status
    List<DeliveryEntry> findByStatus(String status);
    
    // Check if order is already assigned
    boolean existsByOrderId(Long orderId);
    
    // Get all delivery entries ordered by creation date
    List<DeliveryEntry> findAllByOrderByCreatedAtDesc();
}
