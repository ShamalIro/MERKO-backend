package com.merko.merko_backend.dto;

import java.time.LocalDate;

public class VehicleInfoDto {
    private String vehicleType;
    private String vehicleNumber;
    private Integer weightCapacity;
    private Integer parcelCapacity;
    private LocalDate licenseExpiryDate;
    private String vehicleDocuments;

    // Constructors
    public VehicleInfoDto() {}

    public VehicleInfoDto(String vehicleType, String vehicleNumber, Integer weightCapacity, 
                         Integer parcelCapacity, LocalDate licenseExpiryDate, String vehicleDocuments) {
        this.vehicleType = vehicleType;
        this.vehicleNumber = vehicleNumber;
        this.weightCapacity = weightCapacity;
        this.parcelCapacity = parcelCapacity;
        this.licenseExpiryDate = licenseExpiryDate;
        this.vehicleDocuments = vehicleDocuments;
    }

    // Getters and Setters
    public String getVehicleType() { return vehicleType; }
    public void setVehicleType(String vehicleType) { this.vehicleType = vehicleType; }

    public String getVehicleNumber() { return vehicleNumber; }
    public void setVehicleNumber(String vehicleNumber) { this.vehicleNumber = vehicleNumber; }

    public Integer getWeightCapacity() { return weightCapacity; }
    public void setWeightCapacity(Integer weightCapacity) { this.weightCapacity = weightCapacity; }

    public Integer getParcelCapacity() { return parcelCapacity; }
    public void setParcelCapacity(Integer parcelCapacity) { this.parcelCapacity = parcelCapacity; }

    public LocalDate getLicenseExpiryDate() { return licenseExpiryDate; }
    public void setLicenseExpiryDate(LocalDate licenseExpiryDate) { this.licenseExpiryDate = licenseExpiryDate; }

    public String getVehicleDocuments() { return vehicleDocuments; }
    public void setVehicleDocuments(String vehicleDocuments) { this.vehicleDocuments = vehicleDocuments; }
}