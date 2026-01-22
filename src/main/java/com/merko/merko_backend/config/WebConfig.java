package com.merko.merko_backend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private static final Logger logger = LoggerFactory.getLogger(WebConfig.class);

    @Value("${file.upload-dir:uploads}")
    private String uploadDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Get the absolute path to the uploads directory with proper normalization
        Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();

        logger.info("Configuring resource handler for UPLOADS directory: {}", uploadPath.toString());
        logger.info("Upload directory exists: {}", java.nio.file.Files.exists(uploadPath));

        // Serve files from /uploads/** pattern
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + uploadPath + "/")
                .setCachePeriod(3600)
                .resourceChain(true);

        logger.info("Resource handler configured for pattern: /uploads/** -> file:" + uploadPath + "/");
    }

    // NO CORS configuration here - it's already handled by SecurityConfig
}
