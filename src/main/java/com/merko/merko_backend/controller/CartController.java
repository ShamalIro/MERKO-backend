package com.merko.merko_backend.controller;

import com.merko.merko_backend.dto.*;
import com.merko.merko_backend.service.CartService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/cart")
@CrossOrigin(origins = "http://localhost:5173", allowedHeaders = "*")
public class CartController {

    private static final Logger logger = LoggerFactory.getLogger(CartController.class);

    @Autowired
    private CartService cartService;

    @PostMapping("/add")
    @Transactional
    public ResponseEntity<?> addToCart(@RequestBody AddToCartRequest request,
                                       @RequestParam String userEmail) {
        try {
            logger.info("🎯 CART ADD ENDPOINT HIT - User: {}, Product: {}, Quantity: {}",
                    userEmail, request.getProductId(), request.getQuantity());

            // Use service layer to handle business logic
            cartService.addToCart(request, userEmail);

            logger.info("✅ Item added to cart successfully");
            return ResponseEntity.ok(Map.of("message", "Item added to cart successfully"));

        } catch (Exception e) {
            logger.error("❌ Error adding item to cart", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", e.getMessage()));
        }
    }

    @GetMapping("/my-cart")
    public ResponseEntity<?> getMyCart(@RequestParam String userEmail) {
        try {
            logger.info("🛒 Fetching cart for user: {}", userEmail);

            // Use service layer to handle business logic
            CartDTO cartDTO = cartService.getMyCart(userEmail);

            logger.info("✅ Cart fetched successfully - Items: {}, Subtotal: {}",
                    cartDTO.getCartItems().size(), cartDTO.getSubtotal());
            return ResponseEntity.ok(cartDTO);

        } catch (Exception e) {
            logger.error("❌ Error fetching cart", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", e.getMessage()));
        }
    }

    @PutMapping("/update/{cartItemId}")
    @Transactional
    public ResponseEntity<?> updateCartItem(@PathVariable Long cartItemId,
                                            @RequestBody UpdateCartItemRequest request,
                                            @RequestParam String userEmail) {
        try {
            logger.info("🛒 Updating cart item - ID: {}, New Quantity: {}, User: {}",
                    cartItemId, request.getQuantity(), userEmail);

            // Use service layer to handle business logic
            cartService.updateCartItem(cartItemId, request, userEmail);

            logger.info("✅ Cart item updated successfully");
            return ResponseEntity.ok(Map.of("message", "Cart item updated successfully"));

        } catch (Exception e) {
            logger.error("❌ Error updating cart item", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", e.getMessage()));
        }
    }

    @DeleteMapping("/remove/{cartItemId}")
    @Transactional
    public ResponseEntity<?> removeCartItem(@PathVariable Long cartItemId,
                                            @RequestParam String userEmail) {
        try {
            logger.info("🛒 Removing cart item - ID: {}, User: {}", cartItemId, userEmail);

            // Use service layer to handle business logic
            cartService.removeCartItem(cartItemId, userEmail);

            logger.info("✅ Cart item removed successfully");
            return ResponseEntity.ok(Map.of("message", "Cart item removed successfully"));

        } catch (Exception e) {
            logger.error("❌ Error removing cart item", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", e.getMessage()));
        }
    }

    @DeleteMapping("/clear")
    @Transactional
    public ResponseEntity<?> clearCart(@RequestParam String userEmail) {
        try {
            logger.info("🛒 Clearing cart for user: {}", userEmail);

            // Use service layer to handle business logic
            cartService.clearCart(userEmail);

            logger.info("✅ Cart cleared successfully");
            return ResponseEntity.ok(Map.of("message", "Cart cleared successfully"));

        } catch (Exception e) {
            logger.error("❌ Error clearing cart", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", e.getMessage()));
        }
    }
}