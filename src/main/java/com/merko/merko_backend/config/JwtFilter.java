package com.merko.merko_backend.config;

import com.merko.merko_backend.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtFilter.class);
    private final JwtUtil jwtUtil;

    public JwtFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String requestURI = request.getRequestURI();
        String method = request.getMethod();

        logger.info("=== JWT Filter Processing: {} {} ===", method, requestURI);

        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            logger.info("Token found in header");

            try {
                String email = jwtUtil.extractEmail(token);
                String role = jwtUtil.extractRole(token);

                logger.info("Extracted from token - Email: {}, Role: {}", email, role);

                if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    logger.info("Setting up authentication context");

                    if (jwtUtil.validateToken(token, email)) {
                        logger.info("Token validation successful");

                        // Assign role based on token
                        List<SimpleGrantedAuthority> authorities;
                        if ("MERCHANT".equals(role)) {
                            authorities = List.of(new SimpleGrantedAuthority("ROLE_MERCHANT"));
                            logger.info("Granted ROLE_MERCHANT authority");
                        } else {
                            authorities = List.of(new SimpleGrantedAuthority("ROLE_SUPPLIER"));
                            logger.info("Granted ROLE_SUPPLIER authority");
                        }

                        UsernamePasswordAuthenticationToken authToken =
                                new UsernamePasswordAuthenticationToken(email, null, authorities);

                        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authToken);

                        logger.info("Authentication context set successfully for user: {} with authorities: {}",
                                email, authorities);
                    } else {
                        logger.warn("Token validation failed for email: {}", email);
                    }
                } else {
                    if (email == null) {
                        logger.warn("Email extraction failed");
                    }
                    if (SecurityContextHolder.getContext().getAuthentication() != null) {
                        logger.info("Authentication already exists in security context");
                    }
                }
            } catch (Exception e) {
                logger.error("Error processing JWT token: ", e);
            }
        } else {
            logger.info("No Bearer token found in Authorization header");
        }

        logger.info("=== JWT Filter completed, proceeding to next filter ===");
        filterChain.doFilter(request, response);
    }

}
