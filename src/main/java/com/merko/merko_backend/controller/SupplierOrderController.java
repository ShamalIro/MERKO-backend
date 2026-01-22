package com.merko.merko_backend.controller;

import com.merko.merko_backend.service.SupplierOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/suppliers")
@CrossOrigin(origins = "http://localhost:5173")
public class SupplierOrderController {

    @Autowired
    private SupplierOrderService supplierOrderService;

    // Get all order items for current supplier (excluding PENDING ones for all orders tab)
    @GetMapping("/orders")
    public ResponseEntity<?> getSupplierOrders(@RequestParam String userEmail) {
        try {
            var result = supplierOrderService.getSupplierOrders(userEmail);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    // Get only non-pending orders for the "All Orders" tab
    @GetMapping("/orders/all")
    public ResponseEntity<?> getNonPendingSupplierOrders(@RequestParam String userEmail) {
        try {
            var result = supplierOrderService.getNonPendingSupplierOrders(userEmail);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    // Update order item status (for suppliers) with stock management
    @PutMapping("/orders/items/{itemId}/status")
    public ResponseEntity<?> updateOrderItemStatus(@PathVariable Long itemId,
                                                   @RequestBody Map<String, String> request,
                                                   @RequestParam String userEmail) {
        try {
            String status = request.get("status");
            supplierOrderService.updateOrderItemStatus(itemId, status, userEmail);
            return ResponseEntity.ok(Map.of("message", "Order item status updated successfully"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", "Invalid status value"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }
}