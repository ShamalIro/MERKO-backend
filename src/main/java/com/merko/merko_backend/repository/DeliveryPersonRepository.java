package com.merko.merko_backend.repository;

import com.merko.merko_backend.entity.DeliveryPerson;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DeliveryPersonRepository extends JpaRepository<DeliveryPerson, Long> {

    // Find by email for login
    Optional<DeliveryPerson> findByEmail(String email);

    // Check if email exists
    boolean existsByEmail(String email);

    // Check if license number exists
    boolean existsByLicenseNumber(String licenseNumber);

    // Find by status
    List<DeliveryPerson> findByStatus(String status);

    // Find active delivery persons
    @Query("SELECT dp FROM DeliveryPerson dp WHERE dp.status = 'ACTIVE' ORDER BY dp.createdAt DESC")
    List<DeliveryPerson> findActiveDeliveryPersons();

    // Find by role
    List<DeliveryPerson> findByRole(String role);

    // Get all delivery persons ordered by created date
    List<DeliveryPerson> findAllByOrderByCreatedAtDesc();
}