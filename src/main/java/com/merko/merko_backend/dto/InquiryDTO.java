package com.merko.merko_backend.dto;

import java.time.LocalDateTime;

public class InquiryDTO {
    private Long id;
    private String userType;
    private String userName;
    private String userEmail;
    private String topic;
    private String subTopic;
    private String description;
    private String adminReply;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private boolean canEditDelete;

    // Constructors
    public InquiryDTO() {}

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUserType() { return userType; }
    public void setUserType(String userType) { this.userType = userType; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public String getUserEmail() { return userEmail; }
    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }

    public String getTopic() { return topic; }
    public void setTopic(String topic) { this.topic = topic; }

    public String getSubTopic() { return subTopic; }
    public void setSubTopic(String subTopic) { this.subTopic = subTopic; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getAdminReply() { return adminReply; }
    public void setAdminReply(String adminReply) { this.adminReply = adminReply; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public boolean isCanEditDelete() { return canEditDelete; }
    public void setCanEditDelete(boolean canEditDelete) { this.canEditDelete = canEditDelete; }
}