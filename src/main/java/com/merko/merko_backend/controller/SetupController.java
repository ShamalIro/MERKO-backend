package com.merko.merko_backend.controller;

import com.merko.merko_backend.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/setup")
@CrossOrigin(origins = "http://localhost:5173")
public class SetupController {
    
    @Autowired
    private AdminService adminService;
    
    // Check if system needs initial setup
    @GetMapping("/status")
    public ResponseEntity<?> getSetupStatus() {
        Map<String, Object> response = new HashMap<>();
        boolean needsSetup = !adminService.anyAdminExists();
        
        response.put("needsSetup", needsSetup);
        response.put("message", needsSetup ? "System needs initial admin setup" : "System is already configured");
        
        return ResponseEntity.ok(response);
    }
    
    // Create default admin (for initial setup - should be secured in production)
    @PostMapping("/create-default-admin")
    public ResponseEntity<?> createDefaultAdmin() {
        try {
            // Check if admin already exists
            if (adminService.adminExists("admin@merko.com")) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "Default admin already exists");
                return ResponseEntity.badRequest().body(response);
            }
            
            // Create default admin
            adminService.createAdmin("Admin", "User", "admin@merko.com", "admin123");
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Default admin created successfully! Email: admin@merko.com, Password: admin123");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            
            return ResponseEntity.badRequest().body(response);
        }
    }
}
