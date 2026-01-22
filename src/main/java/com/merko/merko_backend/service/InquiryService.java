package com.merko.merko_backend.service;

import com.merko.merko_backend.dto.*;
import com.merko.merko_backend.entity.Inquiry;
import com.merko.merko_backend.entity.InquiryStatus;
import com.merko.merko_backend.entity.User;
import com.merko.merko_backend.repository.InquiryRepository;
import com.merko.merko_backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class InquiryService {

    @Autowired
    private InquiryRepository inquiryRepository;

    @Autowired
    private UserRepository userRepository;

    // SIMPLIFIED: Get inquiries for a specific user
    public List<InquiryDTO> getInquiriesByUserEmail(String email) {
        System.out.println("🔍 Fetching inquiries for email: " + email);

        try {
            // Verify user exists
            boolean userExists = userRepository.findByEmail(email).isPresent();
            System.out.println("👤 User exists: " + userExists);

            if (!userExists) {
                System.out.println("⚠️ User not found for email: " + email);
                return List.of();
            }

            List<Inquiry> inquiries = inquiryRepository.findByUserEmail(email);
            System.out.println("✅ Found " + inquiries.size() + " inquiries for user");

            // Log details about found inquiries
            for (int i = 0; i < Math.min(inquiries.size(), 3); i++) {
                Inquiry inquiry = inquiries.get(i);
                System.out.println("📝 Inquiry " + (i+1) + ": ID=" + inquiry.getId() +
                        ", Type=" + inquiry.getUserType() +
                        ", Topic=" + inquiry.getTopic() +
                        ", UserEmail=" + (inquiry.getUser() != null ? inquiry.getUser().getEmail() : "null"));
            }

            return inquiries.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());

        } catch (Exception e) {
            System.out.println("❌ Error in getInquiriesByUserEmail: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Error fetching inquiries for user: " + email, e);
        }
    }

    // Get all inquiries for admin
    public List<InquiryDTO> getAllInquiries() {
        try {
            System.out.println("📊 Admin: Fetching all inquiries from database");

            Long totalCount = inquiryRepository.countAllInquiries();
            System.out.println("📊 Total inquiry count in database: " + totalCount);

            List<Inquiry> inquiries = inquiryRepository.findAllByOrderByCreatedAtDesc();
            System.out.println("📊 Admin: Found " + inquiries.size() + " total inquiries");

            // Log some details about the inquiries
            for (int i = 0; i < Math.min(inquiries.size(), 5); i++) {
                Inquiry inquiry = inquiries.get(i);
                System.out.println("📝 Admin Inquiry " + (i+1) + ": ID=" + inquiry.getId() +
                        ", UserType=" + inquiry.getUserType() +
                        ", Email=" + inquiry.getUserEmail() +
                        ", Topic=" + inquiry.getTopic());
            }

            return inquiries.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            System.out.println("❌ Error in getAllInquiries: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Error fetching all inquiries", e);
        }
    }

    // SIMPLIFIED: Create new inquiry
    public InquiryDTO createInquiry(String userEmail, CreateInquiryDTO createInquiryDTO) {
        System.out.println("🆕 Creating new inquiry for: " + userEmail);
        System.out.println("📋 Inquiry details: Type=" + createInquiryDTO.getUserType() +
                ", Topic=" + createInquiryDTO.getTopic() +
                ", SubTopic=" + createInquiryDTO.getSubTopic());

        try {
            Inquiry inquiry = new Inquiry();
            inquiry.setTopic(createInquiryDTO.getTopic());
            inquiry.setSubTopic(createInquiryDTO.getSubTopic());
            inquiry.setDescription(createInquiryDTO.getDescription());
            inquiry.setUserType(createInquiryDTO.getUserType());

            // Find user by email
            System.out.println("🔍 Looking up user with email: " + userEmail);
            User user = userRepository.findByEmail(userEmail)
                    .orElseThrow(() -> new RuntimeException("User not found with email: " + userEmail));

            inquiry.setUser(user);
            System.out.println("✅ User found: " + user.getFirstName() + " " + user.getLastName());

            // Save and verify
            Inquiry savedInquiry = inquiryRepository.save(inquiry);
            System.out.println("✅ Created new inquiry with ID: " + savedInquiry.getId());

            // Verify it was saved correctly
            Inquiry verificationInquiry = inquiryRepository.findById(savedInquiry.getId()).orElse(null);
            if (verificationInquiry != null) {
                System.out.println("🔍 Verification: Saved inquiry has UserType=" + verificationInquiry.getUserType() +
                        ", Email=" + verificationInquiry.getUserEmail());
            }

            return convertToDTO(savedInquiry);

        } catch (Exception e) {
            System.out.println("❌ Error creating inquiry: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Error creating inquiry for user: " + userEmail, e);
        }
    }

    // Update inquiry (only if status is PENDING)
    public InquiryDTO updateInquiry(Long inquiryId, UpdateInquiryDTO updateInquiryDTO, String userEmail) {
        System.out.println("🔄 Updating inquiry ID: " + inquiryId + " for user: " + userEmail);

        Inquiry inquiry = inquiryRepository.findById(inquiryId)
                .orElseThrow(() -> new RuntimeException("Inquiry not found with ID: " + inquiryId));

        if (!isUserOwner(inquiry, userEmail)) {
            System.out.println("❌ User " + userEmail + " is not owner of inquiry " + inquiryId);
            System.out.println("🔍 Inquiry belongs to: " + inquiry.getUserEmail());
            throw new RuntimeException("Unauthorized: User " + userEmail + " cannot update inquiry " + inquiryId);
        }

        if (inquiry.getStatus() != InquiryStatus.PENDING) {
            throw new RuntimeException("Cannot update inquiry " + inquiryId + ". Status is " + inquiry.getStatus() + ", not PENDING");
        }

        // Only update content if provided, otherwise keep existing values
        if (updateInquiryDTO.getTopic() != null && !updateInquiryDTO.getTopic().isEmpty()) {
            inquiry.setTopic(updateInquiryDTO.getTopic());
        }
        if (updateInquiryDTO.getSubTopic() != null && !updateInquiryDTO.getSubTopic().isEmpty()) {
            inquiry.setSubTopic(updateInquiryDTO.getSubTopic());
        }
        if (updateInquiryDTO.getDescription() != null && !updateInquiryDTO.getDescription().isEmpty()) {
            inquiry.setDescription(updateInquiryDTO.getDescription());
        }

        inquiry.setUpdatedAt(LocalDateTime.now());

        Inquiry updatedInquiry = inquiryRepository.save(inquiry);
        System.out.println("✅ Updated inquiry ID: " + inquiryId);
        return convertToDTO(updatedInquiry);
    }

    // Delete inquiry (only if status is PENDING)
    public void deleteInquiry(Long inquiryId, String userEmail) {
        System.out.println("🗑️ Deleting inquiry ID: " + inquiryId + " for user: " + userEmail);

        Inquiry inquiry = inquiryRepository.findById(inquiryId)
                .orElseThrow(() -> new RuntimeException("Inquiry not found with ID: " + inquiryId));

        if (!isUserOwner(inquiry, userEmail)) {
            System.out.println("❌ User " + userEmail + " is not owner of inquiry " + inquiryId);
            throw new RuntimeException("Unauthorized: User " + userEmail + " cannot delete inquiry " + inquiryId);
        }

        if (inquiry.getStatus() != InquiryStatus.PENDING) {
            throw new RuntimeException("Cannot delete inquiry " + inquiryId + ". Status is " + inquiry.getStatus() + ", not PENDING");
        }

        inquiryRepository.delete(inquiry);
        System.out.println("✅ Deleted inquiry ID: " + inquiryId);
    }

    // Admin update inquiry status (no authentication needed)
    public InquiryDTO updateInquiryStatus(Long inquiryId, String status) {
        System.out.println("🔄 Admin updating inquiry " + inquiryId + " status to: " + status);

        Inquiry inquiry = inquiryRepository.findById(inquiryId)
                .orElseThrow(() -> new RuntimeException("Inquiry not found with ID: " + inquiryId));

        InquiryStatus newStatus;
        try {
            newStatus = InquiryStatus.valueOf(status);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid status: " + status + ". Valid values: PENDING, PROCESSING, HOLD, RESPONDED, CLOSED");
        }

        System.out.println("📋 Status change: " + inquiry.getStatus() + " → " + newStatus);

        // If status is RESPONDED, require admin reply
        if (newStatus == InquiryStatus.RESPONDED &&
                (inquiry.getAdminReply() == null || inquiry.getAdminReply().trim().isEmpty())) {
            throw new RuntimeException("Admin reply is required when setting status to RESPONDED");
        }

        inquiry.setStatus(newStatus);
        inquiry.setUpdatedAt(LocalDateTime.now());

        Inquiry updatedInquiry = inquiryRepository.save(inquiry);
        System.out.println("✅ Successfully updated inquiry " + inquiryId + " status to " + newStatus);
        return convertToDTO(updatedInquiry);
    }

    // Admin reply to inquiry (no authentication needed)
    public InquiryDTO adminReplyToInquiry(Long inquiryId, AdminReplyDTO adminReplyDTO) {
        System.out.println("💬 Admin replying to inquiry: " + inquiryId);

        Inquiry inquiry = inquiryRepository.findById(inquiryId)
                .orElseThrow(() -> new RuntimeException("Inquiry not found with ID: " + inquiryId));

        // Validate admin reply
        if (adminReplyDTO.getAdminReply() == null || adminReplyDTO.getAdminReply().trim().isEmpty()) {
            throw new RuntimeException("Admin reply cannot be empty");
        }

        String reply = adminReplyDTO.getAdminReply().trim();
        System.out.println("📝 Reply length: " + reply.length() + " characters");
        System.out.println("📋 Previous status: " + inquiry.getStatus());

        inquiry.setAdminReply(reply);
        inquiry.setStatus(InquiryStatus.RESPONDED);
        inquiry.setUpdatedAt(LocalDateTime.now());

        Inquiry updatedInquiry = inquiryRepository.save(inquiry);
        System.out.println("✅ Successfully added admin reply to inquiry " + inquiryId);
        return convertToDTO(updatedInquiry);
    }

    // Get inquiry by ID
    public InquiryDTO getInquiryById(Long inquiryId) {
        Inquiry inquiry = inquiryRepository.findById(inquiryId)
                .orElseThrow(() -> new RuntimeException("Inquiry not found with ID: " + inquiryId));
        return convertToDTO(inquiry);
    }

    // Update inquiry with admin privileges (bypasses user ownership check)
    public InquiryDTO updateInquiryAsAdmin(Long inquiryId, UpdateInquiryDTO updateInquiryDTO) {
        System.out.println("👨‍💼 Admin updating inquiry content: " + inquiryId);

        Inquiry inquiry = inquiryRepository.findById(inquiryId)
                .orElseThrow(() -> new RuntimeException("Inquiry not found with ID: " + inquiryId));

        // Handle status update if provided
        if (updateInquiryDTO.getStatus() != null && !updateInquiryDTO.getStatus().isEmpty()) {
            System.out.println("📋 Status update requested: " + updateInquiryDTO.getStatus());
            return updateInquiryStatus(inquiryId, updateInquiryDTO.getStatus());
        }

        // Otherwise update content
        if (updateInquiryDTO.getTopic() != null && !updateInquiryDTO.getTopic().isEmpty()) {
            inquiry.setTopic(updateInquiryDTO.getTopic());
        }
        if (updateInquiryDTO.getSubTopic() != null && !updateInquiryDTO.getSubTopic().isEmpty()) {
            inquiry.setSubTopic(updateInquiryDTO.getSubTopic());
        }
        if (updateInquiryDTO.getDescription() != null && !updateInquiryDTO.getDescription().isEmpty()) {
            inquiry.setDescription(updateInquiryDTO.getDescription());
        }

        inquiry.setUpdatedAt(LocalDateTime.now());

        Inquiry updatedInquiry = inquiryRepository.save(inquiry);
        System.out.println("✅ Admin updated inquiry ID: " + inquiryId);
        return convertToDTO(updatedInquiry);
    }

    // Delete inquiry with admin privileges (bypasses user ownership check)
    public void deleteInquiryAsAdmin(Long inquiryId) {
        System.out.println("👨‍💼 Admin deleting inquiry: " + inquiryId);

        Inquiry inquiry = inquiryRepository.findById(inquiryId)
                .orElseThrow(() -> new RuntimeException("Inquiry not found with ID: " + inquiryId));

        inquiryRepository.delete(inquiry);
        System.out.println("✅ Admin deleted inquiry ID: " + inquiryId);
    }

    // Get inquiries by status
    public List<InquiryDTO> getInquiriesByStatus(String status) {
        System.out.println("🔍 Filtering inquiries by status: " + status);

        // Validate status
        try {
            InquiryStatus.valueOf(status);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid status: " + status);
        }

        List<Inquiry> inquiries = inquiryRepository.findByStatusOrderByCreatedAtDesc(status);
        System.out.println("✅ Found " + inquiries.size() + " inquiries with status: " + status);
        return inquiries.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // Get inquiries by user type
    public List<InquiryDTO> getInquiriesByUserType(String userType) {
        System.out.println("🔍 Filtering inquiries by user type: " + userType);

        if (!"SUPPLIER".equals(userType) && !"MERCHANT".equals(userType)) {
            throw new RuntimeException("Invalid user type: " + userType + ". Valid values: SUPPLIER, MERCHANT");
        }

        List<Inquiry> inquiries = inquiryRepository.findByUserTypeOrderByCreatedAtDesc(userType);
        System.out.println("✅ Found " + inquiries.size() + " inquiries for user type: " + userType);
        return inquiries.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // SIMPLIFIED Helper method to check if user owns the inquiry
    private boolean isUserOwner(Inquiry inquiry, String userEmail) {
        System.out.println("🔍 Checking ownership for inquiry ID: " + inquiry.getId());
        System.out.println("📧 Checking against email: " + userEmail);

        if (inquiry.getUser() != null) {
            String inquiryUserEmail = inquiry.getUser().getEmail();
            System.out.println("👤 Inquiry user email: " + inquiryUserEmail);
            boolean isOwner = inquiryUserEmail.equals(userEmail);
            System.out.println("✅ Ownership result: " + isOwner);
            return isOwner;
        }

        System.out.println("❌ Could not determine ownership - null user");
        return false;
    }

    // Convert entity to DTO
    private InquiryDTO convertToDTO(Inquiry inquiry) {
        try {
            InquiryDTO dto = new InquiryDTO();
            dto.setId(inquiry.getId());
            dto.setUserType(inquiry.getUserType());
            dto.setUserName(inquiry.getUserName());
            dto.setUserEmail(inquiry.getUserEmail());
            dto.setTopic(inquiry.getTopic());
            dto.setSubTopic(inquiry.getSubTopic());
            dto.setDescription(inquiry.getDescription());
            dto.setAdminReply(inquiry.getAdminReply());
            dto.setStatus(inquiry.getStatus().name());
            dto.setCreatedAt(inquiry.getCreatedAt());
            dto.setUpdatedAt(inquiry.getUpdatedAt());
            dto.setCanEditDelete(inquiry.getStatus() == InquiryStatus.PENDING);

            return dto;
        } catch (Exception e) {
            System.out.println("❌ Error converting inquiry to DTO: " + e.getMessage());
            System.out.println("🔍 Inquiry details: ID=" + inquiry.getId() + ", UserType=" + inquiry.getUserType());
            throw new RuntimeException("Error converting inquiry to DTO", e);
        }
    }

    // DEBUG METHODS
    public void debugDatabaseContent() {
        try {
            System.out.println("🔧 === DATABASE DEBUG INFO ===");

            Long totalCount = inquiryRepository.countAllInquiries();
            System.out.println("📊 Total inquiries in database: " + totalCount);

            List<Object[]> basicInfo = inquiryRepository.findAllBasicInfo();
            System.out.println("📋 Basic inquiry info:");
            for (Object[] info : basicInfo) {
                System.out.println("  ID: " + info[0] + ", Type: " + info[1] + ", Email: " + info[2] + ", Topic: " + info[3]);
            }

        } catch (Exception e) {
            System.out.println("❌ Error in debug method: " + e.getMessage());
        }
    }
}