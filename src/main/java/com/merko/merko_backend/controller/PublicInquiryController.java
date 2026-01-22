package com.merko.merko_backend.controller;

import com.merko.merko_backend.dto.*;
import com.merko.merko_backend.service.InquiryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/public/inquiries")
@CrossOrigin(origins = "*") // Allow all origins
public class PublicInquiryController {

    @Autowired
    private InquiryService inquiryService;

    // Completely open - no authentication of any kind
    @GetMapping("/all")
    public ResponseEntity<List<InquiryDTO>> getAllInquiries() {
        try {
            System.out.println("🔓 PUBLIC ACCESS: Fetching all inquiries (no auth)");
            List<InquiryDTO> inquiries = inquiryService.getAllInquiries();
            System.out.println("✅ PUBLIC ACCESS: Found " + inquiries.size() + " inquiries");
            return ResponseEntity.ok(inquiries);
        } catch (Exception e) {
            System.out.println("❌ PUBLIC ACCESS Error: " + e.getMessage());
            return ResponseEntity.status(500).build();
        }
    }

    // Completely open status update
    @PatchMapping("/{id}/status")
    public ResponseEntity<InquiryDTO> updateStatus(
            @PathVariable Long id,
            @RequestBody StatusUpdateDTO statusUpdateDTO) {
        try {
            System.out.println("🔓 PUBLIC ACCESS: Updating status for inquiry " + id);
            InquiryDTO inquiry = inquiryService.updateInquiryStatus(id, statusUpdateDTO.getStatus());
            return ResponseEntity.ok(inquiry);
        } catch (RuntimeException e) {
            System.out.println("❌ PUBLIC ACCESS Status Update Error: " + e.getMessage());
            return ResponseEntity.status(400).body(null);
        }
    }

    // Completely open admin reply
    @PostMapping("/{id}/admin-reply")
    public ResponseEntity<InquiryDTO> adminReply(
            @PathVariable Long id,
            @RequestBody AdminReplyDTO adminReplyDTO) {
        try {
            System.out.println("🔓 PUBLIC ACCESS: Admin reply for inquiry " + id);
            InquiryDTO inquiry = inquiryService.adminReplyToInquiry(id, adminReplyDTO);
            return ResponseEntity.ok(inquiry);
        } catch (RuntimeException e) {
            System.out.println("❌ PUBLIC ACCESS Reply Error: " + e.getMessage());
            return ResponseEntity.status(400).body(null);
        }
    }

    // Get single inquiry
    @GetMapping("/{id}")
    public ResponseEntity<InquiryDTO> getInquiryById(@PathVariable Long id) {
        try {
            System.out.println("🔓 PUBLIC ACCESS: Getting inquiry " + id);
            InquiryDTO inquiry = inquiryService.getInquiryById(id);
            return ResponseEntity.ok(inquiry);
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).build();
        }
    }
}