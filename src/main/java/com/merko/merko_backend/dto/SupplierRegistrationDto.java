package com.merko.merko_backend.dto;

public class SupplierRegistrationDto {
    private String companyName;
    private String contactPersonName;
    private String email;
    private String username;
    private String phoneNumber;
    private String businessRegistrationNumber;
    private String password;
    
    // Constructors
    public SupplierRegistrationDto() {}
    
    public SupplierRegistrationDto(String companyName, String contactPersonName, String email, 
                                 String username, String phoneNumber, String businessRegistrationNumber, String password) {
        this.companyName = companyName;
        this.contactPersonName = contactPersonName;
        this.email = email;
        this.username = username;
        this.phoneNumber = phoneNumber;
        this.businessRegistrationNumber = businessRegistrationNumber;
        this.password = password;
    }
    
    // Getters and Setters
    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }
    
    public String getContactPersonName() { return contactPersonName; }
    public void setContactPersonName(String contactPersonName) { this.contactPersonName = contactPersonName; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    
    public String getBusinessRegistrationNumber() { return businessRegistrationNumber; }
    public void setBusinessRegistrationNumber(String businessRegistrationNumber) { this.businessRegistrationNumber = businessRegistrationNumber; }
    
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}