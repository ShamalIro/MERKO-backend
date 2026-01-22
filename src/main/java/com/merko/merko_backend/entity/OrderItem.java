package com.merko.merko_backend.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "order_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    @JsonIgnore
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    private Integer quantity;

    @Column(precision = 38, scale = 2)
    private BigDecimal priceAtTime;

    @Column(precision = 38, scale = 2)
    private BigDecimal totalPrice;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status = OrderStatus.PENDING; // ADD THIS FIELD

    // Helper method to calculate total
    public void calculateTotal() {
        if (priceAtTime != null && quantity != null) {
            totalPrice = priceAtTime.multiply(BigDecimal.valueOf(quantity));
        }
    }

    @PrePersist
    @PreUpdate
    protected void calculateTotalPrice() {
        calculateTotal();
    }
}
