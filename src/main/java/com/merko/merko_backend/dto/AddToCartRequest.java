package com.merko.merko_backend.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AddToCartRequest {
    private Integer productId;
    private Integer quantity;
}