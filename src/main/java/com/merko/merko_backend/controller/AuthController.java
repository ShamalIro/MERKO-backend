package com.merko.merko_backend.controller;

import com.merko.merko_backend.dto.AdminLoginDto;
import com.merko.merko_backend.dto.LoginDto;
import com.merko.merko_backend.entity.Admin;
import com.merko.merko_backend.entity.User;
import com.merko.merko_backend.entity.UserRole;
import com.merko.merko_backend.entity.UserStatus;
import com.merko.merko_backend.service.AdminService;
import com.merko.merko_backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174", "http://localhost:3000"}, 
            methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS},
            allowedHeaders = "*",
            allowCredentials = "true")
public class AuthController {
    
    @Autowired
    private AdminService adminService;
    
    @Autowired
    private UserService userService;
    
    @RequestMapping(value = "/login", method = RequestMethod.OPTIONS)
    public ResponseEntity<?> optionsLogin() {
        return ResponseEntity.ok().build();
    }
    
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDto loginDto) {
        try {
            System.out.println("Auth login attempt for email: " + loginDto.getEmail() + ", role: " + loginDto.getRole());
            
            // Check if this is an admin login attempt
            if ("ADMIN".equalsIgnoreCase(loginDto.getRole())) {
                try {
                    // Convert to AdminLoginDto for admin service
                    AdminLoginDto adminLoginDto = new AdminLoginDto(loginDto.getEmail(), loginDto.getPassword());
                    Admin admin = adminService.loginAdmin(adminLoginDto);
                    if (admin != null) {
                        Map<String, Object> response = new HashMap<>();
                        response.put("success", true);
                        response.put("message", "Admin login successful");
                        response.put("user", createAdminResponse(admin));
                        response.put("role", "ADMIN");
                        return ResponseEntity.ok(response);
                    }
                } catch (RuntimeException e) {
                    System.out.println("Admin login failed: " + e.getMessage());
                }
            }
            
            // Try to authenticate as general user (supplier/merchant/delivery)
            try {
                Optional<User> userOpt = userService.findByEmail(loginDto.getEmail());
                if (userOpt.isPresent()) {
                    User user = userOpt.get();
                    
                    // Check if the user's role matches the requested role
                    UserRole userRole = user.getRole();
                    String requestedRole = loginDto.getRole();
                    
                    // Normalize role names for comparison
                    if ("DELIVERY".equalsIgnoreCase(requestedRole) || "Delivery Person".equalsIgnoreCase(requestedRole)) {
                        requestedRole = "DELIVERY";
                    }
                    
                    System.out.println("User role in DB: " + userRole + ", Requested role: " + requestedRole);
                    
                    // Validate role match
                    if (!userRole.name().equalsIgnoreCase(requestedRole)) {
                        Map<String, Object> response = new HashMap<>();
                        response.put("success", false);
                        response.put("message", "Role mismatch. Your account is registered as " + userRole + " but you selected " + requestedRole);
                        return ResponseEntity.status(400).body(response);
                    }
                    
                    // Check if user is approved
                    if (user.getStatus() != UserStatus.APPROVED) {
                        Map<String, Object> response = new HashMap<>();
                        response.put("success", false);
                        response.put("message", "Account not approved. Status: " + user.getStatus() + ". Please wait for admin approval.");
                        return ResponseEntity.status(403).body(response);
                    }
                    
                    // Validate password
                    if (userService.validatePassword(loginDto.getPassword(), user.getPassword())) {
                        Map<String, Object> response = new HashMap<>();
                        response.put("success", true);
                        response.put("message", "User login successful");
                        response.put("user", createUserResponse(user));
                        response.put("role", user.getRole().name());
                        return ResponseEntity.ok(response);
                    } else {
                        System.out.println("Password validation failed for user: " + loginDto.getEmail());
                    }
                } else {
                    System.out.println("User not found: " + loginDto.getEmail());
                }
            } catch (Exception e) {
                System.out.println("User login failed: " + e.getMessage());
                e.printStackTrace();
            }
            
            // If all authentication attempts fail
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Invalid email or password");
            return ResponseEntity.status(401).body(response);
            
        } catch (Exception e) {
            System.out.println("Login error: " + e.getMessage());
            e.printStackTrace();
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Login failed: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
    
    private Map<String, Object> createAdminResponse(Admin admin) {
        Map<String, Object> adminMap = new HashMap<>();
        adminMap.put("id", admin.getId());
        adminMap.put("email", admin.getEmail());
        adminMap.put("firstName", admin.getFirstName());
        adminMap.put("lastName", admin.getLastName());
        adminMap.put("role", admin.getRole() != null ? admin.getRole() : "ADMIN");
        return adminMap;
    }
    
    private Map<String, Object> createUserResponse(User user) {
        Map<String, Object> userMap = new HashMap<>();
        userMap.put("id", user.getId());
        userMap.put("email", user.getEmail());
        userMap.put("firstName", user.getFirstName());
        userMap.put("lastName", user.getLastName());
        userMap.put("role", user.getRole().name());
        userMap.put("phoneNumber", user.getPhoneNumber());
        userMap.put("companyName", user.getCompanyName());
        return userMap;
    }
}