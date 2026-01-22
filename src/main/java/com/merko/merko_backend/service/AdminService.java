package com.merko.merko_backend.service;

import com.merko.merko_backend.dto.AdminLoginDto;
import com.merko.merko_backend.entity.Admin;
import com.merko.merko_backend.repository.AdminRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class AdminService {
    
    @Autowired
    private AdminRepository adminRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    // Admin login authentication
    public Admin loginAdmin(AdminLoginDto loginDto) {
        if (loginDto.getEmail() == null || loginDto.getEmail().trim().isEmpty()) {
            throw new RuntimeException("Email is required");
        }
        
        if (loginDto.getPassword() == null || loginDto.getPassword().trim().isEmpty()) {
            throw new RuntimeException("Password is required");
        }
        
        // Find admin by email
        Optional<Admin> adminOpt = adminRepository.findByEmail(loginDto.getEmail());
        
        if (adminOpt.isEmpty()) {
            throw new RuntimeException("Invalid email or password");
        }
        
        Admin admin = adminOpt.get();
        
        // Check password - support both plain text and BCrypt for backward compatibility
        String storedPassword = admin.getPassword();
        boolean passwordMatch = false;
        
        // First try BCrypt
        try {
            passwordMatch = passwordEncoder.matches(loginDto.getPassword(), storedPassword);
        } catch (Exception e) {
            // If BCrypt fails, try plain text comparison
            passwordMatch = loginDto.getPassword().equals(storedPassword);
        }
        
        // If BCrypt didn't work, try plain text
        if (!passwordMatch) {
            passwordMatch = loginDto.getPassword().equals(storedPassword);
        }
        
        if (!passwordMatch) {
            throw new RuntimeException("Invalid email or password");
        }
        
        return admin;
    }
    
    // Create admin (for initial setup or adding new admins)
    public Admin createAdmin(String firstName, String lastName, String email, String password) {
        if (adminRepository.existsByEmail(email)) {
            throw new RuntimeException("Admin with this email already exists");
        }
        
        // Encode password using BCrypt
        String encodedPassword = passwordEncoder.encode(password);
        
        Admin admin = new Admin(email, encodedPassword);
        admin.setFirstName(firstName);
        admin.setLastName(lastName);
        admin.setRole("ADMIN");
        return adminRepository.save(admin);
    }
    
    // Get admin by email
    public Optional<Admin> findByEmail(String email) {
        return adminRepository.findByEmail(email);
    }
    
    // Check if admin exists
    public boolean adminExists(String email) {
        return adminRepository.existsByEmail(email);
    }
    
    // Check if any admin exists (for initial setup)
    public boolean anyAdminExists() {
        return adminRepository.count() > 0;
    }
}
