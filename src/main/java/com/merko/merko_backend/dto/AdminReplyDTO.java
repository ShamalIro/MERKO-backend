package com.merko.merko_backend.dto;

public class AdminReplyDTO {
    private String adminReply;
    private String status;

    // Constructors
    public AdminReplyDTO() {}

    public AdminReplyDTO(String adminReply, String status) {
        this.adminReply = adminReply;
        this.status = status;
    }

    // Getters and Setters
    public String getAdminReply() { return adminReply; }
    public void setAdminReply(String adminReply) { this.adminReply = adminReply; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}