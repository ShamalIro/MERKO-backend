package com.merko.merko_backend.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CheckoutRequest {
    private ShippingInfo shippingInfo;
    private PaymentInfo paymentInfo;
    private String shippingMethod;
}

