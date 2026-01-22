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
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CartService {

    private static final Logger logger = LoggerFactory.getLogger(CartService.class);

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private UserRepository userRepository; // Changed from MerchantRepository

    @Autowired
    private ProductRepository productRepository;

    @Transactional
    public void addToCart(AddToCartRequest request, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Verify user is a merchant
        if (!user.getRole().toString().equals("MERCHANT")) {
            throw new RuntimeException("Only merchants can add items to cart");
        }

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        // Check if product is active
        if (!"Active".equals(product.getStatus())) {
            throw new RuntimeException("Product is not available");
        }

        // REMOVED: Stock quantity validation - allow any quantity in cart

        // Find or create active cart for user
        Cart cart = cartRepository.findActiveCartByUser(user)
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setUser(user);
                    newCart.setStatus("ACTIVE");
                    return cartRepository.save(newCart);
                });

        // Check if item already exists in cart
        Optional<CartItem> existingItem = cartItemRepository.findByCartAndProduct(cart, product);

        if (existingItem.isPresent()) {
            // Update quantity - allow any quantity without stock validation
            CartItem cartItem = existingItem.get();
            int newQuantity = cartItem.getQuantity() + request.getQuantity();

            // No stock validation - store whatever quantity user wants
            cartItem.setQuantity(newQuantity);
            cartItemRepository.save(cartItem);
            logger.info("Updated cart item quantity - Product: {}, New Quantity: {}", product.getProductName(), newQuantity);
        } else {
            // Create new cart item - allow any quantity without stock validation
            CartItem cartItem = new CartItem();
            cartItem.setCart(cart);
            cartItem.setProduct(product);
            cartItem.setQuantity(request.getQuantity()); // Store exact requested quantity
            cartItem.setPriceAtTime(product.getPrice()); // Store current price
            cartItemRepository.save(cartItem);
            cart.addCartItem(cartItem);
            logger.info("Added new item to cart - Product: {}, Quantity: {}", product.getProductName(), request.getQuantity());
        }

        logger.info("Product {} added to cart for user {} with quantity {}", product.getProductName(), user.getEmail(), request.getQuantity());
    }

    public CartDTO getMyCart(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Verify user is a merchant
        if (!user.getRole().toString().equals("MERCHANT")) {
            throw new RuntimeException("Only merchants can view cart");
        }

        // Find active cart
        Optional<Cart> cartOpt = cartRepository.findActiveCartByUser(user);

        if (cartOpt.isEmpty()) {
            // Return empty cart
            CartDTO emptyCart = new CartDTO();
            emptyCart.setCartItems(List.of());
            emptyCart.setSubtotal(BigDecimal.ZERO);
            emptyCart.setTotalQuantity(0);
            emptyCart.setStatus("ACTIVE");
            emptyCart.setUserEmail(userEmail);
            emptyCart.setUserName(getUserDisplayName(user));
            return emptyCart;
        }

        Cart cart = cartOpt.get();
        return convertToCartDTO(cart);
    }

    @Transactional
    public void updateCartItem(Long cartItemId, UpdateCartItemRequest request, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Verify user is a merchant
        if (!user.getRole().toString().equals("MERCHANT")) {
            throw new RuntimeException("Only merchants can update cart");
        }

        // Find cart item
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new RuntimeException("Cart item not found"));

        // Verify ownership
        if (!cartItem.getCart().getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Access denied");
        }

        // REMOVED: Stock validation - allow any quantity
        // Update quantity with exact value user provided
        cartItem.setQuantity(request.getQuantity());
        cartItemRepository.save(cartItem);

        logger.info("Cart item updated - ID: {}, Quantity: {}", cartItemId, request.getQuantity());
    }

    @Transactional
    public void removeCartItem(Long cartItemId, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Verify user is a merchant
        if (!user.getRole().toString().equals("MERCHANT")) {
            throw new RuntimeException("Only merchants can remove cart items");
        }

        // Find cart item
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new RuntimeException("Cart item not found"));

        // Verify ownership
        if (!cartItem.getCart().getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Access denied");
        }

        // Remove cart item
        cartItemRepository.delete(cartItem);
        logger.info("Cart item removed - ID: {}, Product: {}", cartItemId, cartItem.getProduct().getProductName());
    }

    @Transactional
    public void clearCart(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Verify user is a merchant
        if (!user.getRole().toString().equals("MERCHANT")) {
            throw new RuntimeException("Only merchants can clear cart");
        }

        // Find active cart
        Optional<Cart> cartOpt = cartRepository.findActiveCartByUser(user);

        if (cartOpt.isPresent()) {
            Cart cart = cartOpt.get();
            cartItemRepository.deleteAll(cart.getCartItems());
            cart.getCartItems().clear();
            cartRepository.save(cart);
            logger.info("Cart cleared for user: {}", userEmail);
        }
    }

    // Private helper methods
    private CartDTO convertToCartDTO(Cart cart) {
        CartDTO cartDTO = new CartDTO();
        cartDTO.setId(cart.getId());
        cartDTO.setUserEmail(cart.getUser().getEmail());
        cartDTO.setUserName(getUserDisplayName(cart.getUser()));
        cartDTO.setStatus(cart.getStatus());
        cartDTO.setCreatedAt(cart.getCreatedAt());
        cartDTO.setUpdatedAt(cart.getUpdatedAt());

        // Convert cart items
        List<CartItemDTO> cartItemDTOs = cart.getCartItems().stream()
                .map(this::convertToCartItemDTO)
                .collect(Collectors.toList());
        cartDTO.setCartItems(cartItemDTOs);

        // Calculate totals
        BigDecimal subtotal = cartItemDTOs.stream()
                .map(CartItemDTO::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Integer totalQuantity = cartItemDTOs.stream()
                .mapToInt(CartItemDTO::getQuantity)
                .sum();

        cartDTO.setSubtotal(subtotal);
        cartDTO.setTotalQuantity(totalQuantity);

        logger.debug("Cart converted to DTO - ID: {}, Items: {}, Subtotal: {}",
                cart.getId(), cartItemDTOs.size(), subtotal);

        return cartDTO;
    }

    private CartItemDTO convertToCartItemDTO(CartItem cartItem) {
        CartItemDTO dto = new CartItemDTO();
        dto.setId(cartItem.getId());
        dto.setProductId(cartItem.getProduct().getId());
        dto.setProductName(cartItem.getProduct().getProductName());
        dto.setProductSku(cartItem.getProduct().getSku());
        dto.setBrand(cartItem.getProduct().getBrand());
        dto.setCategory(cartItem.getProduct().getCategory());
        dto.setPriceAtTime(cartItem.getPriceAtTime());
        dto.setQuantity(cartItem.getQuantity());
        dto.setTotal(cartItem.getTotal());
        dto.setStockQuantity(cartItem.getProduct().getStockQuantity());
        dto.setCreatedAt(cartItem.getCreatedAt());

        // Get supplier company name from product
        dto.setSupplierCompanyName(cartItem.getProduct().getSupplierCompanyName());

        if (cartItem.getProduct().getImages() != null && !cartItem.getProduct().getImages().isEmpty()) {
            dto.setImageUrls(cartItem.getProduct().getImages().stream()
                    .map(ProductImage::getImageUrl)
                    .collect(Collectors.toList()));
        }

        return dto;
    }

    private String getUserDisplayName(User user) {
        if (user.getCompanyName() != null && !user.getCompanyName().trim().isEmpty()) {
            return user.getCompanyName();
        }
        return user.getFirstName() + " " + user.getLastName();
    }
}