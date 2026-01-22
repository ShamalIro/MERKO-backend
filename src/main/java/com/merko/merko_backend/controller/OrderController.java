package com.merko.merko_backend.controller;

import com.merko.merko_backend.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = "http://localhost:5173")
public class OrderController {

    @Autowired
    private OrderService orderService;

    // Get all orders for current merchant
    @GetMapping("/my-orders")
    public ResponseEntity<?> getMyOrders(@RequestParam String userEmail) {
        try {
            var result = orderService.getMyOrders(userEmail);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    // Get specific order details
    @GetMapping("/{orderId}")
    public ResponseEntity<?> getOrderDetails(@PathVariable Long orderId,
                                             @RequestParam String userEmail) {
        try {
            var result = orderService.getOrderDetails(orderId, userEmail);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    // Update individual order item quantity
    @PutMapping("/{orderId}/items/{itemId}/quantity")
    public ResponseEntity<?> updateOrderItemQuantity(@PathVariable Long orderId,
                                                     @PathVariable Long itemId,
                                                     @RequestBody Map<String, Integer> request,
                                                     @RequestParam String userEmail) {
        try {
            Integer quantity = request.get("quantity");
            var result = orderService.updateOrderItemQuantity(orderId, itemId, quantity, userEmail);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    // Update individual order item status
    @PutMapping("/{orderId}/items/{itemId}/status")
    public ResponseEntity<?> updateOrderItemStatus(@PathVariable Long orderId,
                                                   @PathVariable Long itemId,
                                                   @RequestBody Map<String, String> request,
                                                   @RequestParam String userEmail) {
        try {
            String status = request.get("status");
            var result = orderService.updateOrderItemStatus(orderId, itemId, status, userEmail);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    // DELETE individual order item
    @DeleteMapping("/{orderId}/items/{itemId}")
    public ResponseEntity<?> deleteOrderItem(@PathVariable Long orderId,
                                             @PathVariable Long itemId,
                                             @RequestParam String userEmail) {
        try {
            var result = orderService.deleteOrderItem(orderId, itemId, userEmail);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    // Cancel entire order (cancel all items)
    @DeleteMapping("/{orderId}")
    public ResponseEntity<?> cancelOrder(@PathVariable Long orderId,
                                         @RequestParam String userEmail) {
        try {
            orderService.cancelOrder(orderId, userEmail);
            return ResponseEntity.ok(Map.of("message", "Order cancelled successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }
}