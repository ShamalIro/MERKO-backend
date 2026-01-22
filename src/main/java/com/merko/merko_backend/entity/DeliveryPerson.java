package com.merko.merko_backend.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.time.LocalDate;

@Entity
@Table(name = "delivery_persons")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeliveryPerson {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long deliveryPersonId;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String phoneNumber;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String licenseNumber;

    @Column
    private String vehicleType;

    @Column
    private String vehicleNumber;

    @Column
    private Integer weightCapacity;

    @Column
    private Integer parcelCapacity;

    @Column
    private LocalDate licenseExpiryDate;

    @Column
    private String vehicleDocuments; // Store file paths/URLs as JSON string

    @Column
    private String address;

    @Column(nullable = false)
    @Builder.Default
    private String role = "DELIVERY_PERSON";

    @Column(nullable = false)
    @Builder.Default
    private String status = "ACTIVE";

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @Column
    private LocalDateTime approvedAt;

    @Column
    private String approvedBy;

    // Constructor for creating new delivery person
    public DeliveryPerson(String firstName, String lastName, String email, String phoneNumber, 
                         String password, String licenseNumber, String vehicleType, String vehicleNumber, String address) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.password = password;
        this.licenseNumber = licenseNumber;
        this.vehicleType = vehicleType;
        this.vehicleNumber = vehicleNumber;
        this.address = address;
        this.role = "DELIVERY_PERSON";
        this.status = "ACTIVE";
    }
}