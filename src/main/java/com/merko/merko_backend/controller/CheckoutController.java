package com.merko.merko_backend.controller;

import com.merko.merko_backend.dto.CheckoutRequest;
import com.merko.merko_backend.entity.Order;
import com.merko.merko_backend.service.CheckoutService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/checkout")
@CrossOrigin(origins = "http://localhost:5173")
public class CheckoutController {

    @Autowired
    private CheckoutService checkoutService;

    @PostMapping("/process")
    public ResponseEntity<?> processCheckout(@RequestBody CheckoutRequest checkoutRequest,
                                             @RequestParam String userEmail) {
        try {
            Order order = checkoutService.processCheckout(userEmail, checkoutRequest);

            return ResponseEntity.ok(Map.of(
                    "message", "Order placed successfully",
                    "orderNumber", order.getOrderNumber(),
                    "orderId", order.getId()
            ));

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }
}