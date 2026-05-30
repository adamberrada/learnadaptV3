package com.anouar.elearning.platform.config;

import com.anouar.elearning.platform.dto.PlatformConfigDTO;
import com.anouar.elearning.platform.service.PlatformConfigurationService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner initializePlatformConfiguration(PlatformConfigurationService configurationService) {
        return args -> configurationService.initializeIfMissing(new PlatformConfigDTO(
                "E-Learning Adaptive",
                "support@elearning.local",
                false,
                List.of("fr", "en"),
                50
        ));
    }
}
