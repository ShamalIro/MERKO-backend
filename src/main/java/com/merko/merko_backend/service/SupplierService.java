package com.merko.merko_backend.service;

import com.merko.merko_backend.entity.Supplier;
import com.merko.merko_backend.entity.User;
import com.merko.merko_backend.entity.UserRole;
import com.merko.merko_backend.entity.UserStatus;
import com.merko.merko_backend.repository.SupplierRepository;
import com.merko.merko_backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class SupplierService {
    
    @Autowired
    private SupplierRepository supplierRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    // Register a new supplier
    public Supplier registerSupplier(String companyName, String contactPersonName, 
                                   String email, String phoneNumber, 
                                   String businessRegistrationNumber, String password, 
                                   String username) throws Exception {
        
        // Check if email already exists
        if (supplierRepository.existsByEmail(email)) {
            throw new Exception("Email is already registered");
        }
        
        // Check if username already exists (if provided)
        if (username != null && !username.trim().isEmpty() && supplierRepository.existsByUsername(username)) {
            throw new Exception("Username is already taken");
        }
        
        // Validate required fields
        if (companyName == null || companyName.trim().isEmpty()) {
            throw new Exception("Company name is required");
        }
        if (contactPersonName == null || contactPersonName.trim().isEmpty()) {
            throw new Exception("Contact person name is required");
        }
        if (email == null || email.trim().isEmpty()) {
            throw new Exception("Email is required");
        }
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            throw new Exception("Phone number is required");
        }
        if (password == null || password.trim().isEmpty()) {
            throw new Exception("Password is required");
        }
        
        // Create new supplier
        Supplier supplier = new Supplier();
        supplier.setCompanyName(companyName.trim());
        supplier.setContactPersonName(contactPersonName.trim());
        supplier.setEmail(email.trim().toLowerCase());
        supplier.setPhoneNumber(phoneNumber.trim());
        supplier.setBusinessRegistrationNumber(businessRegistrationNumber != null ? businessRegistrationNumber.trim() : null);
        supplier.setPassword(passwordEncoder.encode(password)); // Encrypt password
        supplier.setRole("SUPPLIER");
        supplier.setStatus("PENDING_APPROVAL");
        supplier.setCreatedAt(LocalDateTime.now());
        supplier.setUpdatedAt(LocalDateTime.now());
        
        // Set username if provided
        if (username != null && !username.trim().isEmpty()) {
            supplier.setUsername(username.trim());
        }
        
        return supplierRepository.save(supplier);
    }
    
    // Get all suppliers
    public List<Supplier> getAllSuppliers() {
        return supplierRepository.findAllByOrderByCreatedAtDesc();
    }
    
    // Get supplier by ID
    public Optional<Supplier> getSupplierById(Long supplierId) {
        return supplierRepository.findById(supplierId);
    }
    
    // Get supplier by email
    public Optional<Supplier> getSupplierByEmail(String email) {
        return supplierRepository.findByEmail(email);
    }
    
    // Get suppliers by status
    public List<Supplier> getSuppliersByStatus(String status) {
        return supplierRepository.findByStatus(status);
    }
    
    // Get pending approval suppliers
    public List<Supplier> getPendingApprovalSuppliers() {
        return supplierRepository.findPendingApprovalSuppliers();
    }
    
    // Get approved suppliers
    public List<Supplier> getApprovedSuppliers() {
        return supplierRepository.findApprovedSuppliers();
    }
    
    // Approve supplier
    public Supplier approveSupplier(Long supplierId, String approvedBy) throws Exception {
        Optional<Supplier> supplierOpt = supplierRepository.findById(supplierId);
        if (!supplierOpt.isPresent()) {
            throw new Exception("Supplier not found");
        }
        
        Supplier supplier = supplierOpt.get();
        supplier.setStatus("APPROVED");
        supplier.setApprovedAt(LocalDateTime.now());
        supplier.setApprovedBy(approvedBy);
        
        // Save updated supplier first
        Supplier savedSupplier = supplierRepository.save(supplier);
        
        // Transfer approved supplier to users table for login functionality
        transferSupplierToUsers(savedSupplier);
        
        System.out.println("Supplier " + supplier.getEmail() + " approved successfully and transferred to users table.");
        return savedSupplier;
    }
    
    /**
     * Transfer approved supplier data to users table for login functionality
     */
    public void transferSupplierToUsers(Supplier supplier) {
        try {
            // Check if user already exists in users table
            if (userRepository.existsByEmail(supplier.getEmail())) {
                System.out.println("User with email " + supplier.getEmail() + " already exists in users table. Skipping transfer.");
                return; // Already transferred
            }
            
            // Create new user from supplier data
            User user = new User();
            
            // Extract first and last name from contact person name
            String[] nameParts = supplier.getContactPersonName().split(" ", 2);
            user.setFirstName(nameParts[0]);
            user.setLastName(nameParts.length > 1 ? nameParts[1] : "");
            
            // Set user data from supplier
            user.setEmail(supplier.getEmail());
            user.setPassword(supplier.getPassword()); // Already encrypted
            user.setPhoneNumber(supplier.getPhoneNumber());
            user.setCompanyName(supplier.getCompanyName());
            user.setBusinessType("Supplier"); // Default for suppliers
            user.setRole(UserRole.SUPPLIER);
            user.setStatus(UserStatus.APPROVED);
            user.setRegistrationDate(supplier.getCreatedAt());
            user.setApprovalDate(supplier.getApprovedAt());
            user.setApprovedBy(supplier.getApprovedBy() != null ? supplier.getApprovedBy().toString() : "admin");
            
            // Save to users table
            userRepository.save(user);
            System.out.println("Successfully transferred supplier " + supplier.getEmail() + " to users table.");
            
        } catch (Exception e) {
            // Handle duplicate email error gracefully
            if (e.getMessage() != null && e.getMessage().contains("Duplicate entry")) {
                System.out.println("Duplicate email detected for " + supplier.getEmail() + ". User already exists in users table.");
                // Check if the user actually exists now and update if needed
                Optional<User> existingUser = userRepository.findByEmail(supplier.getEmail());
                if (existingUser.isPresent()) {
                    User user = existingUser.get();
                    // Update the existing user's status to APPROVED if it's not already
                    if (user.getStatus() != UserStatus.APPROVED) {
                        user.setStatus(UserStatus.APPROVED);
                        user.setApprovalDate(supplier.getApprovedAt());
                        user.setApprovedBy(supplier.getApprovedBy() != null ? supplier.getApprovedBy().toString() : "admin");
                        userRepository.save(user);
                        System.out.println("Updated existing user " + supplier.getEmail() + " status to APPROVED.");
                    }
                }
                return; // Don't throw error, just continue
            } else {
                // Re-throw other exceptions
                throw new RuntimeException("Failed to transfer supplier to users table: " + e.getMessage(), e);
            }
        }
    }
    
    // Reject supplier
    public Supplier rejectSupplier(Long supplierId, String rejectedBy) throws Exception {
        Optional<Supplier> supplierOpt = supplierRepository.findById(supplierId);
        if (!supplierOpt.isPresent()) {
            throw new Exception("Supplier not found");
        }
        
        Supplier supplier = supplierOpt.get();
        supplier.setStatus("REJECTED");
        supplier.setApprovedBy(rejectedBy);
        
        // Note: Rejected suppliers are NOT transferred to users table
        System.out.println("Supplier " + supplier.getEmail() + " rejected. Status set to REJECTED.");
        return supplierRepository.save(supplier);
    }
    
    // Update supplier status
    public Supplier updateSupplierStatus(Long supplierId, String newStatus) throws Exception {
        Optional<Supplier> supplierOpt = supplierRepository.findById(supplierId);
        if (!supplierOpt.isPresent()) {
            throw new Exception("Supplier not found");
        }
        
        Supplier supplier = supplierOpt.get();
        supplier.setStatus(newStatus);
        
        return supplierRepository.save(supplier);
    }
    
    // Search suppliers by company name
    public List<Supplier> searchSuppliersByCompanyName(String companyName) {
        return supplierRepository.findByCompanyNameContainingIgnoreCase(companyName);
    }
    
    // Delete supplier
    public void deleteSupplier(Long supplierId) throws Exception {
        if (!supplierRepository.existsById(supplierId)) {
            throw new Exception("Supplier not found");
        }
        supplierRepository.deleteById(supplierId);
    }
}