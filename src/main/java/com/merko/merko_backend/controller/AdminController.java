package com.merko.merko_backend.controller;

import com.merko.merko_backend.dto.AdminLoginDto;
import com.merko.merko_backend.entity.Admin;
import com.merko.merko_backend.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174", "http://localhost:3000"}, 
            methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS},
            allowedHeaders = "*",
            allowCredentials = "true")
public class AdminController {
    
    @Autowired
    private AdminService adminService;
    
    @GetMapping("/test")
    public ResponseEntity<?> test() {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Admin controller is working!");
        return ResponseEntity.ok(response);
    }
    
    @RequestMapping(value = "/login", method = RequestMethod.OPTIONS)
    public ResponseEntity<?> optionsLogin() {
        return ResponseEntity.ok().build();
    }
    
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AdminLoginDto loginDto) {
        try {
            Admin admin = adminService.loginAdmin(loginDto);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Login successful");
            response.put("admin", createAdminResponse(admin));
            response.put("token", generateToken(admin)); // Simple token for now
            response.put("role", "SUPER_ADMIN");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    private Map<String, Object> createAdminResponse(Admin admin) {
        Map<String, Object> response = new HashMap<>();
        response.put("id", admin.getId());
        response.put("email", admin.getEmail());
        response.put("firstName", admin.getFirstName());
        response.put("lastName", admin.getLastName());
        response.put("role", admin.getRole());
        response.put("status", "ACTIVE"); // Default status since Admin entity doesn't have status field
        return response;
    }
    
    private String generateToken(Admin admin) {
        // Simple token generation - in production, use JWT
        return "ADMIN_TOKEN_" + admin.getId() + "_" + System.currentTimeMillis();
    }
}