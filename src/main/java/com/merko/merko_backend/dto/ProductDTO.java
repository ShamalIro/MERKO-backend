package com.merko.merko_backend.dto;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductDTO {
    private Integer id;
    private String productName;
    private String description;
    private String sku;
    private String category;
    private BigDecimal price;
    private BigDecimal cost;
    private Integer stockQuantity;
    private BigDecimal weight;
    private String status;
    private String barcode;
    private Integer lowStockAlert;
    private String trackInventory;
    private BigDecimal comparePrice;
    private BigDecimal profitMargin;
    private String features;
    private String careInstructions;
    private String brand;
    private String countryOfOrigin;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Supplier info (without causing lazy loading issues)
    private String supplierCompanyName;

    // Images as simple URLs
    private List<String> imageUrls;
}
