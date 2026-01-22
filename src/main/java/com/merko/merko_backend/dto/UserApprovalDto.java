package com.merko.merko_backend.dto;

import com.merko.merko_backend.entity.UserStatus;

public class UserApprovalDto {
    private Long userId;
    private UserStatus status;
    private String approvedBy;
    private String rejectionReason;
    
    // Default constructor
    public UserApprovalDto() {}
    
    // Constructor
    public UserApprovalDto(Long userId, UserStatus status, String approvedBy, String rejectionReason) {
        this.userId = userId;
        this.status = status;
        this.approvedBy = approvedBy;
        this.rejectionReason = rejectionReason;
    }
    
    // Getters and Setters
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    
    public UserStatus getStatus() { return status; }
    public void setStatus(UserStatus status) { this.status = status; }
    
    public String getApprovedBy() { return approvedBy; }
    public void setApprovedBy(String approvedBy) { this.approvedBy = approvedBy; }
    
    public String getRejectionReason() { return rejectionReason; }
    public void setRejectionReason(String rejectionReason) { this.rejectionReason = rejectionReason; }
}