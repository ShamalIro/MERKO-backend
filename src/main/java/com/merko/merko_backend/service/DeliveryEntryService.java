package com.merko.merko_backend.service;

import com.merko.merko_backend.entity.DeliveryEntry;
import com.merko.merko_backend.entity.ConfirmedOrder;
import com.merko.merko_backend.entity.RouteStop;
import com.merko.merko_backend.repository.DeliveryEntryRepository;
import com.merko.merko_backend.repository.ConfirmedOrderRepository;
import com.merko.merko_backend.repository.RouteStopRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
public class DeliveryEntryService {
    
    @Autowired
    private DeliveryEntryRepository deliveryEntryRepository;
    
    @Autowired
    private ConfirmedOrderRepository confirmedOrderRepository;
    
    @Autowired
    private RouteStopRepository routeStopRepository;
    
    // Get all delivery entries
    public List<DeliveryEntry> getAllDeliveryEntries() {
        return deliveryEntryRepository.findAllByOrderByCreatedAtDesc();
    }
    
    // Create delivery entry with simple validation
    public DeliveryEntry createDeliveryEntry(Long orderId) throws Exception {
        // Validation 1: Check if order exists
        Optional<ConfirmedOrder> orderOpt = confirmedOrderRepository.findById(orderId);
        if (!orderOpt.isPresent()) {
            throw new Exception("Order not found with ID: " + orderId);
        }
        
        ConfirmedOrder order = orderOpt.get();
        
        // Validation 2: Check if order status is "Ready to Pick"
        if (!"Ready to Pick".equals(order.getStatus())) {
            throw new Exception("Order must be 'Ready to Pick' to assign for delivery. Current status: " + order.getStatus());
        }
        
        // Validation 3: Check if order is already assigned
        if (deliveryEntryRepository.existsByOrderId(orderId)) {
            throw new Exception("Order is already assigned for delivery");
        }
        
        // Validation 4: Check required fields
        if (order.getMerchantName() == null || order.getMerchantName().trim().isEmpty()) {
            throw new Exception("Merchant name is required");
        }
        
        if (order.getSupplierName() == null || order.getSupplierName().trim().isEmpty()) {
            throw new Exception("Supplier name is required");
        }
        
        // Create and save delivery entry
        DeliveryEntry deliveryEntry = new DeliveryEntry(
            orderId,
            order.getMerchantName(),
            order.getSupplierName(),
            order.getDeliveryAddress()
        );
        
        DeliveryEntry savedEntry = deliveryEntryRepository.save(deliveryEntry);
        
        // Update confirmed order status to "Assigned"
        order.setStatus("Assigned");
        confirmedOrderRepository.save(order);
        
        return savedEntry;
    }
    
    // Update delivery entry status with validation
    public DeliveryEntry updateDeliveryStatus(Long deliveryId, String newStatus) throws Exception {
        Optional<DeliveryEntry> entryOpt = deliveryEntryRepository.findById(deliveryId);
        if (!entryOpt.isPresent()) {
            throw new Exception("Delivery entry not found with ID: " + deliveryId);
        }
        
        // Simple status validation
        if (newStatus == null || newStatus.trim().isEmpty()) {
            throw new Exception("Status cannot be empty");
        }
        
        String[] validStatuses = {"Ready for delivery", "Out for delivery", "Delivered", "Failed delivery", "Returned"};
        boolean isValidStatus = false;
        for (String status : validStatuses) {
            if (status.equals(newStatus)) {
                isValidStatus = true;
                break;
            }
        }
        
        if (!isValidStatus) {
            throw new Exception("Invalid status. Valid statuses are: Ready for delivery, Out for delivery, Delivered, Failed delivery, Returned");
        }
        
        DeliveryEntry entry = entryOpt.get();
        entry.setStatus(newStatus);
        return deliveryEntryRepository.save(entry);
    }
    
    // Delete delivery entry with comprehensive constraint handling
    @Transactional
    public void deleteDeliveryEntry(Long deliveryId) throws Exception {
        // Check if delivery entry exists
        if (!deliveryEntryRepository.existsById(deliveryId)) {
            throw new Exception("Delivery entry not found with ID: " + deliveryId);
        }
        
        // Get the delivery entry to check its current status
        Optional<DeliveryEntry> entryOpt = deliveryEntryRepository.findById(deliveryId);
        if (!entryOpt.isPresent()) {
            throw new Exception("Delivery entry not found with ID: " + deliveryId);
        }
        
        DeliveryEntry entry = entryOpt.get();
        
        // Allow deletion of any status - removed business rule restriction
        System.out.println("Preparing to delete delivery entry " + deliveryId + " with status: " + entry.getStatus());
        
        try {
            // Step 1: Handle route stops constraint
            List<RouteStop> routeStops = routeStopRepository.findByDeliveryEntryId(deliveryId);
            
            if (!routeStops.isEmpty()) {
                // Check if any route stops are in 'visited' status (audit trail protection)
                boolean hasVisitedStops = routeStops.stream()
                    .anyMatch(stop -> stop.getStatus() == RouteStop.StopStatus.visited);
                
                if (hasVisitedStops) {
                    throw new Exception("Cannot delete delivery entry because it has visited route stops. This delivery has been processed and cannot be removed for audit purposes.");
                }
                
                // Log the cleanup operation
                System.out.println("Found " + routeStops.size() + " route stops for delivery " + deliveryId + ". Cleaning up...");
                
                // Delete all associated route stops first
                routeStopRepository.deleteByDeliveryEntryId(deliveryId);
                
                // Verify cleanup was successful
                List<RouteStop> remainingStops = routeStopRepository.findByDeliveryEntryId(deliveryId);
                if (!remainingStops.isEmpty()) {
                    throw new Exception("Failed to clean up route stops. " + remainingStops.size() + " stops remain.");
                }
                
                System.out.println("Successfully cleaned up route stops for delivery " + deliveryId);
            }
            
            // Step 2: Delete the delivery entry
            deliveryEntryRepository.deleteById(deliveryId);
            
            // Step 3: Verify deletion was successful
            if (deliveryEntryRepository.existsById(deliveryId)) {
                throw new Exception("Delivery entry deletion failed. Entry still exists in database.");
            }
            
            System.out.println("Successfully deleted delivery entry " + deliveryId);
            
        } catch (Exception e) {
            String errorMessage = e.getMessage();
            
            // Re-throw our custom business rule exceptions as-is
            if (errorMessage.contains("has visited route stops") ||
                errorMessage.contains("Delivery entry not found") ||
                errorMessage.contains("Failed to clean up route stops") ||
                errorMessage.contains("deletion failed")) {
                throw e;
            }
            
            // Handle specific database constraint errors
            if (errorMessage != null) {
                String lowerError = errorMessage.toLowerCase();
                
                if (lowerError.contains("foreign key") && lowerError.contains("route")) {
                    throw new Exception("Foreign key constraint: This delivery is still referenced by route stops. Please remove it from all routes first, then try again.");
                } else if (lowerError.contains("constraint") && lowerError.contains("route_stops")) {
                    throw new Exception("Database constraint: Route stops are preventing deletion. All associated route stops must be removed first.");
                } else if (lowerError.contains("integrity constraint")) {
                    throw new Exception("Data integrity constraint: This delivery entry is referenced by other records and cannot be deleted.");
                } else if (lowerError.contains("constraint")) {
                    throw new Exception("Database constraint violation: " + errorMessage);
                } else if (lowerError.contains("foreign key")) {
                    throw new Exception("Foreign key constraint: This delivery entry is referenced by other records: " + errorMessage);
                }
            }
            
            // Generic fallback error
            throw new Exception("Failed to delete delivery entry: " + errorMessage);
        }
    }
}
