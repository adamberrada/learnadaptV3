package com.anouar.elearning.ai.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class HeaderAuthenticationFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String userId = request.getHeader("X-User-Id");
        String role = request.getHeader("X-User-Role");

        if (hasText(userId) && hasText(role)) {
            String normalizedRole = normalizeRole(role);
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    userId,
                    null,
                    List.of(new SimpleGrantedAuthority("ROLE_" + normalizedRole))
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);
    }

    private boolean hasText(String value) {
        return value != null && !value.isBlank();
    }

    private String normalizeRole(String role) {
        String value = role.trim().toUpperCase(Locale.ROOT).replace("ROLE_", "");
        return switch (value) {
            case "ENSEIGNANT", "INSTRUCTOR", "TEACHER" -> "TEACHER";
            case "APPRENANT", "STUDENT", "LEARNER" -> "LEARNER";
            case "ADMINISTRATEUR", "ADMIN" -> "ADMIN";
            case "SERVICE", "INTERNAL", "MICROSERVICE" -> "SERVICE";
            default -> value;
        };
    }
}
