package com.merko.merko_backend.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "confirmed_orders")
public class ConfirmedOrder {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Long orderId;
    
    @Column(name = "merchant_id")
    private Long merchantId;
    
    @Column(name = "supplier_id")
    private Long supplierId;
    
    @Column(name = "delivery_address")
    private String deliveryAddress;
    
    @Column(name = "route")
    private String route;
    
    @Column(name = "status")
    private String status;
    
    @Column(name = "order_date")
    private LocalDateTime orderDate;
    
    @Column(name = "total_amount")
    private Double totalAmount;
    
    @Column(name = "merchant_name")
    private String merchantName;
    
    @Column(name = "supplier_name")
    private String supplierName;
    
    @Column(name = "contact_number")
    private String contactNumber;
    
    @Column(name = "delivery_instructions")
    private String deliveryInstructions;
    
    // Default constructor
    public ConfirmedOrder() {}
    
    // Constructor with essential fields
    public ConfirmedOrder(Long merchantId, Long supplierId, String deliveryAddress, 
                         String status, LocalDateTime orderDate) {
        this.merchantId = merchantId;
        this.supplierId = supplierId;
        this.deliveryAddress = deliveryAddress;
        this.status = status;
        this.orderDate = orderDate;
    }
    
    // Getters and Setters
    public Long getOrderId() {
        return orderId;
    }
    
    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }
    
    public Long getMerchantId() {
        return merchantId;
    }
    
    public void setMerchantId(Long merchantId) {
        this.merchantId = merchantId;
    }
    
    public Long getSupplierId() {
        return supplierId;
    }
    
    public void setSupplierId(Long supplierId) {
        this.supplierId = supplierId;
    }
    
    public String getDeliveryAddress() {
        return deliveryAddress;
    }
    
    public void setDeliveryAddress(String deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }
    
    public String getRoute() {
        return route;
    }
    
    public void setRoute(String route) {
        this.route = route;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public LocalDateTime getOrderDate() {
        return orderDate;
    }
    
    public void setOrderDate(LocalDateTime orderDate) {
        this.orderDate = orderDate;
    }
    
    public Double getTotalAmount() {
        return totalAmount;
    }
    
    public void setTotalAmount(Double totalAmount) {
        this.totalAmount = totalAmount;
    }
    
    public String getMerchantName() {
        return merchantName;
    }
    
    public void setMerchantName(String merchantName) {
        this.merchantName = merchantName;
    }
    
    public String getSupplierName() {
        return supplierName;
    }
    
    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }
    
    public String getContactNumber() {
        return contactNumber;
    }
    
    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }
    
    public String getDeliveryInstructions() {
        return deliveryInstructions;
    }
    
    public void setDeliveryInstructions(String deliveryInstructions) {
        this.deliveryInstructions = deliveryInstructions;
    }
    
    @Override
    public String toString() {
        return "ConfirmedOrder{" +
                "orderId=" + orderId +
                ", merchantId=" + merchantId +
                ", supplierId=" + supplierId +
                ", deliveryAddress='" + deliveryAddress + '\'' +
                ", route='" + route + '\'' +
                ", status='" + status + '\'' +
                ", orderDate=" + orderDate +
                ", totalAmount=" + totalAmount +
                ", merchantName='" + merchantName + '\'' +
                ", supplierName='" + supplierName + '\'' +
                '}';
    }
}
