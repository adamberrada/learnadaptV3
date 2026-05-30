package com.anouar.elearning.platform.repository;

import com.anouar.elearning.platform.entity.PlatformConfiguration;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PlatformConfigurationRepository extends JpaRepository<PlatformConfiguration, Long> {

    Optional<PlatformConfiguration> findBySingletonKey(String singletonKey);
}
