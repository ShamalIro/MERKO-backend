package com.merko.merko_backend.dto;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SupplierOrderItemDTO {
    private Long id;
    private String productName;
    private String productSku;
    private Integer quantity;
    private BigDecimal priceAtTime;
    private BigDecimal totalPrice;
    private String status;
    private LocalDateTime orderDate;
    private String orderNumber;
    private String merchantCompanyName;
    private Integer currentStock;
    private String supplierCompanyName; // ADD THIS FIELD
}