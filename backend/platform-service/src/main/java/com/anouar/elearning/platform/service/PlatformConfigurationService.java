package com.anouar.elearning.platform.service;

import com.anouar.elearning.platform.dto.PlatformConfigDTO;
import com.anouar.elearning.platform.entity.PlatformConfiguration;
import com.anouar.elearning.platform.exception.ResourceNotFoundException;
import com.anouar.elearning.platform.repository.PlatformConfigurationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;

@Service
@Transactional
public class PlatformConfigurationService {

    private final PlatformConfigurationRepository configurationRepository;

    public PlatformConfigurationService(PlatformConfigurationRepository configurationRepository) {
        this.configurationRepository = configurationRepository;
    }

    public void initializeIfMissing(PlatformConfigDTO defaults) {
        configurationRepository.findBySingletonKey(PlatformConfiguration.GLOBAL_SINGLETON_KEY)
                .orElseGet(() -> configurationRepository.save(toEntity(defaults)));
    }

    @Transactional(readOnly = true)
    public PlatformConfigDTO getCurrentConfiguration() {
        return configurationRepository.findBySingletonKey(PlatformConfiguration.GLOBAL_SINGLETON_KEY)
                .map(this::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Platform configuration is not initialized."));
    }

    public PlatformConfigDTO update(PlatformConfigDTO request) {
        PlatformConfiguration configuration = configurationRepository
                .findBySingletonKey(PlatformConfiguration.GLOBAL_SINGLETON_KEY)
                .orElseGet(() -> toEntity(request));

        configuration.setSiteName(request.siteName());
        configuration.setSupportEmail(request.supportEmail());
        configuration.setMaintenanceMode(request.maintenanceMode());
        configuration.setAllowedLanguages(new ArrayList<>(request.allowedLanguages()));
        configuration.setMaxUploadSizeInMb(request.maxUploadSizeInMb());

        return toDto(configurationRepository.save(configuration));
    }

    private PlatformConfiguration toEntity(PlatformConfigDTO dto) {
        return PlatformConfiguration.builder()
                .singletonKey(PlatformConfiguration.GLOBAL_SINGLETON_KEY)
                .siteName(dto.siteName())
                .supportEmail(dto.supportEmail())
                .maintenanceMode(dto.maintenanceMode())
                .allowedLanguages(new ArrayList<>(dto.allowedLanguages()))
                .maxUploadSizeInMb(dto.maxUploadSizeInMb())
                .build();
    }

    private PlatformConfigDTO toDto(PlatformConfiguration configuration) {
        return new PlatformConfigDTO(
                configuration.getSiteName(),
                configuration.getSupportEmail(),
                configuration.isMaintenanceMode(),
                configuration.getAllowedLanguages(),
                configuration.getMaxUploadSizeInMb()
        );
    }
}
