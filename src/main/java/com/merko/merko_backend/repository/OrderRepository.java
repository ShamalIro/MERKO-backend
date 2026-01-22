package com.merko.merko_backend.repository;

import com.merko.merko_backend.entity.Order;
import com.merko.merko_backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    // Find orders by User
    @Query("SELECT o FROM Order o LEFT JOIN FETCH o.orderItems items LEFT JOIN FETCH items.product WHERE o.user = :user ORDER BY o.orderDate DESC")
    List<Order> findByUserOrderByOrderDateDesc(@Param("user") User user);

    Optional<Order> findByOrderNumber(String orderNumber);

    @Query("SELECT o FROM Order o LEFT JOIN FETCH o.orderItems items LEFT JOIN FETCH items.product WHERE o.id = :orderId")
    Optional<Order> findByIdWithItemsAndProducts(@Param("orderId") Long orderId);

    @Query("SELECT o FROM Order o LEFT JOIN FETCH o.orderItems WHERE o.id = :orderId")
    Optional<Order> findByIdWithItems(@Param("orderId") Long orderId);
}