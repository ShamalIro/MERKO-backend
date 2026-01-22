package com.merko.merko_backend.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "inquiry")
public class Inquiry {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String userType; // "SUPPLIER" or "MERCHANT"

    // REPLACED: Separate supplier and merchant with single user reference
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String topic;

    @Column(nullable = false)
    private String subTopic;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(columnDefinition = "TEXT")
    private String adminReply;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InquiryStatus status = InquiryStatus.PENDING;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    // Constructors
    public Inquiry() {
        this.createdAt = LocalDateTime.now();
    }

    public Inquiry(String userType, User user, String topic, String subTopic, String description) {
        this();
        this.userType = userType;
        this.user = user;
        this.topic = topic;
        this.subTopic = subTopic;
        this.description = description;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUserType() { return userType; }
    public void setUserType(String userType) { this.userType = userType; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public String getTopic() { return topic; }
    public void setTopic(String topic) { this.topic = topic; }

    public String getSubTopic() { return subTopic; }
    public void setSubTopic(String subTopic) { this.subTopic = subTopic; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getAdminReply() { return adminReply; }
    public void setAdminReply(String adminReply) { this.adminReply = adminReply; }

    public InquiryStatus getStatus() { return status; }
    public void setStatus(InquiryStatus status) {
        this.status = status;
        this.updatedAt = LocalDateTime.now();
    }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    // SIMPLIFIED Helper method to get user name
    public String getUserName() {
        if (user != null) {
            return user.getFirstName() + " " + user.getLastName();
        }
        return "Unknown User";
    }

    // SIMPLIFIED Helper method to get user email
    public String getUserEmail() {
        if (user != null) {
            return user.getEmail();
        }
        return "Unknown Email";
    }
}