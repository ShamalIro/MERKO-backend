package com.merko.merko_backend.controller;

import com.merko.merko_backend.entity.Supplier;
import com.merko.merko_backend.service.SupplierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/suppliers")
@CrossOrigin(origins = "*")
public class SupplierController {
    
    @Autowired
    private SupplierService supplierService;
    
    // Register a new supplier
    @PostMapping("/register")
    public ResponseEntity<?> registerSupplier(@RequestBody Map<String, String> request) {
        try {
            String companyName = request.get("companyName");
            String contactPersonName = request.get("contactPersonName");
            String email = request.get("email");
            String phoneNumber = request.get("phoneNumber");
            String businessRegistrationNumber = request.get("businessRegistrationNumber");
            String password = request.get("password");
            String username = request.get("username");
            
            Supplier supplier = supplierService.registerSupplier(
                companyName, contactPersonName, email, phoneNumber, 
                businessRegistrationNumber, password, username
            );
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Supplier registered successfully! Please wait for admin approval.",
                "supplierId", supplier.getSupplierId(),
                "companyName", supplier.getCompanyName(),
                "status", supplier.getStatus()
            ));
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }
    
    // Get all suppliers
    @GetMapping
    public ResponseEntity<?> getAllSuppliers() {
        try {
            List<Supplier> suppliers = supplierService.getAllSuppliers();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Suppliers fetched successfully");
            response.put("count", suppliers.size());
            response.put("suppliers", suppliers.stream().map(this::createSupplierResponse).toList());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to fetch suppliers: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    // Get supplier by ID
    @GetMapping("/{supplierId}")
    public ResponseEntity<Supplier> getSupplierById(@PathVariable Long supplierId) {
        try {
            Optional<Supplier> supplier = supplierService.getSupplierById(supplierId);
            if (supplier.isPresent()) {
                return ResponseEntity.ok(supplier.get());
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    // Get suppliers by status
    @GetMapping("/status/{status}")
    public ResponseEntity<List<Supplier>> getSuppliersByStatus(@PathVariable String status) {
        try {
            List<Supplier> suppliers = supplierService.getSuppliersByStatus(status);
            return ResponseEntity.ok(suppliers);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    // Get pending approval suppliers
    @GetMapping("/pending-approval")
    public ResponseEntity<List<Supplier>> getPendingApprovalSuppliers() {
        try {
            List<Supplier> suppliers = supplierService.getPendingApprovalSuppliers();
            return ResponseEntity.ok(suppliers);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    // Get approved suppliers
    @GetMapping("/approved")
    public ResponseEntity<List<Supplier>> getApprovedSuppliers() {
        try {
            List<Supplier> suppliers = supplierService.getApprovedSuppliers();
            return ResponseEntity.ok(suppliers);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    // Approve supplier
    @PutMapping("/{supplierId}/approve")
    public ResponseEntity<?> approveSupplier(@PathVariable Long supplierId, @RequestBody Map<String, String> request) {
        try {
            String approvedBy = request.getOrDefault("approvedBy", "Admin");
            Supplier supplier = supplierService.approveSupplier(supplierId, approvedBy);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Supplier approved successfully",
                "supplier", supplier
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }
    
    // Reject supplier
    @PutMapping("/{supplierId}/reject")
    public ResponseEntity<?> rejectSupplier(@PathVariable Long supplierId, @RequestBody Map<String, String> request) {
        try {
            String rejectedBy = request.getOrDefault("rejectedBy", "Admin");
            Supplier supplier = supplierService.rejectSupplier(supplierId, rejectedBy);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Supplier rejected",
                "supplier", supplier
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }
    
    // Update supplier status
    @PutMapping("/{supplierId}/status")
    public ResponseEntity<?> updateSupplierStatus(@PathVariable Long supplierId, @RequestBody Map<String, String> request) {
        try {
            String newStatus = request.get("status");
            if (newStatus == null || newStatus.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Status is required"
                ));
            }
            
            Supplier supplier = supplierService.updateSupplierStatus(supplierId, newStatus);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Status updated successfully",
                "supplier", supplier
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }
    
    // Search suppliers by company name
    @GetMapping("/search")
    public ResponseEntity<List<Supplier>> searchSuppliers(@RequestParam String companyName) {
        try {
            List<Supplier> suppliers = supplierService.searchSuppliersByCompanyName(companyName);
            return ResponseEntity.ok(suppliers);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    // Delete supplier
    @DeleteMapping("/{supplierId}")
    public ResponseEntity<?> deleteSupplier(@PathVariable Long supplierId) {
        try {
            supplierService.deleteSupplier(supplierId);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Supplier deleted successfully"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    // Helper method to create supplier response format for Approvals Management
    private Map<String, Object> createSupplierResponse(Supplier supplier) {
        Map<String, Object> response = new HashMap<>();
        response.put("id", supplier.getSupplierId());
        response.put("companyName", supplier.getCompanyName());
        response.put("contactPersonName", supplier.getContactPersonName());
        response.put("email", supplier.getEmail());
        response.put("phoneNumber", supplier.getPhoneNumber());
        response.put("businessRegistrationNumber", supplier.getBusinessRegistrationNumber());
        response.put("status", supplier.getStatus());
        response.put("role", supplier.getRole());
        response.put("createdAt", supplier.getCreatedAt());
        response.put("updatedAt", supplier.getUpdatedAt());
        response.put("approvedAt", supplier.getApprovedAt());
        response.put("approvedBy", supplier.getApprovedBy());
        return response;
    }
}