package com.anouar.elearning.auth.controller;

import com.anouar.elearning.auth.dto.*;
import com.anouar.elearning.auth.service.AuthService;
import com.anouar.elearning.auth.security.JwtUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final JwtUtils jwtUtils;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserResponse>> registerUser(@Valid @RequestBody RegisterRequest registerRequest) {
        UserResponse userResponse = authService.registerUser(registerRequest);
        return ResponseEntity.ok(ApiResponse.success("User registered successfully!", userResponse));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<UserResponse>> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        AuthService.AuthResponse authResponse = authService.login(loginRequest);

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, authResponse.jwtCookie().toString())
                .body(ApiResponse.success("Login successful!", authResponse.userResponse()));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<String>> logoutUser() {
        ResponseCookie cookie = jwtUtils.getCleanJwtCookie();
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(ApiResponse.success("You have been logged out!", null));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> currentUser(Authentication authentication) {
        UserResponse userResponse = authService.getCurrentUser(authentication.getName());
        return ResponseEntity.ok(ApiResponse.success("Current user retrieved successfully!", userResponse));
    }

    @GetMapping("/health")
    public ResponseEntity<ApiResponse<String>> checkStatus() {
        return ResponseEntity.ok(ApiResponse.success("Authentication service is up and running!", "Healthy"));
    }
}
