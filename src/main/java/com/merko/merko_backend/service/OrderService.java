package com.merko.merko_backend.service;

import com.merko.merko_backend.entity.*;
import com.merko.merko_backend.dto.*;
import com.merko.merko_backend.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private static final Logger logger = LoggerFactory.getLogger(OrderService.class);

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private ProductRepository productRepository;

    public List<OrderSummaryDTO> getMyOrders(String userEmail) {
        logger.info("🔍 Fetching orders for user: {}", userEmail);

        // Get user from users table
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + userEmail));

        // Verify user is a merchant
        if (!user.getRole().toString().equals("MERCHANT")) {
            throw new RuntimeException("Only merchants can access this resource");
        }

        logger.info("✅ Merchant found: {} (ID: {})", user.getFirstName(), user.getId());

        // Find orders for this user
        List<Order> orders = orderRepository.findByUserOrderByOrderDateDesc(user);
        logger.info("📦 Found {} orders for merchant", orders.size());

        return orders.stream()
                .map(this::convertToSummaryDTO)
                .collect(Collectors.toList());
    }

    public OrderDetailsDTO getOrderDetails(Long orderId, String userEmail) {
        logger.info("🔍 Fetching order details for order: {} by user: {}", orderId, userEmail);

        // Get user from users table
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + userEmail));

        // Verify user is a merchant
        if (!user.getRole().toString().equals("MERCHANT")) {
            throw new RuntimeException("Only merchants can access this resource");
        }

        Order order = orderRepository.findByIdWithItemsAndProducts(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with ID: " + orderId));

        // Verify ownership - check if order belongs to this user
        if (!order.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Access denied: This order does not belong to you");
        }

        return convertToDetailsDTO(order);
    }

    @Transactional
    public OrderDetailsDTO updateOrderItemQuantity(Long orderId, Long itemId, Integer quantity, String userEmail) {
        validateOrderOwnership(orderId, userEmail);

        OrderItem orderItem = orderItemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Order item not found with ID: " + itemId));

        // Verify that the order item belongs to the order
        if (!orderItem.getOrder().getId().equals(orderId)) {
            throw new RuntimeException("Order item does not belong to this order");
        }

        // Only allow quantity changes for PENDING items
        if (orderItem.getStatus() != OrderStatus.PENDING) {
            throw new RuntimeException("Can only update quantity for PENDING items");
        }

        if (quantity == null || quantity < 1) {
            throw new RuntimeException("Invalid quantity");
        }

        orderItem.setQuantity(quantity);
        orderItem.calculateTotal();
        orderItemRepository.save(orderItem);

        logger.info("✅ Order item quantity updated - Order: {}, Item: {}, New Quantity: {}",
                orderId, itemId, quantity);

        return getUpdatedOrderDetails(orderId);
    }

    @Transactional
    public OrderDetailsDTO updateOrderItemStatus(Long orderId, Long itemId, String status, String userEmail) {
        validateOrderOwnership(orderId, userEmail);

        OrderItem orderItem = orderItemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Order item not found with ID: " + itemId));

        // Verify that the order item belongs to the order
        if (!orderItem.getOrder().getId().equals(orderId)) {
            throw new RuntimeException("Order item does not belong to this order");
        }

        OrderStatus newStatus = OrderStatus.valueOf(status.toUpperCase());
        orderItem.setStatus(newStatus);
        orderItemRepository.save(orderItem);

        logger.info("✅ Order item status updated - Order: {}, Item: {}, New Status: {}",
                orderId, itemId, newStatus);

        return getUpdatedOrderDetails(orderId);
    }

    @Transactional
    public OrderDetailsDTO deleteOrderItem(Long orderId, Long itemId, String userEmail) {
        validateOrderOwnership(orderId, userEmail);

        OrderItem orderItem = orderItemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Order item not found with ID: " + itemId));

        // Verify that the order item belongs to the order
        if (!orderItem.getOrder().getId().equals(orderId)) {
            throw new RuntimeException("Order item does not belong to this order");
        }

        // Only allow deletion for PENDING items
        if (orderItem.getStatus() != OrderStatus.PENDING) {
            throw new RuntimeException("Can only delete PENDING items");
        }

        orderItemRepository.delete(orderItem);

        logger.info("✅ Order item deleted - Order: {}, Item: {}", orderId, itemId);

        return getUpdatedOrderDetails(orderId);
    }

    @Transactional
    public void cancelOrder(Long orderId, String userEmail) {
        logger.info("🔄 Cancelling order: {} by user: {}", orderId, userEmail);

        // Get user from users table
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + userEmail));

        // Verify user is a merchant
        if (!user.getRole().toString().equals("MERCHANT")) {
            throw new RuntimeException("Only merchants can access this resource");
        }

        Order order = orderRepository.findByIdWithItems(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with ID: " + orderId));

        // Verify ownership - check if order belongs to this user
        if (!order.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Access denied: This order does not belong to you");
        }

        // Check if any item is already delivered or shipped (can't cancel those)
        boolean hasNonCancellableItems = order.getOrderItems().stream()
                .anyMatch(item -> item.getStatus() == OrderStatus.DELIVERED || item.getStatus() == OrderStatus.SHIPPED);

        if (hasNonCancellableItems) {
            throw new RuntimeException("Cannot cancel order. Some items are already shipped or delivered.");
        }

        // Cancel all order items
        for (OrderItem item : order.getOrderItems()) {
            if (item.getStatus() == OrderStatus.PENDING || item.getStatus() == OrderStatus.CONFIRMED) {
                item.setStatus(OrderStatus.CANCELLED);
                orderItemRepository.save(item);
            }
        }

        logger.info("✅ Order {} cancelled by merchant {}", orderId, userEmail);
    }

    // Private helper methods
    private void validateOrderOwnership(Long orderId, String userEmail) {
        logger.debug("🔍 Validating order ownership - Order: {}, User: {}", orderId, userEmail);

        // Get user from users table
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + userEmail));

        // Verify user is a merchant
        if (!user.getRole().toString().equals("MERCHANT")) {
            throw new RuntimeException("Only merchants can access this resource");
        }

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with ID: " + orderId));

        // Verify ownership - check if order belongs to this user
        if (!order.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Access denied: This order does not belong to you");
        }
    }

    private OrderDetailsDTO getUpdatedOrderDetails(Long orderId) {
        Order updatedOrder = orderRepository.findByIdWithItemsAndProducts(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found after update with ID: " + orderId));
        return convertToDetailsDTO(updatedOrder);
    }

    private String calculateOverallStatus(Order order) {
        if (order.getOrderItems() == null || order.getOrderItems().isEmpty()) {
            return "PENDING";
        }

        List<OrderItem> items = order.getOrderItems();

        // If any item is cancelled, show partially cancelled
        if (items.stream().anyMatch(item -> item.getStatus() == OrderStatus.CANCELLED)) {
            return "PARTIALLY_CANCELLED";
        }

        // If all items are delivered, show delivered
        if (items.stream().allMatch(item -> item.getStatus() == OrderStatus.DELIVERED)) {
            return "DELIVERED";
        }

        // If any item is shipped, show shipped
        if (items.stream().anyMatch(item -> item.getStatus() == OrderStatus.SHIPPED)) {
            return "SHIPPED";
        }

        // If any item is confirmed, show confirmed
        if (items.stream().anyMatch(item -> item.getStatus() == OrderStatus.CONFIRMED)) {
            return "CONFIRMED";
        }

        return "PENDING";
    }

    private OrderSummaryDTO convertToSummaryDTO(Order order) {
        OrderSummaryDTO dto = new OrderSummaryDTO();
        dto.setId(order.getId());
        dto.setOrderNumber(order.getOrderNumber());
        dto.setStatus(calculateOverallStatus(order));
        dto.setTotalAmount(order.getTotalAmount());
        dto.setOrderDate(order.getOrderDate());
        dto.setItemCount(order.getOrderItems() != null ? order.getOrderItems().size() : 0);
        dto.setShippingMethod(order.getShippingMethod() != null ? order.getShippingMethod().toString() : "STANDARD");

        return dto;
    }

    private OrderDetailsDTO convertToDetailsDTO(Order order) {
        OrderDetailsDTO dto = new OrderDetailsDTO();
        dto.setId(order.getId());
        dto.setOrderNumber(order.getOrderNumber());
        dto.setStatus(calculateOverallStatus(order));
        dto.setSubtotal(order.getSubtotal());
        dto.setTaxAmount(order.getTaxAmount());
        dto.setShippingCost(order.getShippingCost());
        dto.setTotalAmount(order.getTotalAmount());
        dto.setOrderDate(order.getOrderDate());
        dto.setUpdatedAt(order.getUpdatedAt());

        // Shipping Information
        dto.setShippingFirstName(order.getShippingFirstName());
        dto.setShippingLastName(order.getShippingLastName());
        dto.setShippingCompanyName(order.getShippingCompanyName());
        dto.setShippingAddress(order.getShippingAddress());
        dto.setShippingApartment(order.getShippingApartment());
        dto.setShippingCity(order.getShippingCity());
        dto.setShippingState(order.getShippingState());
        dto.setShippingZipCode(order.getShippingZipCode());
        dto.setShippingPhone(order.getShippingPhone());

        // Payment Information
        dto.setPaymentMethod(order.getPaymentMethod() != null ? order.getPaymentMethod().toString() : null);
        dto.setCardLastFour(order.getCardLastFour());
        dto.setCardHolderName(order.getCardHolderName());
        dto.setCardExpiration(order.getCardExpiration());
        dto.setShippingMethod(order.getShippingMethod() != null ? order.getShippingMethod().toString() : null);

        // Convert order items with their individual statuses
        if (order.getOrderItems() != null) {
            List<OrderItemDTO> itemDTOs = order.getOrderItems().stream()
                    .map(this::convertToOrderItemDTO)
                    .collect(Collectors.toList());
            dto.setOrderItems(itemDTOs);
        }

        return dto;
    }

    private OrderItemDTO convertToOrderItemDTO(OrderItem item) {
        OrderItemDTO itemDTO = new OrderItemDTO();
        itemDTO.setId(item.getId());

        if (item.getProduct() != null) {
            itemDTO.setProductId(item.getProduct().getId());
            itemDTO.setProductName(item.getProduct().getProductName() != null ?
                    item.getProduct().getProductName() : "Product Not Available");
            itemDTO.setProductSku(item.getProduct().getSku() != null ?
                    item.getProduct().getSku() : "N/A");
        } else {
            itemDTO.setProductId(null);
            itemDTO.setProductName("Product Not Available");
            itemDTO.setProductSku("N/A");
        }

        itemDTO.setQuantity(item.getQuantity());
        itemDTO.setPriceAtTime(item.getPriceAtTime());
        itemDTO.setTotalPrice(item.getTotalPrice());
        itemDTO.setStatus(item.getStatus() != null ? item.getStatus().toString() : "PENDING");
        return itemDTO;
    }
}