package com.merko.merko_backend.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "route_stops")
public class RouteStop {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "stop_id")
    private Long stopId;
    
    @Column(name = "route_id", nullable = false)
    private Long routeId;
    
    @Column(name = "delivery_entry_id", nullable = false)
    private Long deliveryEntryId;
    
    @Column(name = "stop_order", nullable = false)
    private Integer stopOrder;
    
    @Column(name = "address", nullable = false, length = 500)
    private String address;
    
    @Column(name = "latitude", precision = 10, scale = 8)
    private BigDecimal latitude;
    
    @Column(name = "longitude", precision = 11, scale = 8)
    private BigDecimal longitude;
    
    @Column(name = "estimated_arrival_time")
    private LocalTime estimatedArrivalTime;
    
    @Column(name = "actual_arrival_time")
    private LocalTime actualArrivalTime;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private StopStatus status = StopStatus.pending;
    
    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
    
    public enum StopStatus {
        pending, visited, skipped
    }
    
    // Constructors
    public RouteStop() {}
    
    public RouteStop(Long routeId, Long deliveryEntryId, Integer stopOrder, String address) {
        this.routeId = routeId;
        this.deliveryEntryId = deliveryEntryId;
        this.stopOrder = stopOrder;
        this.address = address;
        this.status = StopStatus.pending;
    }
    
    // Getters and Setters
    public Long getStopId() { return stopId; }
    public void setStopId(Long stopId) { this.stopId = stopId; }
    
    public Long getRouteId() { return routeId; }
    public void setRouteId(Long routeId) { this.routeId = routeId; }
    
    public Long getDeliveryEntryId() { return deliveryEntryId; }
    public void setDeliveryEntryId(Long deliveryEntryId) { this.deliveryEntryId = deliveryEntryId; }
    
    public Integer getStopOrder() { return stopOrder; }
    public void setStopOrder(Integer stopOrder) { this.stopOrder = stopOrder; }
    
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    
    public BigDecimal getLatitude() { return latitude; }
    public void setLatitude(BigDecimal latitude) { this.latitude = latitude; }
    
    public BigDecimal getLongitude() { return longitude; }
    public void setLongitude(BigDecimal longitude) { this.longitude = longitude; }
    
    public LocalTime getEstimatedArrivalTime() { return estimatedArrivalTime; }
    public void setEstimatedArrivalTime(LocalTime estimatedArrivalTime) { this.estimatedArrivalTime = estimatedArrivalTime; }
    
    public LocalTime getActualArrivalTime() { return actualArrivalTime; }
    public void setActualArrivalTime(LocalTime actualArrivalTime) { this.actualArrivalTime = actualArrivalTime; }
    
    public StopStatus getStatus() { return status; }
    public void setStatus(StopStatus status) { this.status = status; }
    
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}