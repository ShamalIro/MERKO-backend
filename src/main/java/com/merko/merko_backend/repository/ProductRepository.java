// Add these methods to your existing ProductRepository
package com.merko.merko_backend.repository;

import com.merko.merko_backend.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Integer> {
    // Existing methods
    List<Product> findByUserId(Long userId);
    Optional<Product> findBySkuAndUserId(String sku, Long userId);

    // NEW METHODS for supplier products
    List<Product> findByUserIdAndStatus(Long userId, String status);

    @Query("SELECT p FROM Product p WHERE p.userId = :userId AND p.status = 'Active'")
    List<Product> findActiveProductsBySupplier(@Param("userId") Long userId);

    // Method to get products by supplier ID with eager loading of images
    @Query("SELECT DISTINCT p FROM Product p LEFT JOIN FETCH p.images WHERE p.userId = :supplierId AND p.status = 'Active'")
    List<Product> findBySupplierIdWithImages(@Param("supplierId") Long supplierId);
}