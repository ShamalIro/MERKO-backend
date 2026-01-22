package com.merko.merko_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemDTO {
    private Long id;
    private Integer productId;
    private String productName;
    private String productSku;
    private Integer quantity;
    private BigDecimal priceAtTime;
    private BigDecimal totalPrice;
    private String status; // ADD THIS FIELD
}
