package com.merko.merko_backend.repository;

import com.merko.merko_backend.entity.RouteStop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Repository
public interface RouteStopRepository extends JpaRepository<RouteStop, Long> {
    
    // Find stops by route ID ordered by stop order
    List<RouteStop> findByRouteIdOrderByStopOrder(Long routeId);
    
    // Find stops by route ID and status
    List<RouteStop> findByRouteIdAndStatusOrderByStopOrder(Long routeId, RouteStop.StopStatus status);
    
    // Find stops by delivery entry ID
    List<RouteStop> findByDeliveryEntryId(Long deliveryEntryId);
    
    // Count stops by route ID
    long countByRouteId(Long routeId);
    
    // Delete all stops for a route
    @Modifying
    @Transactional
    void deleteByRouteId(Long routeId);
    
    // Delete all stops for a delivery entry
    @Modifying
    @Transactional
    void deleteByDeliveryEntryId(Long deliveryEntryId);
}