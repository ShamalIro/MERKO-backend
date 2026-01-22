package com.merko.merko_backend.controller;

import com.merko.merko_backend.service.UserStatsService;
import com.merko.merko_backend.entity.UserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "http://localhost:5173")
public class AdminStatsController {
    
    @Autowired
    private UserStatsService userStatsService;
    
    /**
     * Get user statistics for admin dashboard
     */
    @GetMapping("/stats")
    public ResponseEntity<?> getUserStats() {
        try {
            Map<String, Object> stats = userStatsService.getUserStatistics();
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "error", "Failed to retrieve user statistics",
                "message", e.getMessage()
            ));
        }
    }
    
    /**
     * Get detailed user statistics with role breakdowns
     */
    @GetMapping("/stats/detailed")
    public ResponseEntity<?> getDetailedUserStats() {
        try {
            Map<String, Object> detailedStats = userStatsService.getDetailedUserStats();
            return ResponseEntity.ok(detailedStats);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "error", "Failed to retrieve detailed statistics",
                "message", e.getMessage()
            ));
        }
    }
    
    /**
     * Get statistics for a specific role
     */
    @GetMapping("/stats/role/{role}")
    public ResponseEntity<?> getRoleStats(@PathVariable String role) {
        try {
            UserRole userRole = UserRole.valueOf(role.toUpperCase());
            Map<String, Object> roleStats = userStatsService.getRoleStatistics(userRole);
            return ResponseEntity.ok(roleStats);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of(
                "error", "Invalid role specified",
                "message", "Valid roles are: MERCHANT, SUPPLIER, DELIVERY, ADMIN",
                "providedRole", role
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "error", "Failed to retrieve role statistics",
                "message", e.getMessage()
            ));
        }
    }
    
    /**
     * Get recent sign-ups (merchants and suppliers)
     */
    @GetMapping("/recent-signups")
    public ResponseEntity<?> getRecentSignUps() {
        try {
            return ResponseEntity.ok(userStatsService.getRecentSignUps());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "error", "Failed to retrieve recent sign-ups",
                "message", e.getMessage()
            ));
        }
    }
    
    /**
     * Health check endpoint
     */
    @GetMapping("/health")
    public ResponseEntity<?> healthCheck() {
        return ResponseEntity.ok(Map.of(
            "status", "healthy",
            "timestamp", System.currentTimeMillis(),
            "service", "Admin Stats Service"
        ));
    }
}