package com.anouar.elearning.auth.service;

import com.anouar.elearning.auth.dto.LoginRequest;
import com.anouar.elearning.auth.dto.RegisterRequest;
import com.anouar.elearning.auth.dto.UserResponse;
import com.anouar.elearning.auth.entity.ERole;
import com.anouar.elearning.auth.entity.User;
import com.anouar.elearning.auth.exception.UserAlreadyExistsException;
import com.anouar.elearning.auth.repository.userRepository;
import com.anouar.elearning.auth.security.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final userRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    @Transactional
    public UserResponse registerUser(RegisterRequest registerRequest) {
        if (userRepository.findByEmail(registerRequest.getEmail()).isPresent()) {
            throw new UserAlreadyExistsException("Error: Email is already in use!");
        }

        String firstname = normalizeNamePart(registerRequest.getFirstname());
        String lastname = normalizeNamePart(registerRequest.getLastname());
        if ((firstname == null || lastname == null) && registerRequest.getName() != null) {
            String[] parts = registerRequest.getName().trim().split("\\s+", 2);
            firstname = firstname != null ? firstname : normalizeNamePart(parts[0]);
            lastname = lastname != null ? lastname : (parts.length > 1 ? normalizeNamePart(parts[1]) : "");
        }
        if (firstname == null) {
            throw new IllegalArgumentException("Firstname is required");
        }
        if (lastname == null) {
            lastname = "";
        }

        // Create new user
        User user = User.builder()
                .firstname(firstname)
                .lastname(lastname)
                .email(registerRequest.getEmail())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .build();

        String strRole = registerRequest.getRole();
        if (strRole == null) {
            user.setRole(ERole.ROLE_STUDENT);
        } else {
            switch (strRole.toUpperCase()) {
                case "ADMIN":
                    user.setRole(ERole.ROLE_ADMIN);
                    break;
                case "INSTRUCTOR":
                    user.setRole(ERole.ROLE_INSTRUCTOR);
                    break;
                default:
                    user.setRole(ERole.ROLE_STUDENT);
            }
        }

        userRepository.save(user);

        return mapToUserResponse(user, List.of(user.getRole().name()));
    }

    private String normalizeNamePart(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }

    public AuthResponse login(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        ResponseCookie jwtCookie = jwtUtils.generateJwtCookie(userDetails);

        User user = userRepository.findByEmail(userDetails.getUsername()).orElseThrow();
        
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        UserResponse userResponse = mapToUserResponse(user, roles);

        return new AuthResponse(userResponse, jwtCookie);
    }

    public UserResponse getCurrentUser(String email) {
        User user = userRepository.findByEmail(email).orElseThrow();
        return mapToUserResponse(user, List.of(user.getRole().name()));
    }

    private UserResponse mapToUserResponse(User user, List<String> roles) {
        return UserResponse.builder()
                .id(user.getId())
                .firstname(user.getFirstname())
                .lastname(user.getLastname())
                .email(user.getEmail())
                .roles(roles)
                .build();
    }

    public record AuthResponse(UserResponse userResponse, ResponseCookie jwtCookie) {}
}
