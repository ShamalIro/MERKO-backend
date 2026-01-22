package com.merko.merko_backend.service;

import com.merko.merko_backend.dto.*;
import com.merko.merko_backend.entity.*;
import com.merko.merko_backend.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;

@Service
public class CheckoutService {

    private static final Logger logger = LoggerFactory.getLogger(CheckoutService.class);

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Transactional
    public Order processCheckout(String userEmail, CheckoutRequest checkoutRequest) {
        logger.info("🔄 Processing checkout for user: {}", userEmail);

        // Find user
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Verify user is a merchant
        if (!user.getRole().toString().equals("MERCHANT")) {
            throw new RuntimeException("Only merchants can checkout");
        }

        logger.info("✅ Merchant found: {} {} (ID: {})", user.getFirstName(), user.getLastName(), user.getId());

        // Find active cart using User
        Cart cart = cartRepository.findActiveCartByUser(user)
                .orElseThrow(() -> new RuntimeException("No active cart found"));

        if (cart.getCartItems().isEmpty()) {
            throw new RuntimeException("Cart is empty");
        }

        logger.info("📦 Cart found with {} items", cart.getCartItems().size());

        // ✅ REMOVED: Stock validation - allow orders even with insufficient stock
        // validateStockAvailability(cart);

        // Calculate totals
        BigDecimal subtotal = calculateSubtotal(cart);
        BigDecimal taxRate = new BigDecimal("0.08");
        BigDecimal taxAmount = subtotal.multiply(taxRate);

        ShippingMethod shippingMethod = ShippingMethod.valueOf(checkoutRequest.getShippingMethod());
        BigDecimal shippingCost = shippingMethod == ShippingMethod.EXPRESS
                ? new BigDecimal("25.00")
                : BigDecimal.ZERO;

        BigDecimal totalAmount = subtotal.add(taxAmount).add(shippingCost);

        logger.info("💰 Order totals - Subtotal: {}, Tax: {}, Shipping: {}, Total: {}",
                subtotal, taxAmount, shippingCost, totalAmount);

        // Create order
        Order order = createOrder(user, checkoutRequest, subtotal, taxAmount, shippingCost, totalAmount, shippingMethod);

        // Create order items from cart items
        for (CartItem cartItem : cart.getCartItems()) {
            OrderItem orderItem = createOrderItem(cartItem);
            order.addOrderItem(orderItem);
        }

        // Save order
        Order savedOrder = orderRepository.save(order);
        logger.info("✅ Order created successfully - Order #: {}", savedOrder.getOrderNumber());

        // Clear cart and mark as ORDERED
        clearCart(cart);
        logger.info("🛒 Cart cleared and marked as ORDERED");

        return savedOrder;
    }

    private void validateStockAvailability(Cart cart) {
        logger.info("🔍 Validating stock availability...");

        for (CartItem cartItem : cart.getCartItems()) {
            Product product = cartItem.getProduct();
            if (product == null) {
                throw new RuntimeException("Product not found for cart item");
            }

            int orderedQuantity = cartItem.getQuantity();
            int availableStock = product.getStockQuantity() != null ? product.getStockQuantity() : 0;

            logger.info("📊 Product: {}, Available Stock: {}, Ordered: {}",
                    product.getProductName(), availableStock, orderedQuantity);

            if (availableStock < orderedQuantity) {
                throw new RuntimeException(
                        String.format("Insufficient stock for product '%s'. Available: %d, Requested: %d",
                                product.getProductName(), availableStock, orderedQuantity)
                );
            }
        }
        logger.info("✅ All products have sufficient stock");
    }

    private BigDecimal calculateSubtotal(Cart cart) {
        BigDecimal subtotal = cart.getCartItems().stream()
                .map(item -> {
                    if (item.getPriceAtTime() == null) {
                        // Fallback to product price if priceAtTime is not set
                        BigDecimal price = item.getProduct().getPrice() != null ?
                                item.getProduct().getPrice() : BigDecimal.ZERO;
                        return price.multiply(BigDecimal.valueOf(item.getQuantity()));
                    }
                    return item.getPriceAtTime().multiply(BigDecimal.valueOf(item.getQuantity()));
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        logger.info("🧮 Calculated subtotal: {}", subtotal);
        return subtotal;
    }

    private Order createOrder(User user, CheckoutRequest checkoutRequest,
                              BigDecimal subtotal, BigDecimal taxAmount,
                              BigDecimal shippingCost, BigDecimal totalAmount,
                              ShippingMethod shippingMethod) {
        Order order = new Order();
        order.setUser(user);

        // Set shipping info
        ShippingInfo shippingInfo = checkoutRequest.getShippingInfo();
        order.setShippingFirstName(shippingInfo.getFirstName());
        order.setShippingLastName(shippingInfo.getLastName());
        order.setShippingCompanyName(shippingInfo.getCompanyName());
        order.setShippingAddress(shippingInfo.getAddress());
        order.setShippingApartment(shippingInfo.getApartment());
        order.setShippingCity(shippingInfo.getCity());
        order.setShippingState(shippingInfo.getState());
        order.setShippingZipCode(shippingInfo.getZipCode());
        order.setShippingPhone(shippingInfo.getPhoneNumber());

        // Set payment info
        PaymentInfo paymentInfo = checkoutRequest.getPaymentInfo();
        PaymentMethod paymentMethod = PaymentMethod.valueOf(paymentInfo.getMethod());
        order.setPaymentMethod(paymentMethod);

        if (paymentMethod == PaymentMethod.CREDIT_CARD) {
            String cardNumber = paymentInfo.getCardNumber();
            if (cardNumber != null && cardNumber.length() > 4) {
                order.setCardLastFour(cardNumber.substring(cardNumber.length() - 4));
            } else {
                order.setCardLastFour(cardNumber);
            }
            order.setCardExpiration(paymentInfo.getExpirationDate());
            order.setCardHolderName(paymentInfo.getCardHolderName());
        }

        // Set totals and shipping
        order.setSubtotal(subtotal);
        order.setTaxAmount(taxAmount);
        order.setShippingCost(shippingCost);
        order.setTotalAmount(totalAmount);
        order.setShippingMethod(shippingMethod);

        logger.info("📝 Order created for user: {}", user.getEmail());
        return order;
    }

    private OrderItem createOrderItem(CartItem cartItem) {
        OrderItem orderItem = new OrderItem();
        orderItem.setProduct(cartItem.getProduct());
        orderItem.setQuantity(cartItem.getQuantity());

        // Use priceAtTime from cart item, or fallback to product price
        if (cartItem.getPriceAtTime() != null) {
            orderItem.setPriceAtTime(cartItem.getPriceAtTime());
        } else {
            orderItem.setPriceAtTime(cartItem.getProduct().getPrice());
        }

        orderItem.setStatus(OrderStatus.PENDING);
        orderItem.calculateTotal();

        logger.info("📦 Order item created - Product: {}, Quantity: {}, Price: {}",
                cartItem.getProduct().getProductName(), cartItem.getQuantity(), orderItem.getPriceAtTime());
        return orderItem;
    }

    private void clearCart(Cart cart) {
        cart.getCartItems().clear();
        cart.setStatus("ORDERED");
        cartRepository.save(cart);
        logger.info("🧹 Cart cleared for user");
    }
}