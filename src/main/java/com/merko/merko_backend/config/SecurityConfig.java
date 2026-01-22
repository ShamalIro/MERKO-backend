package com.merko.merko_backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    //Password Encoder
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    //Security Filter Chain
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // Enable CORS
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                // Disable CSRF for APIs
                .csrf(csrf -> csrf.disable())
                // Allow H2 console
                .headers(headers -> headers.frameOptions(frameOptions -> frameOptions.disable()))
                // Define public and secured endpoints
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers(
                                "/", "/index.html",
                                "/css/**", "/js/**", "/images/**", "/favicon.ico",
                                "/api/login",        // LOGIN ENDPOINT - MUST BE PUBLIC
                                "/api/test-password", // TEST ENDPOINT
                                "/api/users/**",     // user endpoints
                                "/api/admin/**",     // admin endpoints (fixed pattern)
                                "/api/delivery/**",  // delivery endpoints
                                "/api/suppliers/**", // supplier endpoints
                                "/uploads/**",
                                "/api/merchants/**", // merchant endpoints
                                "/api/products/**",  // Make all product endpoints public
                                "/api/test/**",      // test endpoints
                                "/api/auth/**",      // auth endpoints
                                "/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html",
                                "/h2-console/**",     // h2 console
                                "/api/cart/**",      // 🔥 ADD THIS LINE - Allow cart endpoints
                                "/api/checkout/**",  // 🔥 ADD THIS LINE - Allow checkout endpoints
                                "/api/orders/**",
                                "/api/inquiries/**",      // inquiry endpoints
                                "/api/public/inquiries/**", // public inquiry endpoints
                                "/api/open/inquiries/**"  // open inquiry endpoints

                        ).permitAll()
                        .anyRequest().authenticated()
                )

                .httpBasic(httpBasic -> httpBasic.disable());

        return http.build();
    }

    //CORS Configuration
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(Arrays.asList("*")); // allow all origins (dev only)
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(false); // Fix: Set to false to allow wildcard origins

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}