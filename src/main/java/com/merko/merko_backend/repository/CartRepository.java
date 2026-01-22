package com.merko.merko_backend.repository;

import com.merko.merko_backend.entity.Cart;
import com.merko.merko_backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {

    // Updated to use User instead of Merchant
    @Query("SELECT c FROM Cart c WHERE c.user = :user AND c.status = 'ACTIVE'")
    Optional<Cart> findActiveCartByUser(@Param("user") User user);

    // Updated to use User instead of Merchant
    @Query("SELECT c FROM Cart c WHERE c.user = :user AND c.status = :status")
    Optional<Cart> findByUserAndStatus(@Param("user") User user, @Param("status") String status);
}