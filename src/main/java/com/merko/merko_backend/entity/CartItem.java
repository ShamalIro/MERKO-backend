package com.merko.merko_backend.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name = "cart_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id", nullable = false)
    private Cart cart;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "quantity", nullable = false)
    private Integer quantity = 1;

    @Column(name = "price_at_time", precision = 10, scale = 2, nullable = false)
    private BigDecimal priceAtTime;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Helper method to calculate total price for this cart item
    public BigDecimal getTotal() {
        if (priceAtTime == null || quantity == null) {
            return BigDecimal.ZERO;
        }
        return priceAtTime.multiply(BigDecimal.valueOf(quantity));
    }

    // Business logic methods
    public void increaseQuantity(Integer amount) {
        if (amount != null && amount > 0) {
            this.quantity += amount;
        }
    }

    public void decreaseQuantity(Integer amount) {
        if (amount != null && amount > 0) {
            this.quantity = Math.max(0, this.quantity - amount);
        }
    }

    public void updateQuantity(Integer newQuantity) {
        if (newQuantity != null && newQuantity >= 0) {
            this.quantity = newQuantity;
        }
    }
}
