package com.merko.merko_backend.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String orderNumber;

    // Using combined User entity
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<OrderItem> orderItems = new ArrayList<>();

    // Shipping Information
    private String shippingFirstName;
    private String shippingLastName;
    private String shippingCompanyName;
    private String shippingAddress;
    private String shippingApartment;
    private String shippingCity;
    private String shippingState;
    private String shippingZipCode;
    private String shippingPhone;

    // Payment Information
    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;

    private String cardLastFour;
    private String cardExpiration;
    private String cardHolderName;

    // Order Totals
    private BigDecimal subtotal;
    private BigDecimal taxAmount;
    private BigDecimal shippingCost;
    private BigDecimal totalAmount;

    @Enumerated(EnumType.STRING)
    private ShippingMethod shippingMethod;

    private LocalDateTime orderDate;
    private LocalDateTime updatedAt;

    // Helper methods
    public void addOrderItem(OrderItem item) {
        orderItems.add(item);
        item.setOrder(this);
    }

    @PrePersist
    protected void onCreate() {
        orderDate = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (orderNumber == null) {
            orderNumber = "ORD-" + System.currentTimeMillis();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }


}