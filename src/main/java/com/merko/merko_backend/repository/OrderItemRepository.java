package com.merko.merko_backend.repository;

import com.merko.merko_backend.entity.OrderItem;
import com.merko.merko_backend.entity.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    // Use direct userId field instead of user relationship
    @Query("SELECT oi FROM OrderItem oi JOIN oi.product p WHERE p.userId = :userId ORDER BY oi.order.orderDate DESC")
    List<OrderItem> findByProductUserId(@Param("userId") Long userId);

    // For non-pending orders
    @Query("SELECT oi FROM OrderItem oi JOIN oi.product p WHERE p.userId = :userId AND oi.status != 'PENDING' ORDER BY oi.order.orderDate DESC")
    List<OrderItem> findByProductUserIdAndStatusNotPending(@Param("userId") Long userId);

    // Remove these methods since Product doesn't have user relationship
    // @Query("SELECT oi FROM OrderItem oi JOIN oi.product p JOIN p.user u WHERE u.id = :userId ORDER BY oi.order.orderDate DESC")
    // List<OrderItem> findByProductUser_Id(@Param("userId") Long userId);

    // @Query("SELECT oi FROM OrderItem oi JOIN oi.product p JOIN p.user u WHERE u.id = :userId AND oi.status != 'PENDING' ORDER BY oi.order.orderDate DESC")
    // List<OrderItem> findByProductUser_IdAndStatusNotPending(@Param("userId") Long userId);

    // Debug method: Get all order items
    @Query("SELECT oi FROM OrderItem oi LEFT JOIN FETCH oi.product p LEFT JOIN FETCH oi.order o ORDER BY oi.id")
    List<OrderItem> findAllWithProductAndOrder();
}