package com.merko.merko_backend.dto;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CartDTO {
    private Long id;
    private String userEmail; // Changed from merchantEmail
    private String userName;  // Changed from merchantName
    private String status;
    private BigDecimal subtotal;
    private Integer totalQuantity;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<CartItemDTO> cartItems;
}