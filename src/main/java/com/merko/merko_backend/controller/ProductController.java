package com.merko.merko_backend.controller;

import com.merko.merko_backend.service.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/products")
@CrossOrigin(origins = "http://localhost:5173", allowedHeaders = "*")
public class ProductController {

    private static final Logger logger = LoggerFactory.getLogger(ProductController.class);

    @Autowired
    private ProductService productService;

    // Add SKU check endpoint
    @GetMapping("/check-sku/{sku}")
    public ResponseEntity<?> checkSkuAvailability(@PathVariable String sku,
                                                  @RequestParam String userEmail) {
        try {
            logger.info("Checking SKU availability: {} for user: {}", sku, userEmail);
            boolean isAvailable = productService.isSkuAvailable(sku, userEmail);

            return ResponseEntity.ok(Map.of(
                    "available", isAvailable,
                    "sku", sku
            ));
        } catch (Exception ex) {
            logger.error("Error checking SKU availability", ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", ex.getMessage()));
        }
    }

    @PostMapping(value = "/add", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> addProduct(@RequestPart("product") String productStr,
                                        @RequestPart(value = "images", required = false) List<MultipartFile> images,
                                        @RequestParam String userEmail) {
        try {
            logger.info("Received /add product request for user: {}", userEmail);
            var result = productService.addProduct(productStr, images, userEmail);
            return ResponseEntity.ok(result);
        } catch (Exception ex) {
            logger.error("Error saving product", ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", ex.getMessage()));
        }
    }

    @GetMapping("/my-products")
    public ResponseEntity<?> getMyProducts(@RequestParam String userEmail) {
        try {
            var result = productService.getMyProducts(userEmail);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            logger.error("Error fetching products", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getProductById(@PathVariable Integer id, @RequestParam String userEmail) {
        try {
            var result = productService.getProductById(id, userEmail);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            logger.error("Error fetching product", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", e.getMessage()));
        }
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateProduct(
            @PathVariable Integer id,
            @RequestPart("product") String productStr,
            @RequestPart(value = "images", required = false) List<MultipartFile> newImages,
            @RequestPart(value = "imagesToDelete", required = false) String imagesToDeleteStr,
            @RequestParam String userEmail) {
        try {
            logger.info("Received update request for product ID: {} for user: {}", id, userEmail);
            var result = productService.updateProduct(id, productStr, newImages, imagesToDeleteStr, userEmail);
            return ResponseEntity.ok(result);
        } catch (Exception ex) {
            logger.error("Error updating product", ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", ex.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable Integer id, @RequestParam String userEmail) {
        try {
            productService.deleteProduct(id, userEmail);
            return ResponseEntity.ok(Map.of("message", "Product deleted successfully"));
        } catch (Exception e) {
            logger.error("Error deleting product", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", e.getMessage()));
        }
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllProducts(@RequestParam String userEmail, @RequestParam String role) {
        try {
            var result = productService.getAllProducts(userEmail, role);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            logger.error("Error fetching all products", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", e.getMessage()));
        }
    }

    // Add these endpoints to your existing ProductController
    @GetMapping("/supplier/{supplierId}")
    public ResponseEntity<?> getProductsBySupplier(@PathVariable Long supplierId,
                                                   @RequestParam String userEmail) {
        try {
            logger.info("Fetching products for supplier ID: {} by user: {}", supplierId, userEmail);
            var result = productService.getProductsBySupplier(supplierId, userEmail);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            logger.error("Error fetching supplier products", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", e.getMessage()));
        }
    }

    @GetMapping("/supplier")
    public ResponseEntity<?> getSupplierProducts(@RequestParam Long supplierId,
                                                 @RequestParam String userEmail) {
        try {
            logger.info("Fetching active products for supplier ID: {}", supplierId);
            var result = productService.getActiveProductsBySupplier(supplierId, userEmail);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "products", result
            ));
        } catch (Exception e) {
            logger.error("Error fetching supplier products", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", e.getMessage()));
        }
    }

}