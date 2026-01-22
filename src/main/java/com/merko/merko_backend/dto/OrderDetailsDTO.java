package com.merko.merko_backend.dto;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderDetailsDTO {
    private Long id;
    private String orderNumber;
    private String status;
    private BigDecimal subtotal;
    private BigDecimal taxAmount;
    private BigDecimal shippingCost;
    private BigDecimal totalAmount;
    private LocalDateTime orderDate;
    private LocalDateTime updatedAt;

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
    private String paymentMethod;
    private String cardLastFour;
    private String cardHolderName;
    private String cardExpiration;

    private String shippingMethod;
    private List<OrderItemDTO> orderItems;
}
