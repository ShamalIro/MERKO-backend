package com.merko.merko_backend.controller;

import com.merko.merko_backend.entity.Merchant;
import com.merko.merko_backend.service.MerchantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/merchants")
@CrossOrigin(origins = "*")
public class MerchantController {
    
    @Autowired
    private MerchantService merchantService;
    
    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> registerMerchant(@RequestBody Merchant merchant) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Validate required fields
            if (merchant.getCompanyName() == null || merchant.getCompanyName().trim().isEmpty()) {
                response.put("success", false);
                response.put("message", "Company name is required");
                return ResponseEntity.badRequest().body(response);
            }
            
            if (merchant.getContactPersonName() == null || merchant.getContactPersonName().trim().isEmpty()) {
                response.put("success", false);
                response.put("message", "Contact person name is required");
                return ResponseEntity.badRequest().body(response);
            }
            
            if (merchant.getEmail() == null || merchant.getEmail().trim().isEmpty()) {
                response.put("success", false);
                response.put("message", "Email is required");
                return ResponseEntity.badRequest().body(response);
            }
            
            if (merchant.getPhoneNumber() == null || merchant.getPhoneNumber().trim().isEmpty()) {
                response.put("success", false);
                response.put("message", "Phone number is required");
                return ResponseEntity.badRequest().body(response);
            }
            
            if (merchant.getPassword() == null || merchant.getPassword().trim().isEmpty()) {
                response.put("success", false);
                response.put("message", "Password is required");
                return ResponseEntity.badRequest().body(response);
            }
            
            Merchant savedMerchant = merchantService.registerMerchant(merchant);
            response.put("success", true);
            response.put("message", "Merchant registered successfully");
            response.put("merchantId", savedMerchant.getMerchantId());
            return ResponseEntity.ok(response);
            
        } catch (RuntimeException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Registration failed: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    @GetMapping("/all")
    public ResponseEntity<?> getAllMerchants() {
        try {
            List<Merchant> merchants = merchantService.getAllMerchants();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Merchants fetched successfully");
            response.put("count", merchants.size());
            response.put("merchants", merchants.stream().map(this::createMerchantResponse).toList());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to fetch merchants: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Merchant> getMerchantById(@PathVariable Long id) {
        try {
            Optional<Merchant> merchant = merchantService.getMerchantById(id);
            return merchant.map(ResponseEntity::ok)
                          .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/email/{email}")
    public ResponseEntity<Merchant> getMerchantByEmail(@PathVariable String email) {
        try {
            Optional<Merchant> merchant = merchantService.getMerchantByEmail(email);
            return merchant.map(ResponseEntity::ok)
                          .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/status/{status}")
    public ResponseEntity<List<Merchant>> getMerchantsByStatus(@PathVariable String status) {
        try {
            List<Merchant> merchants = merchantService.getMerchantsByStatus(status);
            return ResponseEntity.ok(merchants);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/pending")
    public ResponseEntity<List<Merchant>> getPendingApprovalMerchants() {
        try {
            List<Merchant> merchants = merchantService.getPendingApprovalMerchants();
            return ResponseEntity.ok(merchants);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/active")
    public ResponseEntity<List<Merchant>> getActiveMerchants() {
        try {
            List<Merchant> merchants = merchantService.getActiveMerchants();
            return ResponseEntity.ok(merchants);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/search")
    public ResponseEntity<List<Merchant>> searchMerchantsByCompanyName(@RequestParam String companyName) {
        try {
            List<Merchant> merchants = merchantService.searchMerchantsByCompanyName(companyName);
            return ResponseEntity.ok(merchants);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @PutMapping("/{id}/approve")
    public ResponseEntity<Map<String, Object>> approveMerchant(@PathVariable Long id, @RequestParam Long approvedBy) {
        Map<String, Object> response = new HashMap<>();
        try {
            Merchant approvedMerchant = merchantService.approveMerchant(id, approvedBy);
            response.put("success", true);
            response.put("message", "Merchant approved successfully");
            response.put("merchant", approvedMerchant);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    @PutMapping("/{id}/reject")
    public ResponseEntity<Map<String, Object>> rejectMerchant(@PathVariable Long id, @RequestParam String reason) {
        Map<String, Object> response = new HashMap<>();
        try {
            Merchant rejectedMerchant = merchantService.rejectMerchant(id, reason);
            response.put("success", true);
            response.put("message", "Merchant rejected successfully");
            response.put("merchant", rejectedMerchant);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateMerchant(@PathVariable Long id, @RequestBody Merchant merchant) {
        Map<String, Object> response = new HashMap<>();
        try {
            Merchant updatedMerchant = merchantService.updateMerchant(id, merchant);
            response.put("success", true);
            response.put("message", "Merchant updated successfully");
            response.put("merchant", updatedMerchant);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteMerchant(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        try {
            merchantService.deleteMerchant(id);
            response.put("success", true);
            response.put("message", "Merchant deleted successfully");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    @GetMapping("/count/{status}")
    public ResponseEntity<Map<String, Object>> countMerchantsByStatus(@PathVariable String status) {
        try {
            long count = merchantService.countMerchantsByStatus(status);
            Map<String, Object> response = new HashMap<>();
            response.put("status", status);
            response.put("count", count);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/exists/email/{email}")
    public ResponseEntity<Map<String, Boolean>> checkEmailExists(@PathVariable String email) {
        try {
            boolean exists = merchantService.existsByEmail(email);
            Map<String, Boolean> response = new HashMap<>();
            response.put("exists", exists);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/exists/username/{username}")
    public ResponseEntity<Map<String, Boolean>> checkUsernameExists(@PathVariable String username) {
        try {
            boolean exists = merchantService.existsByUsername(username);
            Map<String, Boolean> response = new HashMap<>();
            response.put("exists", exists);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/login-status/{email}")
    public ResponseEntity<Map<String, Object>> getMerchantLoginStatus(@PathVariable String email) {
        try {
            String status = merchantService.getMerchantLoginStatus(email);
            boolean canLogin = merchantService.canMerchantLogin(email);
            
            Map<String, Object> response = new HashMap<>();
            response.put("email", email);
            response.put("loginStatus", status);
            response.put("canLogin", canLogin);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    private Map<String, Object> createMerchantResponse(Merchant merchant) {
        Map<String, Object> response = new HashMap<>();
        response.put("id", merchant.getId());
        response.put("companyName", merchant.getCompanyName());
        response.put("contactPersonName", merchant.getContactPersonName());
        response.put("email", merchant.getEmail());
        response.put("phoneNumber", merchant.getPhoneNumber());
        response.put("businessType", merchant.getBusinessType());
        response.put("businessRegistrationNumber", merchant.getBusinessRegistrationNumber());
        response.put("businessAddress", merchant.getBusinessAddress());
        response.put("status", merchant.getStatus());
        response.put("createdAt", merchant.getCreatedAt());
        response.put("updatedAt", merchant.getUpdatedAt());
        response.put("approvedAt", merchant.getApprovedAt());
        response.put("approvedBy", merchant.getApprovedBy());
        return response;
    }
}
