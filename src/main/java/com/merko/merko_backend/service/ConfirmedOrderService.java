package com.merko.merko_backend.service;

import com.merko.merko_backend.entity.ConfirmedOrder;
import com.merko.merko_backend.repository.ConfirmedOrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class ConfirmedOrderService {
    
    @Autowired
    private ConfirmedOrderRepository confirmedOrderRepository;
    
    // Get all confirmed orders
    public List<ConfirmedOrder> getAllOrders() {
        return confirmedOrderRepository.findAllByOrderByOrderDateDesc();
    }
    
    // Get orders by status
    public List<ConfirmedOrder> getOrdersByStatus(String status) {
        if (status == null || status.equals("All")) {
            return getAllOrders();
        }
        return confirmedOrderRepository.findByStatusOrderByOrderDateDesc(status);
    }
    
    // Get orders by route
    public List<ConfirmedOrder> getOrdersByRoute(String route) {
        if (route == null || route.equals("All Routes")) {
            return getAllOrders();
        }
        return confirmedOrderRepository.findByRoute(route);
    }
    
    // Get orders by date filter
    public List<ConfirmedOrder> getOrdersByDateFilter(String dateFilter) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startDate;
        LocalDateTime endDate = now;
        
        switch (dateFilter.toLowerCase()) {
            case "today":
                return confirmedOrderRepository.findOrdersForToday();
            case "yesterday":
                startDate = now.minusDays(1).withHour(0).withMinute(0).withSecond(0);
                endDate = now.minusDays(1).withHour(23).withMinute(59).withSecond(59);
                return confirmedOrderRepository.findByDateRange(startDate, endDate);
            case "this week":
                startDate = now.minusDays(7).withHour(0).withMinute(0).withSecond(0);
                return confirmedOrderRepository.findByDateRange(startDate, endDate);
            case "this month":
                startDate = now.minusDays(30).withHour(0).withMinute(0).withSecond(0);
                return confirmedOrderRepository.findByDateRange(startDate, endDate);
            default:
                return getAllOrders();
        }
    }
    
    // Get orders with multiple filters
    public List<ConfirmedOrder> getFilteredOrders(String status, String dateFilter, String route) {
        List<ConfirmedOrder> orders = getAllOrders();
        
        // Apply status filter
        if (status != null && !status.equals("All")) {
            orders = orders.stream()
                    .filter(order -> order.getStatus().equals(status))
                    .toList();
        }
        
        // Apply route filter
        if (route != null && !route.equals("All Routes")) {
            orders = orders.stream()
                    .filter(order -> order.getRoute() != null && order.getRoute().equals(route))
                    .toList();
        }
        
        // Apply date filter
        if (dateFilter != null && !dateFilter.equals("All")) {
            LocalDateTime now = LocalDateTime.now();
            LocalDate today = LocalDate.now();
            
            orders = orders.stream()
                    .filter(order -> {
                        LocalDate orderDate = order.getOrderDate().toLocalDate();
                        switch (dateFilter.toLowerCase()) {
                            case "today":
                                return orderDate.equals(today);
                            case "yesterday":
                                return orderDate.equals(today.minusDays(1));
                            case "this week":
                                return orderDate.isAfter(today.minusDays(7)) || orderDate.equals(today.minusDays(7));
                            case "this month":
                                return orderDate.isAfter(today.minusDays(30)) || orderDate.equals(today.minusDays(30));
                            default:
                                return true;
                        }
                    })
                    .toList();
        }
        
        return orders;
    }
    
    // Get order by ID
    public Optional<ConfirmedOrder> getOrderById(Long orderId) {
        return confirmedOrderRepository.findById(orderId);
    }
    
    // Get assignable orders (orders ready for delivery assignment)
    public List<ConfirmedOrder> getAssignableOrders() {
        return confirmedOrderRepository.findAssignableOrders();
    }
    
    // Update order status
    public ConfirmedOrder updateOrderStatus(Long orderId, String newStatus) {
        Optional<ConfirmedOrder> orderOpt = confirmedOrderRepository.findById(orderId);
        if (orderOpt.isPresent()) {
            ConfirmedOrder order = orderOpt.get();
            order.setStatus(newStatus);
            return confirmedOrderRepository.save(order);
        }
        throw new RuntimeException("Order not found with ID: " + orderId);
    }
    
    // Assign route to order
    public ConfirmedOrder assignRoute(Long orderId, String route) {
        Optional<ConfirmedOrder> orderOpt = confirmedOrderRepository.findById(orderId);
        if (orderOpt.isPresent()) {
            ConfirmedOrder order = orderOpt.get();
            order.setRoute(route);
            return confirmedOrderRepository.save(order);
        }
        throw new RuntimeException("Order not found with ID: " + orderId);
    }
    
    // Get orders count by status
    public long getOrdersCountByStatus(String status) {
        return confirmedOrderRepository.countByStatus(status);
    }
    
    // Save or update order
    public ConfirmedOrder saveOrder(ConfirmedOrder order) {
        return confirmedOrderRepository.save(order);
    }
    
    // Delete order
    public void deleteOrder(Long orderId) {
        confirmedOrderRepository.deleteById(orderId);
    }
}
