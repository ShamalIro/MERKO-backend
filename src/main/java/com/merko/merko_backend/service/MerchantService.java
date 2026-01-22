package com.merko.merko_backend.service;

import com.merko.merko_backend.entity.Merchant;
import com.merko.merko_backend.entity.User;
import com.merko.merko_backend.entity.UserRole;
import com.merko.merko_backend.entity.UserStatus;
import com.merko.merko_backend.repository.MerchantRepository;
import com.merko.merko_backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class MerchantService {
    
    @Autowired
    private MerchantRepository merchantRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    public Merchant registerMerchant(Merchant merchant) {
        try {
            // Check if email already exists
            if (merchantRepository.existsByEmail(merchant.getEmail())) {
                throw new RuntimeException("Email already exists");
            }
            
            // Check if username already exists (if provided)
            if (merchant.getUsername() != null && !merchant.getUsername().trim().isEmpty()) {
                if (merchantRepository.existsByUsername(merchant.getUsername())) {
                    throw new RuntimeException("Username already exists");
                }
            }
            
            // Encrypt password
            merchant.setPassword(passwordEncoder.encode(merchant.getPassword()));
            
            // Set default values
            merchant.setRole("MERCHANT");
            merchant.setStatus("PENDING_APPROVAL");
            merchant.setCreatedAt(LocalDateTime.now());
            merchant.setUpdatedAt(LocalDateTime.now());
            
            // Set default business type if not provided
            if (merchant.getBusinessType() == null || merchant.getBusinessType().trim().isEmpty()) {
                merchant.setBusinessType("Retail");
            }
            
            return merchantRepository.save(merchant);
        } catch (Exception e) {
            throw new RuntimeException("Failed to register merchant: " + e.getMessage());
        }
    }
    
    public List<Merchant> getAllMerchants() {
        return merchantRepository.findAll();
    }
    
    public Optional<Merchant> getMerchantById(Long id) {
        return merchantRepository.findById(id);
    }
    
    public Optional<Merchant> getMerchantByEmail(String email) {
        return merchantRepository.findByEmail(email);
    }
    
    public Optional<Merchant> getMerchantByUsername(String username) {
        return merchantRepository.findByUsername(username);
    }
    
    public List<Merchant> getMerchantsByStatus(String status) {
        return merchantRepository.findByStatus(status);
    }
    
    public List<Merchant> getPendingApprovalMerchants() {
        return merchantRepository.findPendingApprovalMerchants();
    }
    
    public List<Merchant> getActiveMerchants() {
        return merchantRepository.findActiveMerchants();
    }
    
    public List<Merchant> getMerchantsByBusinessType(String businessType) {
        return merchantRepository.findByBusinessType(businessType);
    }
    
    public List<Merchant> searchMerchantsByCompanyName(String companyName) {
        return merchantRepository.findByCompanyNameContainingIgnoreCase(companyName);
    }
    
    public Merchant approveMerchant(Long merchantId, Long approvedBy) {
        Optional<Merchant> merchantOpt = merchantRepository.findById(merchantId);
        if (merchantOpt.isPresent()) {
            Merchant merchant = merchantOpt.get();
            
            // Check if merchant is already approved
            if ("APPROVED".equals(merchant.getStatus()) || "ACTIVE".equals(merchant.getStatus())) {
                System.out.println("Merchant " + merchant.getEmail() + " is already approved.");
                // Ensure consistent status
                if ("ACTIVE".equals(merchant.getStatus())) {
                    merchant.setStatus("APPROVED");
                    merchantRepository.save(merchant);
                }
                return merchant; // Already approved, return as-is
            }
            
            // Check if merchant is in a state that can be approved
            if (!"PENDING_APPROVAL".equals(merchant.getStatus())) {
                throw new RuntimeException("Merchant cannot be approved. Current status: " + merchant.getStatus());
            }
            
            // Update merchant status to APPROVED (consistent with suppliers)
            merchant.setStatus("APPROVED");
            merchant.setApprovedAt(LocalDateTime.now());
            merchant.setApprovedBy(approvedBy);
            merchant.setUpdatedAt(LocalDateTime.now());
            
            // Save updated merchant first
            Merchant savedMerchant = merchantRepository.save(merchant);
            
            // Transfer approved merchant to users table for login functionality
            transferMerchantToUsers(savedMerchant);
            
            System.out.println("Merchant " + merchant.getEmail() + " approved successfully and transferred to users table.");
            return savedMerchant;
        }
        throw new RuntimeException("Merchant not found");
    }
    
    /**
     * Transfer approved merchant data to users table for login functionality
     */
    private void transferMerchantToUsers(Merchant merchant) {
        try {
            // Check if user already exists in users table
            if (userRepository.existsByEmail(merchant.getEmail())) {
                System.out.println("User with email " + merchant.getEmail() + " already exists in users table. Skipping transfer.");
                return; // Already transferred
            }
            
            // Create new user from merchant data
            User user = new User();
            
            // Extract first and last name from contact person name
            String[] nameParts = merchant.getContactPersonName().split(" ", 2);
            user.setFirstName(nameParts[0]);
            user.setLastName(nameParts.length > 1 ? nameParts[1] : "");
            
            // Set user data from merchant
            user.setEmail(merchant.getEmail());
            user.setPassword(merchant.getPassword()); // Already encrypted
            user.setPhoneNumber(merchant.getPhoneNumber());
            user.setCompanyName(merchant.getCompanyName());
            user.setBusinessType(merchant.getBusinessType());
            user.setRole(UserRole.MERCHANT);
            user.setStatus(UserStatus.APPROVED);
            user.setRegistrationDate(merchant.getCreatedAt());
            user.setApprovalDate(merchant.getApprovedAt());
            user.setApprovedBy(merchant.getApprovedBy() != null ? merchant.getApprovedBy().toString() : "admin");
            
            // Save to users table
            userRepository.save(user);
            System.out.println("Successfully transferred merchant " + merchant.getEmail() + " to users table.");
            
        } catch (Exception e) {
            // Handle duplicate email error gracefully
            if (e.getMessage() != null && e.getMessage().contains("Duplicate entry")) {
                System.out.println("Duplicate email detected for " + merchant.getEmail() + ". User already exists in users table.");
                // Check if the user actually exists now and update if needed
                Optional<User> existingUser = userRepository.findByEmail(merchant.getEmail());
                if (existingUser.isPresent()) {
                    User user = existingUser.get();
                    // Update the existing user's status to APPROVED if it's not already
                    if (user.getStatus() != UserStatus.APPROVED) {
                        user.setStatus(UserStatus.APPROVED);
                        user.setApprovalDate(merchant.getApprovedAt());
                        user.setApprovedBy(merchant.getApprovedBy() != null ? merchant.getApprovedBy().toString() : "admin");
                        userRepository.save(user);
                        System.out.println("Updated existing user " + merchant.getEmail() + " status to APPROVED.");
                    }
                }
                return; // Don't throw error, just continue
            } else {
                // Re-throw other exceptions
                throw new RuntimeException("Failed to transfer merchant to users table: " + e.getMessage(), e);
            }
        }
    }
    
    public Merchant rejectMerchant(Long merchantId, String reason) {
        Optional<Merchant> merchantOpt = merchantRepository.findById(merchantId);
        if (merchantOpt.isPresent()) {
            Merchant merchant = merchantOpt.get();
            merchant.setStatus("REJECTED");
            merchant.setNotes(reason);
            merchant.setUpdatedAt(LocalDateTime.now());
            
            // Note: Rejected merchants are NOT transferred to users table
            System.out.println("Merchant " + merchant.getEmail() + " rejected. Status set to REJECTED.");
            return merchantRepository.save(merchant);
        }
        throw new RuntimeException("Merchant not found");
    }
    
    public void deleteMerchant(Long merchantId) {
        if (merchantRepository.existsById(merchantId)) {
            merchantRepository.deleteById(merchantId);
        } else {
            throw new RuntimeException("Merchant not found");
        }
    }
    
    public Merchant updateMerchant(Long merchantId, Merchant updatedMerchant) {
        Optional<Merchant> merchantOpt = merchantRepository.findById(merchantId);
        if (merchantOpt.isPresent()) {
            Merchant merchant = merchantOpt.get();
            
            // Update fields
            if (updatedMerchant.getCompanyName() != null) {
                merchant.setCompanyName(updatedMerchant.getCompanyName());
            }
            if (updatedMerchant.getContactPersonName() != null) {
                merchant.setContactPersonName(updatedMerchant.getContactPersonName());
            }
            if (updatedMerchant.getEmail() != null) {
                merchant.setEmail(updatedMerchant.getEmail());
            }
            if (updatedMerchant.getPhoneNumber() != null) {
                merchant.setPhoneNumber(updatedMerchant.getPhoneNumber());
            }
            if (updatedMerchant.getBusinessAddress() != null) {
                merchant.setBusinessAddress(updatedMerchant.getBusinessAddress());
            }
            if (updatedMerchant.getBusinessType() != null) {
                merchant.setBusinessType(updatedMerchant.getBusinessType());
            }
            if (updatedMerchant.getBusinessRegistrationNumber() != null) {
                merchant.setBusinessRegistrationNumber(updatedMerchant.getBusinessRegistrationNumber());
            }
            
            merchant.setUpdatedAt(LocalDateTime.now());
            return merchantRepository.save(merchant);
        }
        throw new RuntimeException("Merchant not found");
    }
    
    public long countMerchantsByStatus(String status) {
        return merchantRepository.countByStatus(status);
    }
    
    public boolean existsByEmail(String email) {
        return merchantRepository.existsByEmail(email);
    }
    
    public boolean existsByUsername(String username) {
        return merchantRepository.existsByUsername(username);
    }
    
    /**
     * Check if a merchant can login (exists in users table with APPROVED status)
     */
    public boolean canMerchantLogin(String email) {
        Optional<User> user = userRepository.findByEmail(email);
        return user.isPresent() && user.get().getRole() == UserRole.MERCHANT && user.get().getStatus() == UserStatus.APPROVED;
    }
    
    /**
     * Get merchant login status for debugging
     */
    public String getMerchantLoginStatus(String email) {
        Optional<Merchant> merchant = merchantRepository.findByEmail(email);
        Optional<User> user = userRepository.findByEmail(email);
        
        if (!merchant.isPresent()) {
            return "MERCHANT_NOT_FOUND";
        }
        
        if (!user.isPresent()) {
            return "NOT_IN_USERS_TABLE - Status: " + merchant.get().getStatus();
        }
        
        return "IN_USERS_TABLE - Status: " + user.get().getStatus() + ", Role: " + user.get().getRole();
    }
}