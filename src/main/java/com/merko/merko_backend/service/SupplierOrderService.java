package com.merko.merko_backend.service;

import com.merko.merko_backend.entity.*;
import com.merko.merko_backend.dto.SupplierOrderItemDTO;
import com.merko.merko_backend.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SupplierOrderService {

    private static final Logger logger = LoggerFactory.getLogger(SupplierOrderService.class);

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ConfirmedOrderRepository confirmedOrderRepository;

    public List<SupplierOrderItemDTO> getSupplierOrders(String userEmail) {
        logger.info("🔍 Fetching supplier orders for: {}", userEmail);

        // Get user from users table
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + userEmail));

        // Verify user is a supplier
        if (!user.getRole().toString().equals("SUPPLIER")) {
            throw new RuntimeException("Only suppliers can access this resource");
        }

        logger.info("✅ Supplier found: {} {} (ID: {})", user.getFirstName(), user.getLastName(), user.getId());

        // Use only the working query method
        List<OrderItem> orderItems;

        try {
            // Method 1: Try with direct userId
            orderItems = orderItemRepository.findByProductUserId(user.getId());
            logger.info("✅ Found {} order items using direct userId", orderItems.size());
        } catch (Exception e) {
            logger.warn("❌ Method 1 failed: {}", e.getMessage());

            // Fallback to manual filtering
            List<OrderItem> allOrderItems = orderItemRepository.findAllWithProductAndOrder();
            orderItems = allOrderItems.stream()
                    .filter(oi -> oi.getProduct() != null &&
                            oi.getProduct().getUserId() != null &&
                            oi.getProduct().getUserId().equals(user.getId()))
                    .collect(Collectors.toList());
            logger.info("✅ Fallback - Found {} order items using manual filtering", orderItems.size());
        }

        // Log order items for debugging
        for (OrderItem item : orderItems) {
            logger.info("📝 Order Item - ID: {}, Product: {} (Supplier ID: {}), Status: {}, Order: {}",
                    item.getId(),
                    item.getProduct() != null ? item.getProduct().getProductName() : "No Product",
                    item.getProduct() != null ? item.getProduct().getUserId() : "No Supplier",
                    item.getStatus(),
                    item.getOrder() != null ? item.getOrder().getOrderNumber() : "No Order"
            );
        }

        return orderItems.stream()
                .map(this::convertToSupplierDTO)
                .collect(Collectors.toList());
    }

    public List<SupplierOrderItemDTO> getNonPendingSupplierOrders(String userEmail) {
        logger.info("🔍 Fetching non-pending supplier orders for: {}", userEmail);

        // Get user from users table
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + userEmail));

        // Verify user is a supplier
        if (!user.getRole().toString().equals("SUPPLIER")) {
            throw new RuntimeException("Only suppliers can access this resource");
        }

        // Use only the working query method
        List<OrderItem> orderItems;

        try {
            orderItems = orderItemRepository.findByProductUserIdAndStatusNotPending(user.getId());
        } catch (Exception e) {
            logger.warn("Method failed, using fallback: {}", e.getMessage());
            // Fallback to manual filtering
            List<OrderItem> allOrderItems = orderItemRepository.findAllWithProductAndOrder();
            orderItems = allOrderItems.stream()
                    .filter(oi -> oi.getProduct() != null &&
                            oi.getProduct().getUserId() != null &&
                            oi.getProduct().getUserId().equals(user.getId()) &&
                            oi.getStatus() != OrderStatus.PENDING)
                    .collect(Collectors.toList());
        }

        logger.info("📦 Found {} non-pending order items for supplier", orderItems.size());

        return orderItems.stream()
                .map(this::convertToSupplierDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public void updateOrderItemStatus(Long itemId, String status, String userEmail) {
        logger.info("🔄 Updating order item {} status to {} for user: {}", itemId, status, userEmail);

        // Get user from users table
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + userEmail));

        // Verify user is a supplier
        if (!user.getRole().toString().equals("SUPPLIER")) {
            throw new RuntimeException("Only suppliers can perform this action");
        }

        OrderItem orderItem = orderItemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Order item not found with ID: " + itemId));

        // Verify that the product belongs to this supplier
        validateSupplierOwnership(orderItem, user.getId());

        if (status == null) {
            throw new RuntimeException("Status is required");
        }

        OrderStatus newOrderStatus = OrderStatus.valueOf(status.toUpperCase());
        OrderStatus currentStatus = orderItem.getStatus();

        logger.info("📋 Status change: {} → {}", currentStatus, newOrderStatus);

        // Handle stock management when confirming an order
        if (newOrderStatus == OrderStatus.CONFIRMED && currentStatus == OrderStatus.PENDING) {
            handleStockDeduction(orderItem);

            // Save to confirmed_orders table when confirming
            saveToConfirmedOrders(orderItem, user);
        }

        // Handle stock restoration when cancelling a confirmed order
        if (newOrderStatus == OrderStatus.CANCELLED && currentStatus == OrderStatus.CONFIRMED) {
            handleStockRestoration(orderItem);
        }

        orderItem.setStatus(newOrderStatus);
        orderItemRepository.save(orderItem);

        logger.info("✅ Order item {} status updated to {} by supplier {}", itemId, newOrderStatus, userEmail);
    }

    // Private helper methods
    private void validateSupplierOwnership(OrderItem orderItem, Long userId) {
        if (orderItem.getProduct() == null) {
            throw new RuntimeException("Product information missing for order item");
        }

        Long productSupplierId = orderItem.getProduct().getUserId();
        if (productSupplierId == null) {
            throw new RuntimeException("Product does not have a supplier assigned");
        }

        if (!productSupplierId.equals(userId)) {
            throw new RuntimeException("Access denied: This product does not belong to you. Product supplier ID: " + productSupplierId + ", Your ID: " + userId);
        }
    }

    private void handleStockDeduction(OrderItem orderItem) {
        Product product = orderItem.getProduct();
        int orderedQuantity = orderItem.getQuantity();
        int currentStock = product.getStockQuantity() != null ? product.getStockQuantity() : 0;

        logger.info("📊 Stock check - Product: {}, Current Stock: {}, Ordered: {}",
                product.getProductName(), currentStock, orderedQuantity);

        // Check if sufficient stock is available
        if (currentStock < orderedQuantity) {
            throw new RuntimeException("Insufficient stock. Available: " + currentStock + ", Ordered: " + orderedQuantity);
        }

        // Deduct stock
        product.setStockQuantity(currentStock - orderedQuantity);
        productRepository.save(product);

        logger.info("✅ Stock deducted for product {}: {} units. New stock: {}",
                product.getId(), orderedQuantity, (currentStock - orderedQuantity));
    }

    private void handleStockRestoration(OrderItem orderItem) {
        Product product = orderItem.getProduct();
        int orderedQuantity = orderItem.getQuantity();
        int currentStock = product.getStockQuantity() != null ? product.getStockQuantity() : 0;

        // Restore stock
        product.setStockQuantity(currentStock + orderedQuantity);
        productRepository.save(product);

        logger.info("🔄 Stock restored for product {}: {} units. New stock: {}",
                product.getId(), orderedQuantity, (currentStock + orderedQuantity));
    }

    // New method to save to confirmed_orders table
    private void saveToConfirmedOrders(OrderItem orderItem, User supplier) {
        try {
            ConfirmedOrder confirmedOrder = new ConfirmedOrder();

            // Set basic order information
            if (orderItem.getOrder() != null) {
                Order order = orderItem.getOrder();

                // Set merchant information from the order
                if (order.getUser() != null) {
                    User merchant = order.getUser();
                    confirmedOrder.setMerchantId(merchant.getId());
                    confirmedOrder.setMerchantName(
                            merchant.getCompanyName() != null ?
                                    merchant.getCompanyName() :
                                    merchant.getFirstName() + " " + merchant.getLastName()
                    );
                    confirmedOrder.setContactNumber(merchant.getPhoneNumber());
                }

                // Set delivery information - using shipping address from Order entity
                String deliveryAddress = buildDeliveryAddress(order);
                confirmedOrder.setDeliveryAddress(deliveryAddress);

                // Set delivery instructions as null as requested
                confirmedOrder.setDeliveryInstructions(null);

                confirmedOrder.setOrderDate(order.getOrderDate());

                // Convert BigDecimal to Double for totalAmount
                if (orderItem.getTotalPrice() != null) {
                    confirmedOrder.setTotalAmount(orderItem.getTotalPrice().doubleValue());
                } else {
                    confirmedOrder.setTotalAmount(0.0);
                }
            }

            // Set supplier information
            confirmedOrder.setSupplierId(supplier.getId());
            confirmedOrder.setSupplierName(
                    supplier.getCompanyName() != null ?
                            supplier.getCompanyName() :
                            supplier.getFirstName() + " " + supplier.getLastName()
            );

            // Set status to "Ready to Pick" as requested
            confirmedOrder.setStatus("Ready to Pick");

            // Route will be null as per requirement
            confirmedOrder.setRoute(null);

            // Save to confirmed_orders table
            confirmedOrderRepository.save(confirmedOrder);

            logger.info("✅ Order item {} saved to confirmed_orders table with ID: {}",
                    orderItem.getId(), confirmedOrder.getOrderId());

        } catch (Exception e) {
            logger.error("❌ Error saving to confirmed_orders table: {}", e.getMessage());
            throw new RuntimeException("Failed to save confirmed order: " + e.getMessage());
        }
    }

    // Helper method to build delivery address from Order entity fields
    private String buildDeliveryAddress(Order order) {
        StringBuilder address = new StringBuilder();

        if (order.getShippingAddress() != null) {
            address.append(order.getShippingAddress());
        }

        if (order.getShippingApartment() != null && !order.getShippingApartment().isEmpty()) {
            if (address.length() > 0) address.append(", ");
            address.append(order.getShippingApartment());
        }

        if (order.getShippingCity() != null && !order.getShippingCity().isEmpty()) {
            if (address.length() > 0) address.append(", ");
            address.append(order.getShippingCity());
        }

        if (order.getShippingState() != null && !order.getShippingState().isEmpty()) {
            if (address.length() > 0) address.append(", ");
            address.append(order.getShippingState());
        }

        if (order.getShippingZipCode() != null && !order.getShippingZipCode().isEmpty()) {
            if (address.length() > 0) address.append(" ");
            address.append(order.getShippingZipCode());
        }

        return address.length() > 0 ? address.toString() : "Address not available";
    }

    private SupplierOrderItemDTO convertToSupplierDTO(OrderItem item) {
        SupplierOrderItemDTO dto = new SupplierOrderItemDTO();
        dto.setId(item.getId());

        if (item.getProduct() != null) {
            dto.setProductName(item.getProduct().getProductName() != null ?
                    item.getProduct().getProductName() : "Product Not Available");
            dto.setProductSku(item.getProduct().getSku() != null ?
                    item.getProduct().getSku() : "N/A");
            dto.setCurrentStock(item.getProduct().getStockQuantity() != null ?
                    item.getProduct().getStockQuantity() : 0);

            // Use the stored supplier company name from product
            if (item.getProduct().getSupplierCompanyName() != null) {
                dto.setSupplierCompanyName(item.getProduct().getSupplierCompanyName());
            }
        } else {
            dto.setProductName("Product Not Available");
            dto.setProductSku("N/A");
            dto.setCurrentStock(0);
        }

        dto.setQuantity(item.getQuantity());
        dto.setPriceAtTime(item.getPriceAtTime());
        dto.setTotalPrice(item.getTotalPrice());
        dto.setStatus(item.getStatus() != null ? item.getStatus().toString() : "PENDING");

        if (item.getOrder() != null) {
            dto.setOrderDate(item.getOrder().getOrderDate());
            dto.setOrderNumber(item.getOrder().getOrderNumber() != null ?
                    item.getOrder().getOrderNumber() : "N/A");

            // Get merchant info from User entity
            if (item.getOrder().getUser() != null) {
                User merchantUser = item.getOrder().getUser();
                String companyName = merchantUser.getCompanyName() != null ?
                        merchantUser.getCompanyName() :
                        (merchantUser.getFirstName() + " " + merchantUser.getLastName());
                dto.setMerchantCompanyName(companyName);
            } else {
                dto.setMerchantCompanyName("Unknown Merchant");
            }
        } else {
            dto.setMerchantCompanyName("Unknown Merchant");
            dto.setOrderNumber("N/A");
        }

        return dto;
    }
}