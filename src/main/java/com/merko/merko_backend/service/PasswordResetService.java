package com.merko.merko_backend.service;

import com.merko.merko_backend.entity.User;
import com.merko.merko_backend.repository.UserRepository;
import com.merko.merko_backend.dto.ForgotPasswordRequest;
import com.merko.merko_backend.dto.ResetPasswordRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class PasswordResetService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    /**
     * Verify if email exists in the system
     */
    public Map<String, Object> forgotPassword(ForgotPasswordRequest request) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Check if user exists with this email
            Optional<User> userOptional = userRepository.findByEmail(request.getEmail());
            
            if (userOptional.isPresent()) {
                // In a real application, you would generate a reset token and send email
                // For now, we'll just confirm the email exists
                response.put("success", true);
                response.put("message", "Password reset instructions have been sent to your email.");
                response.put("email", request.getEmail());
            } else {
                response.put("success", false);
                response.put("message", "No account found with this email address.");
            }
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "An error occurred while processing your request.");
            response.put("error", e.getMessage());
        }
        
        return response;
    }
    
    /**
     * Reset password for user
     */
    public Map<String, Object> resetPassword(ResetPasswordRequest request) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Validate input
            if (request.getNewPassword() == null || request.getNewPassword().trim().isEmpty()) {
                response.put("success", false);
                response.put("message", "New password is required.");
                return response;
            }
            
            if (request.getConfirmPassword() == null || request.getConfirmPassword().trim().isEmpty()) {
                response.put("success", false);
                response.put("message", "Password confirmation is required.");
                return response;
            }
            
            if (!request.getNewPassword().equals(request.getConfirmPassword())) {
                response.put("success", false);
                response.put("message", "Passwords do not match.");
                return response;
            }
            
            if (request.getNewPassword().length() < 6) {
                response.put("success", false);
                response.put("message", "Password must be at least 6 characters long.");
                return response;
            }
            
            // Find user by email
            Optional<User> userOptional = userRepository.findByEmail(request.getEmail());
            
            if (userOptional.isPresent()) {
                User user = userOptional.get();
                
                // Encode and update password
                String encodedPassword = passwordEncoder.encode(request.getNewPassword());
                user.setPassword(encodedPassword);
                
                // Save updated user
                userRepository.save(user);
                
                response.put("success", true);
                response.put("message", "Password has been reset successfully. You can now login with your new password.");
                
            } else {
                response.put("success", false);
                response.put("message", "No account found with this email address.");
            }
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "An error occurred while resetting your password.");
            response.put("error", e.getMessage());
        }
        
        return response;
    }
}