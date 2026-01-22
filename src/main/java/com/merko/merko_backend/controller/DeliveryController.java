package com.merko.merko_backend.controller;

import com.merko.merko_backend.entity.ConfirmedOrder;
import com.merko.merko_backend.entity.DeliveryEntry;
import com.merko.merko_backend.entity.Route;
import com.merko.merko_backend.service.ConfirmedOrderService;
import com.merko.merko_backend.service.DeliveryEntryService;
import com.merko.merko_backend.service.RouteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/delivery")
@CrossOrigin(origins = "*")
public class DeliveryController {
    
    @Autowired
    private ConfirmedOrderService confirmedOrderService;
    
    @Autowired
    private DeliveryEntryService deliveryEntryService;
    
    @Autowired
    private RouteService routeService;
    
    // Get all orders
    @GetMapping("/orders")
    public ResponseEntity<List<ConfirmedOrder>> getAllOrders() {
        try {
            List<ConfirmedOrder> orders = confirmedOrderService.getAllOrders();
            return ResponseEntity.ok(orders);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    // Get orders with filters
    @GetMapping("/orders/filter")
    public ResponseEntity<List<ConfirmedOrder>> getFilteredOrders(
            @RequestParam(required = false, defaultValue = "All") String status,
            @RequestParam(required = false, defaultValue = "All") String dateFilter,
            @RequestParam(required = false, defaultValue = "All Routes") String route) {
        try {
            List<ConfirmedOrder> orders = confirmedOrderService.getFilteredOrders(status, dateFilter, route);
            return ResponseEntity.ok(orders);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    // Get orders ready for pickup (for delivery assignment)
    @GetMapping("/orders/ready-for-pickup")
    public ResponseEntity<List<ConfirmedOrder>> getReadyForPickupOrders() {
        try {
            List<ConfirmedOrder> orders = confirmedOrderService.getOrdersByStatus("Ready to Pick");
            return ResponseEntity.ok(orders);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    // Get assignable orders (for delivery assignment)
    @GetMapping("/orders/assignable")
    public ResponseEntity<List<ConfirmedOrder>> getAssignableOrders() {
        try {
            List<ConfirmedOrder> orders = confirmedOrderService.getAssignableOrders();
            return ResponseEntity.ok(orders);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    // Get order by ID
    @GetMapping("/orders/{orderId}")
    public ResponseEntity<ConfirmedOrder> getOrderById(@PathVariable Long orderId) {
        try {
            Optional<ConfirmedOrder> order = confirmedOrderService.getOrderById(orderId);
            if (order.isPresent()) {
                return ResponseEntity.ok(order.get());
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    // Update order status
    @PutMapping("/orders/{orderId}/status")
    public ResponseEntity<ConfirmedOrder> updateOrderStatus(
            @PathVariable Long orderId, 
            @RequestBody Map<String, String> statusUpdate) {
        try {
            String newStatus = statusUpdate.get("status");
            if (newStatus == null || newStatus.trim().isEmpty()) {
                return ResponseEntity.badRequest().build();
            }
            
            ConfirmedOrder updatedOrder = confirmedOrderService.updateOrderStatus(orderId, newStatus);
            return ResponseEntity.ok(updatedOrder);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    // Assign route to order
    @PutMapping("/orders/{orderId}/route")
    public ResponseEntity<ConfirmedOrder> assignRoute(
            @PathVariable Long orderId, 
            @RequestBody Map<String, String> routeUpdate) {
        try {
            String route = routeUpdate.get("route");
            if (route == null || route.trim().isEmpty()) {
                return ResponseEntity.badRequest().build();
            }
            
            ConfirmedOrder updatedOrder = confirmedOrderService.assignRoute(orderId, route);
            return ResponseEntity.ok(updatedOrder);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    // Get orders count by status
    @GetMapping("/orders/count/{status}")
    public ResponseEntity<Map<String, Long>> getOrdersCountByStatus(@PathVariable String status) {
        try {
            long count = confirmedOrderService.getOrdersCountByStatus(status);
            return ResponseEntity.ok(Map.of("count", count));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    // === DELIVERY ENTRY ENDPOINTS ===
    
    // Get all delivery entries
    @GetMapping("/entries")
    public ResponseEntity<?> getAllDeliveryEntries() {
        try {
            List<DeliveryEntry> entries = deliveryEntryService.getAllDeliveryEntries();
            return ResponseEntity.ok(entries);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to fetch delivery entries", "message", e.getMessage()));
        }
    }
    
    // Create delivery entry from order (simple validation)
    @PostMapping("/entries")
    public ResponseEntity<?> createDeliveryEntry(@RequestBody Map<String, Long> request) {
        try {
            Long orderId = request.get("orderId");
            if (orderId == null) {
                return ResponseEntity.badRequest()
                    .body(Map.of("error", "Order ID is required"));
            }
            
            DeliveryEntry entry = deliveryEntryService.createDeliveryEntry(orderId);
            return ResponseEntity.ok(entry);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", "Failed to create delivery entry", "message", e.getMessage()));
        }
    }
    
    // Update delivery entry status
    @PutMapping("/entries/{deliveryId}/status")
    public ResponseEntity<?> updateDeliveryStatus(@PathVariable Long deliveryId, @RequestBody Map<String, String> request) {
        try {
            String newStatus = request.get("status");
            if (newStatus == null || newStatus.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(Map.of("error", "Status is required"));
            }
            
            DeliveryEntry entry = deliveryEntryService.updateDeliveryStatus(deliveryId, newStatus);
            return ResponseEntity.ok(entry);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", "Failed to update delivery status", "message", e.getMessage()));
        }
    }
    
    // Delete delivery entry
    @DeleteMapping("/entries/{deliveryId}")
    public ResponseEntity<?> deleteDeliveryEntry(@PathVariable Long deliveryId) {
        try {
            deliveryEntryService.deleteDeliveryEntry(deliveryId);
            return ResponseEntity.ok(Map.of("message", "Delivery entry deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", "Failed to delete delivery entry", "message", e.getMessage()));
        }
    }

    // ROUTE MANAGEMENT ENDPOINTS
    
    // Get all routes
    @GetMapping("/routes")
    public ResponseEntity<List<Route>> getAllRoutes() {
        try {
            List<Route> routes = routeService.getAllRoutes();
            return ResponseEntity.ok(routes);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    // Generate optimal route
    @PostMapping("/routes/generate")
    public ResponseEntity<?> generateOptimalRoute() {
        try {
            Route route = routeService.generateOptimalRoute();
            return ResponseEntity.ok(route);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", "Failed to generate route", "message", e.getMessage()));
        }
    }
    
    // Get route with stops
    @GetMapping("/routes/{routeId}")
    public ResponseEntity<?> getRouteWithStops(@PathVariable Long routeId) {
        try {
            Map<String, Object> routeData = routeService.getRouteWithStops(routeId);
            return ResponseEntity.ok(routeData);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", "Failed to get route", "message", e.getMessage()));
        }
    }
    
    // Update route status
    @PutMapping("/routes/{routeId}/status")
    public ResponseEntity<?> updateRouteStatus(@PathVariable Long routeId, @RequestBody Map<String, String> request) {
        try {
            String status = request.get("status");
            Route route = routeService.updateRouteStatus(routeId, status);
            return ResponseEntity.ok(route);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", "Failed to update route status", "message", e.getMessage()));
        }
    }
    
    // Delete route
    @DeleteMapping("/routes/{routeId}")
    public ResponseEntity<?> deleteRoute(@PathVariable Long routeId) {
        try {
            routeService.deleteRoute(routeId);
            return ResponseEntity.ok(Map.of("message", "Route deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", "Failed to delete route", "message", e.getMessage()));
        }
    }

    // Test endpoint
    @GetMapping("/test")
    public ResponseEntity<Map<String, String>> testEndpoint() {
        return ResponseEntity.ok(Map.of("message", "Delivery API is working!", "timestamp", String.valueOf(System.currentTimeMillis())));
    }
}
