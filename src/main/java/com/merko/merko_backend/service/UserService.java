package com.merko.merko_backend.service;

import com.merko.merko_backend.dto.UserRegistrationDto;
import com.merko.merko_backend.dto.UserApprovalDto;
import com.merko.merko_backend.entity.User;
import com.merko.merko_backend.entity.UserRole;
import com.merko.merko_backend.entity.UserStatus;
import com.merko.merko_backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    public List<User> getAllUsers() {
        try {
            return userRepository.findAll();
        } catch (Exception e) {
            System.err.println("Database error fetching users: " + e.getMessage());
            // Return empty list if database has issues
            return new java.util.ArrayList<>();
        }
    }
    
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }
    
    public User createUser(UserRegistrationDto dto) {
        User user = new User();
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setEmail(dto.getEmail());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setPhoneNumber(dto.getPhoneNumber());
        user.setCompanyName(dto.getCompanyName());
        user.setBusinessType(dto.getBusinessType());
        user.setRole(dto.getRole());
        user.setStatus(UserStatus.APPROVED);
        user.setRegistrationDate(LocalDateTime.now());
        user.setApprovalDate(LocalDateTime.now());
        
        return userRepository.save(user);
    }
    
    public User updateUser(Long id, UserRegistrationDto dto) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("User not found"));
            
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setEmail(dto.getEmail());
        user.setPhoneNumber(dto.getPhoneNumber());
        user.setCompanyName(dto.getCompanyName());
        user.setBusinessType(dto.getBusinessType());
        user.setRole(dto.getRole());
        
        return userRepository.save(user);
    }
    
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
    
    public List<User> getUsersByRole(UserRole role) {
        return userRepository.findByRole(role);
    }
    
    public List<User> getUsersByStatus(UserStatus status) {
        return userRepository.findByStatus(status);
    }
    
    public List<User> getPendingUsers() {
        return userRepository.findByStatus(UserStatus.PENDING);
    }
    
    public User registerUser(UserRegistrationDto dto) {
        User user = new User();
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setEmail(dto.getEmail());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setPhoneNumber(dto.getPhoneNumber());
        user.setCompanyName(dto.getCompanyName());
        user.setBusinessType(dto.getBusinessType());
        user.setRole(dto.getRole());
        user.setStatus(UserStatus.PENDING);
        user.setRegistrationDate(LocalDateTime.now());
        
        return userRepository.save(user);
    }
    
    public User updateUserStatus(UserApprovalDto dto) {
        User user = userRepository.findById(dto.getUserId())
            .orElseThrow(() -> new RuntimeException("User not found"));
            
        user.setStatus(dto.getStatus());
        user.setApprovalDate(LocalDateTime.now());
        user.setApprovedBy(dto.getApprovedBy());
        user.setRejectionReason(dto.getRejectionReason());
        
        return userRepository.save(user);
    }
    
    // Authentication methods
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
    
    public boolean validatePassword(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }
}
