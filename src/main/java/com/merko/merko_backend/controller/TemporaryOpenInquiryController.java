package com.merko.merko_backend.controller;

import com.merko.merko_backend.dto.*;
import com.merko.merko_backend.service.InquiryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/open/inquiries")
// FIXED: Remove the problematic @CrossOrigin annotation - let SecurityConfig handle CORS
public class TemporaryOpenInquiryController {

    @Autowired
    private InquiryService inquiryService;

    // Test endpoint to verify controller is accessible
    @GetMapping("/test")
    public ResponseEntity<String> test() {
        System.out.println("🧪 TEST ENDPOINT CALLED - Controller is working!");
        return ResponseEntity.ok("SUCCESS: Open inquiry controller is working perfectly!");
    }

    // Get count of inquiries in database
    @GetMapping("/count")
    public ResponseEntity<String> getCount() {
        try {
            System.out.println("🔢 COUNT ENDPOINT CALLED");
            List<InquiryDTO> inquiries = inquiryService.getAllInquiries();
            String response = "Total inquiries in database: " + inquiries.size();
            System.out.println("✅ " + response);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.out.println("❌ Count endpoint error: " + e.getMessage());
            return ResponseEntity.ok("Error getting count: " + e.getMessage());
        }
    }

    // Get all inquiries without any authentication
    @GetMapping("/all")
    public ResponseEntity<List<InquiryDTO>> getAllInquiriesOpen() {
        try {
            System.out.println("📋 GET ALL INQUIRIES - OPEN ACCESS");
            List<InquiryDTO> inquiries = inquiryService.getAllInquiries();
            System.out.println("✅ Found " + inquiries.size() + " inquiries");

            // Log first few inquiries for debugging
            for (int i = 0; i < Math.min(3, inquiries.size()); i++) {
                InquiryDTO inquiry = inquiries.get(i);
                System.out.println("📝 Inquiry " + (i+1) + ": ID=" + inquiry.getId() +
                        ", User=" + inquiry.getUserEmail() + ", Topic=" + inquiry.getTopic());
            }

            return ResponseEntity.ok(inquiries);
        } catch (Exception e) {
            System.out.println("❌ Get all inquiries error: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        }
    }

    // Update inquiry status without authentication
    @PatchMapping("/{id}/status")
    public ResponseEntity<InquiryDTO> updateStatusOpen(
            @PathVariable Long id,
            @RequestBody StatusUpdateDTO statusUpdateDTO) {
        try {
            System.out.println("🔄 UPDATING STATUS - ID: " + id + ", Status: " + statusUpdateDTO.getStatus());
            InquiryDTO inquiry = inquiryService.updateInquiryStatus(id, statusUpdateDTO.getStatus());
            System.out.println("✅ Status updated successfully");
            return ResponseEntity.ok(inquiry);
        } catch (RuntimeException e) {
            System.out.println("❌ Status update error: " + e.getMessage());
            return ResponseEntity.status(400).build();
        }
    }

    // Submit admin reply without authentication
    @PostMapping("/{id}/admin-reply")
    public ResponseEntity<InquiryDTO> adminReplyOpen(
            @PathVariable Long id,
            @RequestBody AdminReplyDTO adminReplyDTO) {
        try {
            System.out.println("💬 SUBMITTING ADMIN REPLY - ID: " + id);
            System.out.println("📝 Reply: " + adminReplyDTO.getAdminReply());
            InquiryDTO inquiry = inquiryService.adminReplyToInquiry(id, adminReplyDTO);
            System.out.println("✅ Admin reply submitted successfully");
            return ResponseEntity.ok(inquiry);
        } catch (RuntimeException e) {
            System.out.println("❌ Admin reply error: " + e.getMessage());
            return ResponseEntity.status(400).build();
        }
    }

    // Debug endpoint to show inquiry by ID
    @GetMapping("/{id}")
    public ResponseEntity<InquiryDTO> getInquiryByIdOpen(@PathVariable Long id) {
        try {
            System.out.println("🔍 GET INQUIRY BY ID - ID: " + id);
            InquiryDTO inquiry = inquiryService.getInquiryById(id);
            System.out.println("✅ Found inquiry: " + inquiry.getTopic());
            return ResponseEntity.ok(inquiry);
        } catch (RuntimeException e) {
            System.out.println("❌ Get inquiry by ID error: " + e.getMessage());
            return ResponseEntity.status(404).build();
        }
    }

    // Database connection test
    @GetMapping("/db-test")
    public ResponseEntity<String> testDatabaseConnection() {
        try {
            System.out.println("🔍 TESTING DATABASE CONNECTION");
            List<InquiryDTO> inquiries = inquiryService.getAllInquiries();

            String result = "Database connection SUCCESS!\n" +
                    "Total inquiries: " + inquiries.size() + "\n";

            if (!inquiries.isEmpty()) {
                result += "Sample inquiry: ID=" + inquiries.get(0).getId() +
                        ", Topic=" + inquiries.get(0).getTopic();
            }

            System.out.println("✅ " + result);
            return ResponseEntity.ok(result);

        } catch (Exception e) {
            String error = "Database connection FAILED: " + e.getMessage();
            System.out.println("❌ " + error);
            return ResponseEntity.status(500).body(error);
        }
    }

    // Get inquiries by status (for filtering)
    @GetMapping("/status/{status}")
    public ResponseEntity<List<InquiryDTO>> getInquiriesByStatusOpen(@PathVariable String status) {
        try {
            System.out.println("🔍 GET INQUIRIES BY STATUS - Status: " + status);
            List<InquiryDTO> inquiries = inquiryService.getInquiriesByStatus(status);
            System.out.println("✅ Found " + inquiries.size() + " inquiries with status: " + status);
            return ResponseEntity.ok(inquiries);
        } catch (RuntimeException e) {
            System.out.println("❌ Get by status error: " + e.getMessage());
            return ResponseEntity.status(400).build();
        }
    }

    // Get inquiries by user type (for filtering)
    @GetMapping("/user-type/{userType}")
    public ResponseEntity<List<InquiryDTO>> getInquiriesByUserTypeOpen(@PathVariable String userType) {
        try {
            System.out.println("🔍 GET INQUIRIES BY USER TYPE - Type: " + userType);
            List<InquiryDTO> inquiries = inquiryService.getInquiriesByUserType(userType);
            System.out.println("✅ Found " + inquiries.size() + " inquiries for user type: " + userType);
            return ResponseEntity.ok(inquiries);
        } catch (RuntimeException e) {
            System.out.println("❌ Get by user type error: " + e.getMessage());
            return ResponseEntity.status(400).build();
        }
    }
}