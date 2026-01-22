package com.merko.merko_backend.dto;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderSummaryDTO {
    private Long id;
    private String orderNumber;
    private String status;
    private BigDecimal totalAmount;
    private LocalDateTime orderDate;
    private Integer itemCount;
    private String shippingMethod;
}
