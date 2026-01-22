package com.merko.merko_backend.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "admin")
public class Admin {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "admin_id")
    private Long id;  // Use standard naming but map to admin_id column
    
    @Column(name = "email")  // Standard column name
    private String email;
    
    @Column(name = "password")  // Standard column name  
    private String password;
    
    // Optional fields - only include if they exist in your table
    @Column(name = "first_name")
    private String firstName;
    
    @Column(name = "last_name")
    private String lastName;
    
    @Column(name = "role")
    private String role;
    
    // Constructors
    public Admin() {}
    
    public Admin(String email, String password) {
        this.email = email;
        this.password = password;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}
