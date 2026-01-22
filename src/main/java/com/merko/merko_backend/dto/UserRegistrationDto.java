package com.merko.merko_backend.dto;

import com.merko.merko_backend.entity.UserRole;

public class UserRegistrationDto {
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String phoneNumber;
    private String companyName;
    private String businessType;
    private UserRole role;
    
    // Default constructor
    public UserRegistrationDto() {}
    
    // Constructor with all fields
    public UserRegistrationDto(String firstName, String lastName, String email, String password,
                              String phoneNumber, String companyName, String businessType, UserRole role) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.companyName = companyName;
        this.businessType = businessType;
        this.role = role;
    }
    
    // Getters and Setters
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    
    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }
    
    public String getBusinessType() { return businessType; }
    public void setBusinessType(String businessType) { this.businessType = businessType; }
    
    public UserRole getRole() { return role; }
    public void setRole(UserRole role) { this.role = role; }
}