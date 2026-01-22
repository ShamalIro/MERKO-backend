package com.merko.merko_backend.repository;

import com.merko.merko_backend.entity.ConfirmedOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ConfirmedOrderRepository extends JpaRepository<ConfirmedOrder, Long> {
    
    // Find all orders by status
    List<ConfirmedOrder> findByStatus(String status);
    
    // Find orders by merchant ID
    List<ConfirmedOrder> findByMerchantId(Long merchantId);
    
    // Find orders by supplier ID
    List<ConfirmedOrder> findBySupplierId(Long supplierId);
    
    // Find orders by route
    List<ConfirmedOrder> findByRoute(String route);
    
    // Find orders by date range
    @Query("SELECT co FROM ConfirmedOrder co WHERE co.orderDate BETWEEN :startDate AND :endDate")
    List<ConfirmedOrder> findByDateRange(@Param("startDate") LocalDateTime startDate, 
                                       @Param("endDate") LocalDateTime endDate);
    
    // Find orders for today
    @Query("SELECT co FROM ConfirmedOrder co WHERE DATE(co.orderDate) = CURRENT_DATE")
    List<ConfirmedOrder> findOrdersForToday();
    
    // Find orders by status and route
    List<ConfirmedOrder> findByStatusAndRoute(String status, String route);
    
    // Find orders by status ordered by date
    List<ConfirmedOrder> findByStatusOrderByOrderDateDesc(String status);
    
    // Get all orders ordered by date (newest first)
    List<ConfirmedOrder> findAllByOrderByOrderDateDesc();
    
    // Count orders by status
    long countByStatus(String status);
    
    // Find pending orders (status = 'Pending' or 'Confirmed')
    @Query("SELECT co FROM ConfirmedOrder co WHERE co.status IN ('Pending', 'Confirmed', 'Ready for Pickup') ORDER BY co.orderDate DESC")
    List<ConfirmedOrder> findAssignableOrders();
}
