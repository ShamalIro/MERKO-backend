package com.merko.merko_backend.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.merko.merko_backend.dto.ProductDTO;
import com.merko.merko_backend.entity.Product;
import com.merko.merko_backend.entity.ProductImage;
import com.merko.merko_backend.entity.User;
import com.merko.merko_backend.repository.ProductImageRepository;
import com.merko.merko_backend.repository.ProductRepository;
import com.merko.merko_backend.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.Normalizer;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ProductService {

    private static final Logger logger = LoggerFactory.getLogger(ProductService.class);

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductImageRepository productImageRepository;

    @Autowired
    private UserRepository userRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${file.upload-dir:uploads}")
    private String uploadDir;

    // Add SKU availability check method
    public boolean isSkuAvailable(String sku, String email) {
        try {
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // Check if SKU already exists for this user
            Optional<Product> existingProduct = productRepository.findBySkuAndUserId(sku, user.getId());

            boolean available = existingProduct.isEmpty();
            logger.info("SKU '{}' availability check for user {}: {}", sku, user.getId(), available);

            return available;
        } catch (Exception ex) {
            logger.error("Error checking SKU availability", ex);
            throw new RuntimeException("Failed to check SKU availability: " + ex.getMessage());
        }
    }

    public Product addProduct(String productStr, List<MultipartFile> images, String email) {
        try {
            logger.info("Adding product for user: {}", email);

            // Find user from users table
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // Verify user is a supplier
            if (!user.getRole().toString().equals("SUPPLIER")) {
                throw new RuntimeException("Only suppliers can add products");
            }

            // Deserialize JSON string
            ProductDTO productDTO = objectMapper.readValue(productStr, ProductDTO.class);
            logger.info("ProductDTO parsed: {}", productDTO.getProductName());

            // Check for duplicate SKU before creating product
            Optional<Product> existingProduct = productRepository.findBySkuAndUserId(productDTO.getSku(), user.getId());
            if (existingProduct.isPresent()) {
                throw new RuntimeException("SKU '" + productDTO.getSku() + "' is already in use. Please use a different SKU.");
            }

            // Map DTO -> entity
            Product product = new Product();
            product.setUserId(user.getId());
            product.setSupplierCompanyName(user.getCompanyName());
            product.setProductName(productDTO.getProductName());
            product.setDescription(productDTO.getDescription());
            product.setSku(productDTO.getSku());
            product.setCategory(productDTO.getCategory());
            product.setPrice(productDTO.getPrice());
            product.setCost(productDTO.getCost());
            product.setStockQuantity(productDTO.getStockQuantity());
            product.setWeight(productDTO.getWeight());
            product.setStatus(productDTO.getStatus());
            product.setBarcode(productDTO.getBarcode());
            product.setLowStockAlert(productDTO.getLowStockAlert());
            product.setTrackInventory(productDTO.getTrackInventory());
            product.setComparePrice(productDTO.getComparePrice());
            product.setProfitMargin(productDTO.getProfitMargin());
            product.setFeatures(productDTO.getFeatures());
            product.setCareInstructions(productDTO.getCareInstructions());
            product.setBrand(productDTO.getBrand());
            product.setCountryOfOrigin(productDTO.getCountryOfOrigin());

            // Handle images
            if (images != null && !images.isEmpty()) {
                logger.info("Number of images received: {}", images.size());
                handleProductImages(images, product);
            }

            // Save product
            Product savedProduct = productRepository.save(product);
            logger.info("Product with images saved successfully with ID: {}", savedProduct.getId());

            return savedProduct;

        } catch (Exception ex) {
            logger.error("Error saving product", ex);
            throw new RuntimeException("Failed to add product: " + ex.getMessage());
        }
    }

    public List<Product> getMyProducts(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return productRepository.findByUserId(user.getId());
    }

    public Product getProductById(Integer id, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        // Ensure the product belongs to the authenticated user
        if (!product.getUserId().equals(user.getId())) {
            throw new RuntimeException("Access denied");
        }

        // Force load images to avoid lazy loading issues
        product.getImages().size();

        logger.info("Product found: {} with {} images", product.getProductName(), product.getImages().size());
        return product;
    }

    public Product updateProduct(Integer id, String productStr, List<MultipartFile> newImages,
                                 String imagesToDeleteStr, String email) {
        try {
            logger.info("Updating product ID: {}", id);

            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            Product product = productRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Product not found"));

            // Security check
            if (!product.getUserId().equals(user.getId())) {
                throw new RuntimeException("Access denied");
            }

            // Parse product data
            ProductDTO productDTO = objectMapper.readValue(productStr, ProductDTO.class);

            // Check for duplicate SKU when SKU is being changed
            if (!product.getSku().equals(productDTO.getSku())) {
                Optional<Product> existingProduct = productRepository.findBySkuAndUserId(productDTO.getSku(), user.getId());
                if (existingProduct.isPresent()) {
                    throw new RuntimeException("SKU '" + productDTO.getSku() + "' is already in use. Please use a different SKU.");
                }
            }

            // Update product fields
            product.setProductName(productDTO.getProductName());
            product.setDescription(productDTO.getDescription());
            product.setSku(productDTO.getSku());
            product.setCategory(productDTO.getCategory());
            product.setPrice(productDTO.getPrice());
            product.setCost(productDTO.getCost());
            product.setStockQuantity(productDTO.getStockQuantity());
            product.setWeight(productDTO.getWeight());
            product.setStatus(productDTO.getStatus());
            product.setBarcode(productDTO.getBarcode());
            product.setLowStockAlert(productDTO.getLowStockAlert());
            product.setTrackInventory(productDTO.getTrackInventory());
            product.setComparePrice(productDTO.getComparePrice());
            product.setProfitMargin(productDTO.getProfitMargin());
            product.setFeatures(productDTO.getFeatures());
            product.setCareInstructions(productDTO.getCareInstructions());
            product.setBrand(productDTO.getBrand());
            product.setCountryOfOrigin(productDTO.getCountryOfOrigin());

            // Handle image deletions
            if (imagesToDeleteStr != null && !imagesToDeleteStr.isEmpty()) {
                handleImageDeletions(imagesToDeleteStr, product);
            }

            // Handle new images
            if (newImages != null && !newImages.isEmpty()) {
                handleProductImages(newImages, product);
            }

            Product updatedProduct = productRepository.save(product);
            logger.info("Product updated successfully: {}", updatedProduct.getId());

            return updatedProduct;

        } catch (Exception ex) {
            logger.error("Error updating product", ex);
            throw new RuntimeException("Failed to update product: " + ex.getMessage());
        }
    }

    public void deleteProduct(Integer id, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        // Security check
        if (!product.getUserId().equals(user.getId())) {
            throw new RuntimeException("Access denied");
        }

        // Delete associated image files
        for (ProductImage image : product.getImages()) {
            try {
                String filename = extractFilenameFromUrl(image.getImageUrl());
                Path filePath = Paths.get(uploadDir).resolve(filename);
                Files.deleteIfExists(filePath);
                logger.info("Deleted image file: {}", filename);
            } catch (Exception e) {
                logger.warn("Failed to delete image file: {}", e.getMessage());
            }
        }

        productRepository.delete(product);
        logger.info("Product deleted successfully: {}", id);
    }

    public List<ProductDTO> getAllProducts(String email, String role) {
        // Allow both merchants and suppliers to view all products
        if (!"MERCHANT".equals(role) && !"SUPPLIER".equals(role)) {
            throw new RuntimeException("Access denied");
        }

        // Fetch all products with necessary data loaded
        List<Product> products = productRepository.findAll();

        // Filter only active products for merchants
        if ("MERCHANT".equals(role)) {
            products = products.stream()
                    .filter(product -> "Active".equals(product.getStatus()))
                    .collect(Collectors.toList());
        }

        // Convert to DTOs to avoid lazy loading issues
        List<ProductDTO> productDTOs = products.stream().map(this::convertToDTO).collect(Collectors.toList());

        logger.info("Fetched {} products for role: {}", productDTOs.size(), role);
        return productDTOs;
    }

    // FIXED: Sanitize filenames to remove special characters
    private void handleProductImages(List<MultipartFile> images, Product product) throws Exception {
        Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
        Files.createDirectories(uploadPath);

        logger.info("Upload directory: {}", uploadPath.toString());

        for (MultipartFile file : images) {
            logger.info("Processing image: {} (size: {} bytes)", file.getOriginalFilename(), file.getSize());

            String originalFilename = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
            String sanitizedFilename = sanitizeFilename(originalFilename);
            String fileName = UUID.randomUUID() + "_" + sanitizedFilename;

            Path targetLocation = uploadPath.resolve(fileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            ProductImage img = new ProductImage();
            // Store just the filename, not the full path
            img.setImageUrl(fileName);
            img.setProduct(product);

            product.getImages().add(img);

            logger.info("Image saved: {} -> {}", file.getOriginalFilename(), fileName);
        }
    }

    // NEW METHOD: Sanitize filenames by removing special characters
    private String sanitizeFilename(String filename) {
        if (filename == null) return "file";

        // Get the file extension
        String extension = "";
        int lastDot = filename.lastIndexOf('.');
        if (lastDot > 0) {
            extension = filename.substring(lastDot);
            filename = filename.substring(0, lastDot);
        }

        // Remove or replace special characters
        // First normalize to decompose accented characters
        String normalized = Normalizer.normalize(filename, Normalizer.Form.NFD);
        // Remove all non-ASCII characters and special symbols
        String sanitized = normalized.replaceAll("[^a-zA-Z0-9_\\-]", "_");
        // Remove multiple underscores
        sanitized = sanitized.replaceAll("_+", "_");
        // Remove leading/trailing underscores
        sanitized = sanitized.replaceAll("^_|_$", "");

        // If filename is empty after sanitization, use a default
        if (sanitized.isEmpty()) {
            sanitized = "image";
        }

        return sanitized + extension;
    }

    private void handleImageDeletions(String imagesToDeleteStr, Product product) {
        String[] imageIds = imagesToDeleteStr.split(",");
        for (String imageIdStr : imageIds) {
            Long imageId = Long.parseLong(imageIdStr.trim());
            ProductImage imageToDelete = product.getImages().stream()
                    .filter(img -> img.getId().equals(imageId))
                    .findFirst()
                    .orElse(null);

            if (imageToDelete != null) {
                product.getImages().remove(imageToDelete);
                try {
                    String filename = extractFilenameFromUrl(imageToDelete.getImageUrl());
                    Path filePath = Paths.get(uploadDir).resolve(filename);
                    Files.deleteIfExists(filePath);
                    logger.info("Deleted image file during update: {}", filename);
                } catch (Exception e) {
                    logger.warn("Failed to delete image file: {}", e.getMessage());
                }
            }
        }
    }

    private String extractFilenameFromUrl(String imageUrl) {
        if (imageUrl == null) return null;
        if (imageUrl.contains("/")) {
            return imageUrl.substring(imageUrl.lastIndexOf("/") + 1);
        }
        return imageUrl;
    }

    private ProductDTO convertToDTO(Product product) {
        ProductDTO dto = new ProductDTO();
        dto.setId(product.getId());
        dto.setProductName(product.getProductName());
        dto.setDescription(product.getDescription());
        dto.setSku(product.getSku());
        dto.setCategory(product.getCategory());
        dto.setPrice(product.getPrice());
        dto.setCost(product.getCost());
        dto.setStockQuantity(product.getStockQuantity());
        dto.setWeight(product.getWeight());
        dto.setStatus(product.getStatus());
        dto.setBarcode(product.getBarcode());
        dto.setLowStockAlert(product.getLowStockAlert());
        dto.setTrackInventory(product.getTrackInventory());
        dto.setComparePrice(product.getComparePrice());
        dto.setProfitMargin(product.getProfitMargin());
        dto.setFeatures(product.getFeatures());
        dto.setCareInstructions(product.getCareInstructions());
        dto.setBrand(product.getBrand());
        dto.setCountryOfOrigin(product.getCountryOfOrigin());
        dto.setCreatedAt(product.getCreatedAt());
        dto.setUpdatedAt(product.getUpdatedAt());

        dto.setSupplierCompanyName(product.getSupplierCompanyName());

        if (product.getImages() != null && !product.getImages().isEmpty()) {
            List<String> imageUrls = product.getImages().stream()
                    .map(image -> {
                        String imageUrl = image.getImageUrl();
                        // Always return with /uploads/ prefix for consistency
                        if (imageUrl != null && imageUrl.startsWith("/uploads/")) {
                            return imageUrl;
                        } else {
                            return "/uploads/" + imageUrl;
                        }
                    })
                    .collect(Collectors.toList());

            dto.setImageUrls(imageUrls);
            logger.info("Product {}: Final image URLs: {}", product.getId(), imageUrls);
        } else {
            logger.info("Product {} has no images", product.getId());
        }

        return dto;
    }

    // Add these methods to your existing ProductService

    public List<ProductDTO> getProductsBySupplier(Long supplierId, String email) {
        try {
            logger.info("Fetching products for supplier ID: {}", supplierId);

            // Verify the requesting user exists
            User requestingUser = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // Verify the supplier exists
            User supplier = userRepository.findById(supplierId)
                    .orElseThrow(() -> new RuntimeException("Supplier not found"));

            if (!supplier.getRole().toString().equals("SUPPLIER")) {
                throw new RuntimeException("User is not a supplier");
            }

            // Get products for this supplier
            List<Product> products = productRepository.findBySupplierIdWithImages(supplierId);

            // Convert to DTOs
            List<ProductDTO> productDTOs = products.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());

            logger.info("Found {} products for supplier: {}", productDTOs.size(), supplier.getCompanyName());
            return productDTOs;

        } catch (Exception ex) {
            logger.error("Error fetching supplier products", ex);
            throw new RuntimeException("Failed to fetch supplier products: " + ex.getMessage());
        }
    }

    public List<ProductDTO> getActiveProductsBySupplier(Long supplierId, String email) {
        try {
            logger.info("Fetching active products for supplier ID: {}", supplierId);

            // Verify the requesting user exists (can be merchant or supplier)
            User requestingUser = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // Verify the supplier exists and is approved
            User supplier = userRepository.findById(supplierId)
                    .orElseThrow(() -> new RuntimeException("Supplier not found"));

            if (!supplier.getRole().toString().equals("SUPPLIER")) {
                throw new RuntimeException("User is not a supplier");
            }

            if (!supplier.getStatus().toString().equals("APPROVED")) {
                throw new RuntimeException("Supplier is not approved");
            }

            // Get only active products for this supplier
            List<Product> products = productRepository.findActiveProductsBySupplier(supplierId);

            // Convert to DTOs
            List<ProductDTO> productDTOs = products.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());

            logger.info("Found {} active products for supplier: {}", productDTOs.size(), supplier.getCompanyName());
            return productDTOs;

        } catch (Exception ex) {
            logger.error("Error fetching active supplier products", ex);
            throw new RuntimeException("Failed to fetch supplier products: " + ex.getMessage());
        }
    }
}