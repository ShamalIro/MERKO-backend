package com.merko.merko_backend.service;

import com.merko.merko_backend.entity.DeliveryPerson;
import com.merko.merko_backend.repository.DeliveryPersonRepository;
import com.merko.merko_backend.dto.VehicleInfoDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class DeliveryPersonService {
    
    @Autowired
    private DeliveryPersonRepository deliveryPersonRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    // Create a new delivery person
    public DeliveryPerson createDeliveryPerson(String firstName, String lastName, String email, 
                                             String phoneNumber, String password, String licenseNumber,
                                             String vehicleType, String vehicleNumber, String address) throws Exception {
        
        // Check if email already exists
        if (deliveryPersonRepository.existsByEmail(email)) {
            throw new Exception("Email is already registered");
        }
        
        // Check if license number already exists
        if (deliveryPersonRepository.existsByLicenseNumber(licenseNumber)) {
            throw new Exception("License number is already registered");
        }
        
        // Validate required fields
        if (firstName == null || firstName.trim().isEmpty()) {
            throw new Exception("First name is required");
        }
        if (lastName == null || lastName.trim().isEmpty()) {
            throw new Exception("Last name is required");
        }
        if (email == null || email.trim().isEmpty()) {
            throw new Exception("Email is required");
        }
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            throw new Exception("Phone number is required");
        }
        if (password == null || password.trim().isEmpty()) {
            throw new Exception("Password is required");
        }
        if (licenseNumber == null || licenseNumber.trim().isEmpty()) {
            throw new Exception("License number is required");
        }
        
        // Create new delivery person
        DeliveryPerson deliveryPerson = new DeliveryPerson(
            firstName.trim(),
            lastName.trim(),
            email.trim().toLowerCase(),
            phoneNumber.trim(),
            passwordEncoder.encode(password), // Encrypt password
            licenseNumber.trim(),
            vehicleType != null ? vehicleType.trim() : null,
            vehicleNumber != null ? vehicleNumber.trim() : null,
            address != null ? address.trim() : null
        );
        
        deliveryPerson.setApprovedAt(LocalDateTime.now());
        deliveryPerson.setApprovedBy("ADMIN");
        
        return deliveryPersonRepository.save(deliveryPerson);
    }
    
    // Get all delivery persons
    public List<DeliveryPerson> getAllDeliveryPersons() {
        return deliveryPersonRepository.findAllByOrderByCreatedAtDesc();
    }
    
    // Get delivery person by ID
    public Optional<DeliveryPerson> getDeliveryPersonById(Long deliveryPersonId) {
        return deliveryPersonRepository.findById(deliveryPersonId);
    }
    
    // Get delivery person by email
    public Optional<DeliveryPerson> getDeliveryPersonByEmail(String email) {
        return deliveryPersonRepository.findByEmail(email);
    }
    
    // Get delivery persons by status
    public List<DeliveryPerson> getDeliveryPersonsByStatus(String status) {
        return deliveryPersonRepository.findByStatus(status);
    }
    
    // Get active delivery persons
    public List<DeliveryPerson> getActiveDeliveryPersons() {
        return deliveryPersonRepository.findActiveDeliveryPersons();
    }
    
    // Update delivery person status
    public DeliveryPerson updateDeliveryPersonStatus(Long deliveryPersonId, String status) throws Exception {
        Optional<DeliveryPerson> deliveryPersonOpt = deliveryPersonRepository.findById(deliveryPersonId);
        
        if (!deliveryPersonOpt.isPresent()) {
            throw new Exception("Delivery person not found");
        }
        
        DeliveryPerson deliveryPerson = deliveryPersonOpt.get();
        deliveryPerson.setStatus(status);
        
        return deliveryPersonRepository.save(deliveryPerson);
    }
    
    // Update delivery person details
    public DeliveryPerson updateDeliveryPerson(Long deliveryPersonId, DeliveryPerson updatedDeliveryPerson) throws Exception {
        Optional<DeliveryPerson> deliveryPersonOpt = deliveryPersonRepository.findById(deliveryPersonId);
        
        if (!deliveryPersonOpt.isPresent()) {
            throw new Exception("Delivery person not found");
        }
        
        DeliveryPerson deliveryPerson = deliveryPersonOpt.get();
        
        // Update fields
        if (updatedDeliveryPerson.getFirstName() != null) {
            deliveryPerson.setFirstName(updatedDeliveryPerson.getFirstName());
        }
        if (updatedDeliveryPerson.getLastName() != null) {
            deliveryPerson.setLastName(updatedDeliveryPerson.getLastName());
        }
        if (updatedDeliveryPerson.getPhoneNumber() != null) {
            deliveryPerson.setPhoneNumber(updatedDeliveryPerson.getPhoneNumber());
        }
        if (updatedDeliveryPerson.getVehicleType() != null) {
            deliveryPerson.setVehicleType(updatedDeliveryPerson.getVehicleType());
        }
        if (updatedDeliveryPerson.getVehicleNumber() != null) {
            deliveryPerson.setVehicleNumber(updatedDeliveryPerson.getVehicleNumber());
        }
        if (updatedDeliveryPerson.getAddress() != null) {
            deliveryPerson.setAddress(updatedDeliveryPerson.getAddress());
        }
        if (updatedDeliveryPerson.getStatus() != null) {
            deliveryPerson.setStatus(updatedDeliveryPerson.getStatus());
        }
        
        return deliveryPersonRepository.save(deliveryPerson);
    }
    
    // Delete delivery person
    public void deleteDeliveryPerson(Long deliveryPersonId) throws Exception {
        if (!deliveryPersonRepository.existsById(deliveryPersonId)) {
            throw new Exception("Delivery person not found");
        }
        
        deliveryPersonRepository.deleteById(deliveryPersonId);
    }

    // Update vehicle information for delivery person
    public DeliveryPerson updateVehicleInformation(Long deliveryPersonId, VehicleInfoDto vehicleInfoDto) throws Exception {
        Optional<DeliveryPerson> deliveryPersonOpt = deliveryPersonRepository.findById(deliveryPersonId);
        if (!deliveryPersonOpt.isPresent()) {
            throw new Exception("Delivery person not found with ID: " + deliveryPersonId);
        }
        
        DeliveryPerson deliveryPerson = deliveryPersonOpt.get();
        
        // Update vehicle information
        if (vehicleInfoDto.getVehicleType() != null) {
            deliveryPerson.setVehicleType(vehicleInfoDto.getVehicleType());
        }
        if (vehicleInfoDto.getVehicleNumber() != null) {
            deliveryPerson.setVehicleNumber(vehicleInfoDto.getVehicleNumber());
        }
        if (vehicleInfoDto.getWeightCapacity() != null) {
            deliveryPerson.setWeightCapacity(vehicleInfoDto.getWeightCapacity());
        }
        if (vehicleInfoDto.getParcelCapacity() != null) {
            deliveryPerson.setParcelCapacity(vehicleInfoDto.getParcelCapacity());
        }
        if (vehicleInfoDto.getLicenseExpiryDate() != null) {
            deliveryPerson.setLicenseExpiryDate(vehicleInfoDto.getLicenseExpiryDate());
        }
        if (vehicleInfoDto.getVehicleDocuments() != null) {
            deliveryPerson.setVehicleDocuments(vehicleInfoDto.getVehicleDocuments());
        }
        
        return deliveryPersonRepository.save(deliveryPerson);
    }

    // Get vehicle information for delivery person
    public VehicleInfoDto getVehicleInformation(Long deliveryPersonId) throws Exception {
        Optional<DeliveryPerson> deliveryPersonOpt = deliveryPersonRepository.findById(deliveryPersonId);
        if (!deliveryPersonOpt.isPresent()) {
            throw new Exception("Delivery person not found with ID: " + deliveryPersonId);
        }
        
        DeliveryPerson deliveryPerson = deliveryPersonOpt.get();
        
        return new VehicleInfoDto(
            deliveryPerson.getVehicleType(),
            deliveryPerson.getVehicleNumber(),
            deliveryPerson.getWeightCapacity(),
            deliveryPerson.getParcelCapacity(),
            deliveryPerson.getLicenseExpiryDate(),
            deliveryPerson.getVehicleDocuments()
        );
    }
}