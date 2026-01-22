package com.merko.merko_backend.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentInfo {
    private String method; // Use String instead of enum for now
    private String cardNumber;
    private String expirationDate;
    private String cvv;
    private String cardHolderName;
    private String purchaseOrderNumber;
}
