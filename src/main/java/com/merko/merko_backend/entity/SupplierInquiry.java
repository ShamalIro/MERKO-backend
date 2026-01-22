package com.merko.merko_backend.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "supplier_inquiry")
public class SupplierInquiry {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "supplier_id", nullable = false)
    private Supplier supplier;

    @Column(nullable = false)
    private String topic;

    @Column(nullable = false)
    private String subTopic;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InquiryStatus status = InquiryStatus.PENDING;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    // Constructors
    public SupplierInquiry() {
        this.createdAt = LocalDateTime.now();
    }

    public SupplierInquiry(Supplier supplier, String topic, String subTopic, String description) {
        this();
        this.supplier = supplier;
        this.topic = topic;
        this.subTopic = subTopic;
        this.description = description;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Supplier getSupplier() { return supplier; }
    public void setSupplier(Supplier supplier) { this.supplier = supplier; }

    public String getTopic() { return topic; }
    public void setTopic(String topic) { this.topic = topic; }

    public String getSubTopic() { return subTopic; }
    public void setSubTopic(String subTopic) { this.subTopic = subTopic; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public InquiryStatus getStatus() { return status; }
    public void setStatus(InquiryStatus status) {
        this.status = status;
        this.updatedAt = LocalDateTime.now();
    }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
