package com.anouar.elearning.notification.repository;

import com.anouar.elearning.notification.entity.NotificationSetting;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationSettingRepository extends JpaRepository<NotificationSetting, Long> {
    Optional<NotificationSetting> findByUserId(String userId);
}
