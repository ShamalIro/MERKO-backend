package com.merko.merko_backend.controller;

import com.merko.merko_backend.dto.ForgotPasswordRequest;
import com.merko.merko_backend.dto.ResetPasswordRequest;
import com.merko.merko_backend.service.PasswordResetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:5173")
public class PasswordResetController {
    
    @Autowired
    private PasswordResetService passwordResetService;
    
    /**
     * Forgot password endpoint - verify email exists
     */
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody ForgotPasswordRequest request) {
        try {
            Map<String, Object> response = passwordResetService.forgotPassword(request);
            
            if ((Boolean) response.get("success")) {
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.badRequest().body(response);
            }
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "An error occurred while processing your request.",
                "error", e.getMessage()
            ));
        }
    }
    
    /**
     * Reset password endpoint - update user password
     */
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequest request) {
        try {
            Map<String, Object> response = passwordResetService.resetPassword(request);
            
            if ((Boolean) response.get("success")) {
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.badRequest().body(response);
            }
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "An error occurred while resetting your password.",
                "error", e.getMessage()
            ));
        }
    }
}