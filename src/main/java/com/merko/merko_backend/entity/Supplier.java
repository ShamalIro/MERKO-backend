package com.merko.merko_backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "suppliers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Supplier {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "supplier_id")
    private Long supplierId;
    
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
    
    @Column(name = "role")
    private String role = "SUPPLIER";
    
    @Column(name = "status")
    private String status = "PENDING_APPROVAL";
    
    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();
    
    @Column(name = "approved_at")
    private LocalDateTime approvedAt;
    
    @Column(name = "approved_by")
    private String approvedBy;

    // Convenience method for getting ID
    public Long getId() {
        return supplierId;
    }

    // Convenience method for getting Name
    public String getName() {
        return companyName;
    }
}
