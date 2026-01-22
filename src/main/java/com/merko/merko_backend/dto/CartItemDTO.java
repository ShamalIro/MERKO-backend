package com.merko.merko_backend.dto;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CartItemDTO {
    private Long id;
    private Integer productId;
    private String productName;
    private String productSku;
    private String supplierCompanyName;
    private String brand;
    private String category;
    private BigDecimal priceAtTime;
    private Integer quantity;
    // Add explicit setters
    private BigDecimal total;
    private List<String> imageUrls;
    private Integer stockQuantity;
    private LocalDateTime createdAt;

    public BigDecimal getTotal() {
        if (priceAtTime != null && quantity != null) {
            return priceAtTime.multiply(BigDecimal.valueOf(quantity));
        }
        return BigDecimal.ZERO;
    }
}
