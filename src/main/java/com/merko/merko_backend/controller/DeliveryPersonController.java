package com.merko.merko_backend.controller;

import com.merko.merko_backend.entity.DeliveryPerson;
import com.merko.merko_backend.service.DeliveryPersonService;
import com.merko.merko_backend.dto.VehicleInfoDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/delivery-persons")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174"})
public class DeliveryPersonController {
    
    @Autowired
    private DeliveryPersonService deliveryPersonService;
    
    // Create a new delivery person (Admin only)
    @PostMapping("/create")
    public ResponseEntity<Map<String, Object>> createDeliveryPerson(@RequestBody Map<String, String> request) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            String firstName = request.get("firstName");
            String lastName = request.get("lastName");
            String email = request.get("email");
            String phoneNumber = request.get("phoneNumber");
            String password = request.get("password");
            String licenseNumber = request.get("licenseNumber");
            String vehicleType = request.get("vehicleType");
            String vehicleNumber = request.get("vehicleNumber");
            String address = request.get("address");
            
            DeliveryPerson deliveryPerson = deliveryPersonService.createDeliveryPerson(
                firstName, lastName, email, phoneNumber, password, 
                licenseNumber, vehicleType, vehicleNumber, address
            );
            
            response.put("success", true);
            response.put("message", "Delivery person created successfully!");
            response.put("deliveryPersonId", deliveryPerson.getDeliveryPersonId());
            response.put("firstName", deliveryPerson.getFirstName());
            response.put("lastName", deliveryPerson.getLastName());
            response.put("email", deliveryPerson.getEmail());
            response.put("status", deliveryPerson.getStatus());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    // Get all delivery persons
    @GetMapping
    public ResponseEntity<List<DeliveryPerson>> getAllDeliveryPersons() {
        try {
            List<DeliveryPerson> deliveryPersons = deliveryPersonService.getAllDeliveryPersons();
            return ResponseEntity.ok(deliveryPersons);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    // Get delivery person by ID
    @GetMapping("/{id}")
    public ResponseEntity<DeliveryPerson> getDeliveryPersonById(@PathVariable Long id) {
        try {
            Optional<DeliveryPerson> deliveryPerson = deliveryPersonService.getDeliveryPersonById(id);
            return deliveryPerson.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    // Get active delivery persons
    @GetMapping("/active")
    public ResponseEntity<List<DeliveryPerson>> getActiveDeliveryPersons() {
        try {
            List<DeliveryPerson> deliveryPersons = deliveryPersonService.getActiveDeliveryPersons();
            return ResponseEntity.ok(deliveryPersons);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    // Update delivery person status
    @PutMapping("/{id}/status")
    public ResponseEntity<Map<String, Object>> updateDeliveryPersonStatus(@PathVariable Long id, @RequestBody Map<String, String> request) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            String status = request.get("status");
            DeliveryPerson deliveryPerson = deliveryPersonService.updateDeliveryPersonStatus(id, status);
            
            response.put("success", true);
            response.put("message", "Delivery person status updated successfully");
            response.put("deliveryPerson", deliveryPerson);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    // Update delivery person details
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateDeliveryPerson(@PathVariable Long id, @RequestBody DeliveryPerson updatedDeliveryPerson) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            DeliveryPerson deliveryPerson = deliveryPersonService.updateDeliveryPerson(id, updatedDeliveryPerson);
            
            response.put("success", true);
            response.put("message", "Delivery person updated successfully");
            response.put("deliveryPerson", deliveryPerson);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    // Delete delivery person
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteDeliveryPerson(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            deliveryPersonService.deleteDeliveryPerson(id);
            
            response.put("success", true);
            response.put("message", "Delivery person deleted successfully");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // Update vehicle information for delivery person
    @PutMapping("/{id}/vehicle")
    public ResponseEntity<Map<String, Object>> updateVehicleInformation(@PathVariable Long id, @RequestBody VehicleInfoDto vehicleInfoDto) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            DeliveryPerson updatedDeliveryPerson = deliveryPersonService.updateVehicleInformation(id, vehicleInfoDto);
            
            response.put("success", true);
            response.put("message", "Vehicle information updated successfully");
            response.put("deliveryPerson", updatedDeliveryPerson);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // Get vehicle information for delivery person
    @GetMapping("/{id}/vehicle")
    public ResponseEntity<Map<String, Object>> getVehicleInformation(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            VehicleInfoDto vehicleInfo = deliveryPersonService.getVehicleInformation(id);
            
            response.put("success", true);
            response.put("vehicleInfo", vehicleInfo);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}