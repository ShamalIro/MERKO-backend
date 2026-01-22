package com.merko.merko_backend.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "delivery_entries")
public class DeliveryEntry {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "delivery_id")
    private Long deliveryId;
    
    @Column(name = "order_id", nullable = false)
    private Long orderId;
    
    @Column(name = "merchant_name", nullable = false)
    private String merchantName;
    
    @Column(name = "supplier_name", nullable = false)
    private String supplierName;
    
    @Column(name = "delivery_address", nullable = false)
    private String deliveryAddress;
    
    @Column(name = "status", nullable = false)
    private String status = "Ready for delivery";
    
    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();
    
    // Constructors
    public DeliveryEntry() {}
    
    public DeliveryEntry(Long orderId, String merchantName, String supplierName, String deliveryAddress) {
        this.orderId = orderId;
        this.merchantName = merchantName;
        this.supplierName = supplierName;
        this.deliveryAddress = deliveryAddress;
        this.status = "Ready for delivery";
    }
    
    // Getters and Setters
    public Long getDeliveryId() { return deliveryId; }
    public void setDeliveryId(Long deliveryId) { this.deliveryId = deliveryId; }
    
    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }
    
    public String getMerchantName() { return merchantName; }
    public void setMerchantName(String merchantName) { this.merchantName = merchantName; }
    
    public String getSupplierName() { return supplierName; }
    public void setSupplierName(String supplierName) { this.supplierName = supplierName; }
    
    public String getDeliveryAddress() { return deliveryAddress; }
    public void setDeliveryAddress(String deliveryAddress) { this.deliveryAddress = deliveryAddress; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { 
        this.status = status;
        this.updatedAt = LocalDateTime.now();
    }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
