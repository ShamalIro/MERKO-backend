package com.merko.merko_backend.service;

import com.merko.merko_backend.entity.User;
import com.merko.merko_backend.entity.UserRole;
import com.merko.merko_backend.entity.UserStatus;
import com.merko.merko_backend.entity.Merchant;
import com.merko.merko_backend.entity.Supplier;
import com.merko.merko_backend.repository.UserRepository;
import com.merko.merko_backend.repository.MerchantRepository;
import com.merko.merko_backend.repository.SupplierRepository;
import com.merko.merko_backend.dto.RecentSignUpDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class UserStatsService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private MerchantRepository merchantRepository;
    
    @Autowired
    private SupplierRepository supplierRepository;
    
    /**
     * Get user statistics for admin dashboard
     */
    public Map<String, Object> getUserStatistics() {
        Map<String, Object> stats = new HashMap<>();
        
        try {
            // Count users by role
            long merchantCount = userRepository.countByRole(UserRole.MERCHANT);
            long supplierCount = userRepository.countByRole(UserRole.SUPPLIER);
            long deliveryPersonCount = userRepository.countByRole(UserRole.DELIVERY);
            long adminCount = userRepository.countByRole(UserRole.ADMIN);
            
            // Count pending approvals (users with PENDING status)
            long pendingApprovals = userRepository.countByStatus(UserStatus.PENDING);
            
            // Total users count
            long totalUsers = userRepository.count();
            
            // Build statistics map
            stats.put("merchants", merchantCount);
            stats.put("suppliers", supplierCount);
            stats.put("deliveryPersons", deliveryPersonCount);
            stats.put("admins", adminCount);
            stats.put("pendingApprovals", pendingApprovals);
            stats.put("totalUsers", totalUsers);
            
            // Additional breakdown by status
            long approvedUsers = userRepository.countByStatus(UserStatus.APPROVED);
            long rejectedUsers = userRepository.countByStatus(UserStatus.REJECTED);
            
            stats.put("approvedUsers", approvedUsers);
            stats.put("rejectedUsers", rejectedUsers);
            
            System.out.println("User statistics retrieved successfully:");
            System.out.println("- Merchants: " + merchantCount);
            System.out.println("- Suppliers: " + supplierCount);
            System.out.println("- Delivery Persons: " + deliveryPersonCount);
            System.out.println("- Admins: " + adminCount);
            System.out.println("- Pending Approvals: " + pendingApprovals);
            System.out.println("- Total Users: " + totalUsers);
            
        } catch (Exception e) {
            System.err.println("Error retrieving user statistics: " + e.getMessage());
            e.printStackTrace();
            
            // Return default values in case of error
            stats.put("merchants", 0);
            stats.put("suppliers", 0);
            stats.put("deliveryPersons", 0);
            stats.put("admins", 0);
            stats.put("pendingApprovals", 0);
            stats.put("totalUsers", 0);
            stats.put("approvedUsers", 0);
            stats.put("rejectedUsers", 0);
            stats.put("error", "Failed to retrieve statistics: " + e.getMessage());
        }
        
        return stats;
    }
    
    /**
     * Get role-specific statistics
     */
    public Map<String, Object> getRoleStatistics(UserRole role) {
        Map<String, Object> roleStats = new HashMap<>();
        
        try {
            long totalByRole = userRepository.countByRole(role);
            long approvedByRole = userRepository.findByRoleAndStatus(role, UserStatus.APPROVED).size();
            long pendingByRole = userRepository.findByRoleAndStatus(role, UserStatus.PENDING).size();
            long rejectedByRole = userRepository.findByRoleAndStatus(role, UserStatus.REJECTED).size();
            
            roleStats.put("role", role.toString());
            roleStats.put("total", totalByRole);
            roleStats.put("approved", approvedByRole);
            roleStats.put("pending", pendingByRole);
            roleStats.put("rejected", rejectedByRole);
            
        } catch (Exception e) {
            System.err.println("Error retrieving role statistics for " + role + ": " + e.getMessage());
            roleStats.put("error", "Failed to retrieve role statistics: " + e.getMessage());
        }
        
        return roleStats;
    }
    
    /**
     * Get detailed user count breakdown
     */
    public Map<String, Object> getDetailedUserStats() {
        Map<String, Object> detailedStats = new HashMap<>();
        
        try {
            // Get basic statistics
            Map<String, Object> basicStats = getUserStatistics();
            detailedStats.putAll(basicStats);
            
            // Add role-specific breakdowns
            detailedStats.put("merchantStats", getRoleStatistics(UserRole.MERCHANT));
            detailedStats.put("supplierStats", getRoleStatistics(UserRole.SUPPLIER));
            detailedStats.put("deliveryStats", getRoleStatistics(UserRole.DELIVERY));
            detailedStats.put("adminStats", getRoleStatistics(UserRole.ADMIN));
            
        } catch (Exception e) {
            System.err.println("Error retrieving detailed user statistics: " + e.getMessage());
            detailedStats.put("error", "Failed to retrieve detailed statistics: " + e.getMessage());
        }
        
        return detailedStats;
    }
    
    /**
     * Get recent sign-ups (3 most recent merchants and suppliers)
     */
    public List<RecentSignUpDTO> getRecentSignUps() {
        List<RecentSignUpDTO> recentSignUps = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy");
        
        try {
            // Get recent merchants (limited to 3)
            List<Merchant> recentMerchants = merchantRepository.findTop3ByOrderByCreatedAtDesc();
            if (recentMerchants.size() > 3) {
                recentMerchants = recentMerchants.subList(0, 3);
            }
            
            for (Merchant merchant : recentMerchants) {
                RecentSignUpDTO dto = new RecentSignUpDTO();
                dto.setId(merchant.getId());
                dto.setName(merchant.getContactPersonName());
                dto.setCompany(merchant.getCompanyName());
                dto.setRole("Merchant");
                dto.setDate(merchant.getCreatedAt());
                dto.setStatus(merchant.getStatus());
                dto.setEmail(merchant.getEmail());
                recentSignUps.add(dto);
            }
            
            // Get recent suppliers (limited to 3)
            List<Supplier> recentSuppliers = supplierRepository.findTop3ByOrderByCreatedAtDesc();
            if (recentSuppliers.size() > 3) {
                recentSuppliers = recentSuppliers.subList(0, 3);
            }
            
            for (Supplier supplier : recentSuppliers) {
                RecentSignUpDTO dto = new RecentSignUpDTO();
                dto.setId(supplier.getId());
                dto.setName(supplier.getContactPersonName());
                dto.setCompany(supplier.getCompanyName());
                dto.setRole("Supplier");
                dto.setDate(supplier.getCreatedAt());
                dto.setStatus(supplier.getStatus());
                dto.setEmail(supplier.getEmail());
                recentSignUps.add(dto);
            }
            
            // Sort by creation date (most recent first) and limit to 6 total
            recentSignUps = recentSignUps.stream()
                .sorted((a, b) -> b.getDate().compareTo(a.getDate()))
                .limit(6)
                .collect(Collectors.toList());
            
        } catch (Exception e) {
            System.err.println("Error retrieving recent sign-ups: " + e.getMessage());
        }
        
        return recentSignUps;
    }
}