package com.anouar.elearning.auth.config;

import com.anouar.elearning.auth.entity.ERole;
import com.anouar.elearning.auth.entity.User;
import com.anouar.elearning.auth.repository.userRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final userRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.findByEmail("admin@elearning.com").isEmpty()) {
            User admin = User.builder()
                    .firstname("Admin")
                    .lastname("System")
                    .email("admin@elearning.com")
                    .password(passwordEncoder.encode("admin123"))
                    .role(ERole.ROLE_ADMIN)
                    .build();
            userRepository.save(admin);
            System.out.println("Default admin user created: admin@elearning.com / admin123");
        }
    }
}
