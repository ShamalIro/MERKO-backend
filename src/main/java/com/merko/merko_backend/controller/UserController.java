package com.merko.merko_backend.controller;

import com.merko.merko_backend.dto.UserRegistrationDto;
import com.merko.merko_backend.dto.UserApprovalDto;
import com.merko.merko_backend.entity.User;
import com.merko.merko_backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174", "http://localhost:3000"})
public class UserController {
    
    @Autowired
    private UserService userService;
    
    @GetMapping("/test")
    public ResponseEntity<?> test() {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "User controller is working!");
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/debug")
    public ResponseEntity<?> debugUsers() {
        try {
            List<User> users = userService.getAllUsers();
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("count", users.size());
            response.put("users", users.stream().map(this::createUserSummary).toList());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Error: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
    
    private Map<String, Object> createUserSummary(User user) {
        Map<String, Object> summary = new HashMap<>();
        summary.put("id", user.getId());
        summary.put("email", user.getEmail());
        summary.put("firstName", user.getFirstName());
        summary.put("lastName", user.getLastName());
        summary.put("role", user.getRole());
        summary.put("status", user.getStatus());
        return summary;
    }
    
    @GetMapping("/all")
    public ResponseEntity<?> getAllUsers() {
        try {
            List<User> users = userService.getAllUsers();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Users fetched successfully");
            response.put("count", users.size());
            response.put("users", users.stream().map(this::createUserResponse).toList());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to fetch users: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
    
    @PostMapping("/create")
    public ResponseEntity<?> createUser(@RequestBody UserRegistrationDto registrationDto) {
        try {
            User user = userService.createUser(registrationDto);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "User created successfully!");
            response.put("user", createUserResponse(user));
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody UserRegistrationDto registrationDto) {
        try {
            User user = userService.updateUser(id, registrationDto);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "User updated successfully!");
            response.put("user", createUserResponse(user));
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        try {
            userService.deleteUser(id);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "User deleted successfully!");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    private Map<String, Object> createUserResponse(User user) {
        Map<String, Object> userMap = new HashMap<>();
        userMap.put("id", user.getId());
        userMap.put("firstName", user.getFirstName());
        userMap.put("lastName", user.getLastName());
        userMap.put("email", user.getEmail());
        userMap.put("phoneNumber", user.getPhoneNumber());
        userMap.put("companyName", user.getCompanyName());
        userMap.put("businessType", user.getBusinessType());
        userMap.put("role", user.getRole());
        userMap.put("status", user.getStatus());
        userMap.put("registrationDate", user.getRegistrationDate());
        userMap.put("approvalDate", user.getApprovalDate());
        userMap.put("approvedBy", user.getApprovedBy());
        userMap.put("rejectionReason", user.getRejectionReason());
        return userMap;
    }
}
