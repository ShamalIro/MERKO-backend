package com.merko.merko_backend.service;

import com.merko.merko_backend.entity.Route;
import com.merko.merko_backend.entity.RouteStop;
import com.merko.merko_backend.entity.DeliveryEntry;
import com.merko.merko_backend.repository.RouteRepository;
import com.merko.merko_backend.repository.RouteStopRepository;
import com.merko.merko_backend.repository.DeliveryEntryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

@Service
public class RouteService {
    
    @Autowired
    private RouteRepository routeRepository;
    
    @Autowired
    private RouteStopRepository routeStopRepository;
    
    @Autowired
    private DeliveryEntryRepository deliveryEntryRepository;
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    // Get all routes
    public List<Route> getAllRoutes() {
        return routeRepository.findAllByOrderByCreatedAtDesc();
    }
    
    // Get active routes
    public List<Route> getActiveRoutes() {
        return routeRepository.findActiveRoutes();
    }
    
    // Generate optimal route from delivery entries
    @Transactional
    public Route generateOptimalRoute() throws Exception {
        // Get all delivery entries that are ready for delivery
        List<DeliveryEntry> deliveryEntries = deliveryEntryRepository.findByStatus("Ready for delivery");
        
        if (deliveryEntries.isEmpty()) {
            throw new Exception("No delivery entries found that are ready for delivery");
        }
        
        // Extract unique delivery addresses
        Set<String> uniqueAddresses = new HashSet<>();
        Map<String, List<DeliveryEntry>> addressToEntries = new HashMap<>();
        
        for (DeliveryEntry entry : deliveryEntries) {
            String address = entry.getDeliveryAddress();
            if (address != null && !address.trim().isEmpty()) {
                uniqueAddresses.add(address);
                addressToEntries.computeIfAbsent(address, k -> new ArrayList<>()).add(entry);
            }
        }
        
        if (uniqueAddresses.isEmpty()) {
            throw new Exception("No valid delivery addresses found");
        }
        
        // Create route name with timestamp
        String routeName = "Optimized Route - " + LocalDateTime.now().toString().substring(0, 19);
        
        // For now, we'll create a simple route (you can enhance this with actual Google Maps optimization)
        List<String> sortedAddresses = new ArrayList<>(uniqueAddresses);
        Collections.sort(sortedAddresses); // Simple alphabetical sort (can be replaced with distance-based optimization)
        
        // Create route
        Route route = new Route(routeName, "Distribution Center", sortedAddresses.get(sortedAddresses.size() - 1));
        route.setTotalDistance(BigDecimal.valueOf(0)); // Will be calculated
        route.setEstimatedDuration(0); // Will be calculated
        
        // Convert addresses to JSON
        try {
            String addressesJson = objectMapper.writeValueAsString(sortedAddresses);
            route.setDeliveryAddresses(addressesJson);
        } catch (Exception e) {
            route.setDeliveryAddresses("[]");
        }
        
        // Save route
        Route savedRoute = routeRepository.save(route);
        
        // Create route stops
        int stopOrder = 1;
        for (String address : sortedAddresses) {
            List<DeliveryEntry> entriesForAddress = addressToEntries.get(address);
            for (DeliveryEntry entry : entriesForAddress) {
                RouteStop stop = new RouteStop(savedRoute.getRouteId(), entry.getDeliveryId(), stopOrder++, address);
                stop.setEstimatedArrivalTime(LocalTime.now().plusMinutes(stopOrder * 15)); // Estimate 15 mins per stop
                routeStopRepository.save(stop);
            }
        }
        
        return savedRoute;
    }
    
    // Get route with stops
    public Map<String, Object> getRouteWithStops(Long routeId) throws Exception {
        Optional<Route> routeOpt = routeRepository.findById(routeId);
        if (!routeOpt.isPresent()) {
            throw new Exception("Route not found with ID: " + routeId);
        }
        
        Route route = routeOpt.get();
        List<RouteStop> stops = routeStopRepository.findByRouteIdOrderByStopOrder(routeId);
        
        Map<String, Object> result = new HashMap<>();
        result.put("route", route);
        result.put("stops", stops);
        
        return result;
    }
    
    // Update route status
    public Route updateRouteStatus(Long routeId, String status) throws Exception {
        Optional<Route> routeOpt = routeRepository.findById(routeId);
        if (!routeOpt.isPresent()) {
            throw new Exception("Route not found with ID: " + routeId);
        }
        
        Route route = routeOpt.get();
        try {
            Route.RouteStatus newStatus = Route.RouteStatus.valueOf(status);
            route.setStatus(newStatus);
            return routeRepository.save(route);
        } catch (IllegalArgumentException e) {
            throw new Exception("Invalid status: " + status);
        }
    }
    
    // Delete route
    @Transactional
    public void deleteRoute(Long routeId) throws Exception {
        Optional<Route> routeOpt = routeRepository.findById(routeId);
        if (!routeOpt.isPresent()) {
            throw new Exception("Route not found with ID: " + routeId);
        }
        
        // Delete all stops for this route
        routeStopRepository.deleteByRouteId(routeId);
        
        // Delete the route
        routeRepository.deleteById(routeId);
    }
}