package com.merko.merko_backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "merchants")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Merchant {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "merchant_id")
    private Long merchantId;
    
    @Column(name = "company_name", nullable = false)
    private String companyName;
    
    @Column(name = "contact_person_name", nullable = false)
    private String contactPersonName;
    
    @Column(name = "email", nullable = false, unique = true)
    private String email;
    
    @Column(name = "username")
    private String username;
    
    @Column(name = "phone_number", nullable = false)
    private String phoneNumber;
    
    @Column(name = "business_registration_number")
    private String businessRegistrationNumber;
    
    @Column(name = "password", nullable = false)
    private String password;
    
    @Column(name = "business_address")
    private String businessAddress;
    
    @Column(name = "business_type")
    private String businessType = "Retail";
    
    @Column(name = "role")
    private String role = "MERCHANT";
    
    @Column(name = "status")
    private String status = "PENDING_APPROVAL";
    
    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();
    
    @Column(name = "approved_at")
    private LocalDateTime approvedAt;
    
    @Column(name = "approved_by")
    private Long approvedBy;
    
    @Column(name = "notes")
    private String notes;
    
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // Convenience method for getting ID
    public Long getId() {
        return merchantId;
    }

    // Convenience method for getting Name
    public String getName() {
        return companyName;
    }
}
