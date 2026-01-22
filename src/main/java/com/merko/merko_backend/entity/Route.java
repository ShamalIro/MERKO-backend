package com.merko.merko_backend.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "routes")
public class Route {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "route_id")
    private Long routeId;
    
    @Column(name = "route_name", nullable = false)
    private String routeName;
    
    @Column(name = "start_location", nullable = false, length = 500)
    private String startLocation;
    
    @Column(name = "end_location", nullable = false, length = 500)
    private String endLocation;
    
    @Column(name = "total_distance")
    private BigDecimal totalDistance;
    
    @Column(name = "estimated_duration")
    private Integer estimatedDuration;
    
    @Column(name = "route_points", columnDefinition = "JSON")
    private String routePoints;
    
    @Column(name = "delivery_addresses", columnDefinition = "JSON")
    private String deliveryAddresses;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private RouteStatus status = RouteStatus.active;
    
    public enum RouteStatus {
        active, completed, archived
    }
    
    // Constructors
    public Route() {}
    
    public Route(String routeName, String startLocation, String endLocation) {
        this.routeName = routeName;
        this.startLocation = startLocation;
        this.endLocation = endLocation;
        this.status = RouteStatus.active;
    }
    
    // Getters and Setters
    public Long getRouteId() { return routeId; }
    public void setRouteId(Long routeId) { this.routeId = routeId; }
    
    public String getRouteName() { return routeName; }
    public void setRouteName(String routeName) { this.routeName = routeName; }
    
    public String getStartLocation() { return startLocation; }
    public void setStartLocation(String startLocation) { this.startLocation = startLocation; }
    
    public String getEndLocation() { return endLocation; }
    public void setEndLocation(String endLocation) { this.endLocation = endLocation; }
    
    public BigDecimal getTotalDistance() { return totalDistance; }
    public void setTotalDistance(BigDecimal totalDistance) { this.totalDistance = totalDistance; }
    
    public Integer getEstimatedDuration() { return estimatedDuration; }
    public void setEstimatedDuration(Integer estimatedDuration) { this.estimatedDuration = estimatedDuration; }
    
    public String getRoutePoints() { return routePoints; }
    public void setRoutePoints(String routePoints) { this.routePoints = routePoints; }
    
    public String getDeliveryAddresses() { return deliveryAddresses; }
    public void setDeliveryAddresses(String deliveryAddresses) { this.deliveryAddresses = deliveryAddresses; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    public RouteStatus getStatus() { return status; }
    public void setStatus(RouteStatus status) { 
        this.status = status;
        this.updatedAt = LocalDateTime.now();
    }
}