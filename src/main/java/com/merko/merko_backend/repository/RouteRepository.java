package com.merko.merko_backend.repository;

import com.merko.merko_backend.entity.Route;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface RouteRepository extends JpaRepository<Route, Long> {
    
    // Find routes by status
    List<Route> findByStatusOrderByCreatedAtDesc(Route.RouteStatus status);
    
    // Find active routes
    @Query("SELECT r FROM Route r WHERE r.status = 'active' ORDER BY r.createdAt DESC")
    List<Route> findActiveRoutes();
    
    // Find all routes ordered by creation date
    List<Route> findAllByOrderByCreatedAtDesc();
    
    // Count routes by status
    long countByStatus(Route.RouteStatus status);
}