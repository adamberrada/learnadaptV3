package com.anouar.elearning.notification.repository;

import com.anouar.elearning.notification.entity.Notification;
import com.anouar.elearning.notification.entity.NotificationStatus;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByRecipientIdOrderByCreatedAtDesc(String recipientId);
    List<Notification> findByRecipientIdAndStatusOrderByCreatedAtDesc(String recipientId, NotificationStatus status);
    Optional<Notification> findByIdAndRecipientId(Long id, String recipientId);
}
