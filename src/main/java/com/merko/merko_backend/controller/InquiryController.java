package com.merko.merko_backend.controller;

import com.merko.merko_backend.dto.*;
import com.merko.merko_backend.service.InquiryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/inquiries")
@CrossOrigin(origins = "http://localhost:5173")
public class InquiryController {

    @Autowired
    private InquiryService inquiryService;

    // Get inquiries for logged-in user (supplier or merchant) - UPDATED with BasicAuth
    @GetMapping
    public ResponseEntity<List<InquiryDTO>> getUserInquiries(@RequestParam String userEmail) {

        System.out.println("🔍 GET /api/inquiries called");
        System.out.println("📧 User email: " + userEmail);

        try {
            List<InquiryDTO> inquiries = inquiryService.getInquiriesByUserEmail(userEmail);
            System.out.println("✅ Successfully fetched " + inquiries.size() + " inquiries for user: " + userEmail);

            // Log first inquiry for debugging (if any exist)
            if (!inquiries.isEmpty()) {
                InquiryDTO first = inquiries.get(0);
                System.out.println("📝 Sample inquiry - ID: " + first.getId() + ", Topic: " + first.getTopic());
            }

            return ResponseEntity.ok(inquiries);

        } catch (Exception e) {
            System.out.println("❌ Error in getUserInquiries: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        }
    }

    // Get all inquiries (admin only) - UPDATED with BasicAuth
    @GetMapping("/admin")
    public ResponseEntity<List<InquiryDTO>> getAllInquiries(@RequestParam String userEmail) {
        try {
            System.out.println("🔍 GET /api/inquiries/admin called");
            System.out.println("📧 Admin user email: " + userEmail);

            List<InquiryDTO> inquiries = inquiryService.getAllInquiries();
            System.out.println("✅ Admin fetched " + inquiries.size() + " total inquiries");
            return ResponseEntity.ok(inquiries);
        } catch (Exception e) {
            System.out.println("❌ Error in getAllInquiries: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        }
    }

    // Create new inquiry - UPDATED with BasicAuth
    @PostMapping
    public ResponseEntity<InquiryDTO> createInquiry(
            @RequestParam String userEmail,
            @RequestBody CreateInquiryDTO createInquiryDTO) {

        System.out.println("🆕 POST /api/inquiries called");
        System.out.println("📧 User email: " + userEmail);
        System.out.println("📋 Request body: " + createInquiryDTO.getTopic() + " - " + createInquiryDTO.getUserType());

        try {
            InquiryDTO inquiry = inquiryService.createInquiry(userEmail, createInquiryDTO);
            System.out.println("✅ Successfully created inquiry with ID: " + inquiry.getId());
            return ResponseEntity.ok(inquiry);

        } catch (RuntimeException e) {
            System.out.println("❌ Error creating inquiry: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(400).body(null);
        }
    }

    // Admin update status - UPDATED with BasicAuth (KEEP THIS FOR BACKWARD COMPATIBILITY)
    @PatchMapping("/{id}/status")
    public ResponseEntity<InquiryDTO> updateStatus(
            @PathVariable Long id,
            @RequestBody StatusUpdateDTO statusUpdateDTO,
            @RequestParam String userEmail) {

        try {
            System.out.println("🔄 PATCH /api/inquiries/" + id + "/status called");
            System.out.println("📧 Admin user email: " + userEmail);
            System.out.println("📋 Status: " + statusUpdateDTO.getStatus());

            InquiryDTO inquiry = inquiryService.updateInquiryStatus(id, statusUpdateDTO.getStatus());
            System.out.println("✅ Successfully updated inquiry status");
            return ResponseEntity.ok(inquiry);
        } catch (RuntimeException e) {
            System.out.println("❌ Error updating status: " + e.getMessage());
            return ResponseEntity.status(400).body(null);
        }
    }

    // Admin reply to inquiry - UPDATED with BasicAuth
    @PostMapping("/{id}/admin-reply")
    public ResponseEntity<InquiryDTO> adminReply(
            @PathVariable Long id,
            @RequestBody AdminReplyDTO adminReplyDTO,
            @RequestParam String userEmail) {

        try {
            System.out.println("💬 POST /api/inquiries/" + id + "/admin-reply called");
            System.out.println("📧 Admin user email: " + userEmail);

            InquiryDTO inquiry = inquiryService.adminReplyToInquiry(id, adminReplyDTO);
            System.out.println("✅ Successfully added admin reply");
            return ResponseEntity.ok(inquiry);
        } catch (RuntimeException e) {
            System.out.println("❌ Error adding admin reply: " + e.getMessage());
            return ResponseEntity.status(400).body(null);
        }
    }

    // Update inquiry - UPDATED with BasicAuth
    @PutMapping("/{id}")
    public ResponseEntity<InquiryDTO> updateInquiry(
            @PathVariable Long id,
            @RequestBody UpdateInquiryDTO updateInquiryDTO,
            @RequestParam String userEmail) {

        System.out.println("🔄 PUT /api/inquiries/" + id + " called");
        System.out.println("📧 User email: " + userEmail);

        try {
            InquiryDTO inquiry = inquiryService.updateInquiry(id, updateInquiryDTO, userEmail);
            System.out.println("✅ Successfully updated inquiry");
            return ResponseEntity.ok(inquiry);
        } catch (RuntimeException e) {
            System.out.println("❌ Error updating inquiry: " + e.getMessage());
            return ResponseEntity.status(400).body(null);
        }
    }

    // Delete inquiry - UPDATED with BasicAuth
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteInquiry(
            @PathVariable Long id,
            @RequestParam String userEmail) {

        System.out.println("🗑️ DELETE /api/inquiries/" + id + " called");
        System.out.println("📧 User email: " + userEmail);

        try {
            inquiryService.deleteInquiry(id, userEmail);
            System.out.println("✅ Successfully deleted inquiry");
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            System.out.println("❌ Error deleting inquiry: " + e.getMessage());
            return ResponseEntity.status(400).build();
        }
    }

    // Additional endpoints with BasicAuth
    @GetMapping("/{id}")
    public ResponseEntity<InquiryDTO> getInquiryById(@PathVariable Long id) {
        try {
            System.out.println("🔍 GET /api/inquiries/" + id + " called");
            InquiryDTO inquiry = inquiryService.getInquiryById(id);
            return ResponseEntity.ok(inquiry);
        } catch (RuntimeException e) {
            System.out.println("❌ Inquiry not found: " + id);
            return ResponseEntity.status(404).build();
        }
    }

    @PutMapping("/admin/{id}")
    public ResponseEntity<InquiryDTO> updateInquiryAsAdmin(
            @PathVariable Long id,
            @RequestBody UpdateInquiryDTO updateInquiryDTO,
            @RequestParam String userEmail) {
        try {
            System.out.println("👨‍💼 PUT /api/inquiries/admin/" + id + " called");
            System.out.println("📧 Admin user email: " + userEmail);
            System.out.println("📋 Update DTO - Status: " + updateInquiryDTO.getStatus() +
                    ", Topic: " + updateInquiryDTO.getTopic() +
                    ", SubTopic: " + updateInquiryDTO.getSubTopic());

            InquiryDTO inquiry = inquiryService.updateInquiryAsAdmin(id, updateInquiryDTO);
            System.out.println("✅ Successfully updated inquiry as admin");
            return ResponseEntity.ok(inquiry);
        } catch (RuntimeException e) {
            System.out.println("❌ Error in admin update: " + e.getMessage());
            return ResponseEntity.status(400).body(null);
        }
    }

    @DeleteMapping("/admin/{id}")
    public ResponseEntity<Void> deleteInquiryAsAdmin(
            @PathVariable Long id,
            @RequestParam String userEmail) {
        try {
            System.out.println("👨‍💼 DELETE /api/inquiries/admin/" + id + " called");
            System.out.println("📧 Admin user email: " + userEmail);

            inquiryService.deleteInquiryAsAdmin(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            System.out.println("❌ Error in admin delete: " + e.getMessage());
            return ResponseEntity.status(400).build();
        }
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<InquiryDTO>> getInquiriesByStatus(@PathVariable String status) {
        try {
            System.out.println("🔍 GET /api/inquiries/status/" + status + " called");
            List<InquiryDTO> inquiries = inquiryService.getInquiriesByStatus(status);
            return ResponseEntity.ok(inquiries);
        } catch (RuntimeException e) {
            System.out.println("❌ Error filtering by status: " + e.getMessage());
            return ResponseEntity.status(400).build();
        }
    }

    @GetMapping("/user-type/{userType}")
    public ResponseEntity<List<InquiryDTO>> getInquiriesByUserType(@PathVariable String userType) {
        try {
            System.out.println("🔍 GET /api/inquiries/user-type/" + userType + " called");
            List<InquiryDTO> inquiries = inquiryService.getInquiriesByUserType(userType);
            return ResponseEntity.ok(inquiries);
        } catch (RuntimeException e) {
            System.out.println("❌ Error filtering by user type: " + e.getMessage());
            return ResponseEntity.status(400).build();
        }
    }

    // DEBUG ENDPOINT - Add this temporarily to test database content
    @GetMapping("/debug/all")
    public ResponseEntity<List<InquiryDTO>> debugGetAllInquiries() {
        try {
            System.out.println("🔧 DEBUG: Fetching ALL inquiries from database");
            List<InquiryDTO> inquiries = inquiryService.getAllInquiries();
            System.out.println("🔧 DEBUG: Found " + inquiries.size() + " total inquiries in database");

            for (InquiryDTO inquiry : inquiries) {
                System.out.println("🔧 DEBUG Inquiry: ID=" + inquiry.getId() +
                        ", Email=" + inquiry.getUserEmail() +
                        ", Type=" + inquiry.getUserType() +
                        ", Topic=" + inquiry.getTopic());
            }

            return ResponseEntity.ok(inquiries);
        } catch (Exception e) {
            System.out.println("❌ DEBUG Error: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        }
    }
}