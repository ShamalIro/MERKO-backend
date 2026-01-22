package com.merko.merko_backend.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CartAddResult {
    private int actualQuantity;
    private boolean wasCapped;
    private int availableStock;
    private int requestedQuantity;
}