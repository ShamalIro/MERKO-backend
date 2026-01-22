package com.merko.merko_backend.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import com.fasterxml.jackson.annotation.JsonManagedReference;

@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // Changed from Supplier to User reference
    @Column(name = "user_id", nullable = false)
    private Long userId;

    // Store supplier company name directly for easy access
    @Column(name = "supplier_company_name")
    private String supplierCompanyName;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<ProductImage> images = new ArrayList<>();

    @Column(name = "product_name", nullable = false)
    private String productName;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String sku;
    private String category;
    private BigDecimal price;
    private BigDecimal cost;

    @Column(name = "stock_quantity")
    private Integer stockQuantity;

    private BigDecimal weight;
    private String status;
    private String barcode;

    @Column(name = "low_stock_alert")
    private Integer lowStockAlert;

    @Column(name = "track_inventory")
    private String trackInventory;

    @Column(name = "compare_price")
    private BigDecimal comparePrice;

    @Column(name = "profit_margin")
    private BigDecimal profitMargin;

    @Column(columnDefinition = "TEXT")
    private String features;

    @Column(name = "care_instructions", columnDefinition = "TEXT")
    private String careInstructions;

    private String brand;

    @Column(name = "country_of_origin")
    private String countryOfOrigin;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}