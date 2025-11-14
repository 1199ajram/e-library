package Ziaat.E_library.Config;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private final JwtTokenValidator jwtTokenValidator;

    public JwtAuthenticationFilter(JwtTokenValidator jwtTokenValidator) {
        this.jwtTokenValidator = jwtTokenValidator;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        // Skip authentication for public endpoints
        String path = request.getRequestURI();
        if (shouldSkipFilter(path)) {
            filterChain.doFilter(request, response);
            return;
        }

        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);

            try {
                // Decode JWT locally (no external call)
                Map<String, Object> userInfo = jwtTokenValidator.validateAndDecodeToken(token);

                if (userInfo != null) {
                    String username = (String) userInfo.get("username");
                    String role = (String) userInfo.get("roleName");

                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(
                                    username,
                                    token, // Store token in credentials
                                    Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role))
                            );

                    // Store user details for later use
                    authentication.setDetails(userInfo);
                    SecurityContextHolder.getContext().setAuthentication(authentication);

                    logger.debug("User authenticated: {}", username);
                }
            } catch (Exception e) {
                logger.error("Token validation failed: {}", e.getMessage());
            }
        }

        filterChain.doFilter(request, response);
    }

    private boolean shouldSkipFilter(String path) {
        return path.startsWith("/api/public/") ||
                path.startsWith("/api/member-login") ||
                path.startsWith("/api/refresh-token") ||
                path.startsWith("/swagger-ui") ||
                path.startsWith("/v3/api-docs") ||
                path.startsWith("/swagger-resources") ||
                path.startsWith("/webjars") ||
                path.startsWith("/actuator") ||
                path.equals("/error");
    }
}