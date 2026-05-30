package com.anouar.elearning.notification.service;

import com.anouar.elearning.notification.dto.NotificationRequest;
import com.anouar.elearning.notification.dto.NotificationSettingRequest;
import com.anouar.elearning.notification.entity.*;
import com.anouar.elearning.notification.exception.NotFoundException;
import com.anouar.elearning.notification.repository.NotificationRepository;
import com.anouar.elearning.notification.repository.NotificationSettingRepository;
import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final NotificationSettingRepository settingRepository;
    private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();

    public SseEmitter subscribe(String userId) {
        SseEmitter emitter = new SseEmitter(0L);
        emitters.put(userId, emitter);
        emitter.onCompletion(() -> emitters.remove(userId));
        emitter.onTimeout(() -> emitters.remove(userId));
        return emitter;
    }

    public Notification routeNotification(NotificationRequest request) {
        NotificationSetting setting = getOrCreateSettings(request.recipientId());
        Notification saved = null;
        if (setting.isEnableInApp()) {
            saved = notificationRepository.save(Notification.builder()
                    .recipientId(request.recipientId())
                    .title(request.title())
                    .message(request.message())
                    .status(NotificationStatus.UNREAD)
                    .type(request.type())
                    .createdAt(Instant.now())
                    .build());
            pushRealtime(saved);
        }
        if (setting.isEnableEmail()) {
            log.info("[EMAIL SENT] To: {} | Subject: {} | Body: {}", request.recipientId(), request.title(), request.message());
        }
        return saved;
    }

    public List<Notification> getUserNotifications(String userId, NotificationStatus status) {
        if (status == null) return notificationRepository.findByRecipientIdOrderByCreatedAtDesc(userId);
        return notificationRepository.findByRecipientIdAndStatusOrderByCreatedAtDesc(userId, status);
    }

    public Notification markAsRead(String userId, Long notificationId) {
        Notification notification = notificationRepository
                .findByIdAndRecipientId(notificationId, userId)
                .orElseThrow(() -> new NotFoundException("Notification not found"));
        notification.setStatus(NotificationStatus.READ);
        return notificationRepository.save(notification);
    }

    public int markAllAsRead(String userId) {
        List<Notification> notifications = notificationRepository.findByRecipientIdAndStatusOrderByCreatedAtDesc(userId, NotificationStatus.UNREAD);
        notifications.forEach(n -> n.setStatus(NotificationStatus.READ));
        notificationRepository.saveAll(notifications);
        return notifications.size();
    }

    public NotificationSetting getSettings(String userId) {
        return getOrCreateSettings(userId);
    }

    public NotificationSetting updateSettings(String userId, NotificationSettingRequest request) {
        NotificationSetting setting = getOrCreateSettings(userId);
        setting.setEnableEmail(request.enableEmail());
        setting.setEnableInApp(request.enableInApp());
        setting.setChannelType(request.channelType());
        setting.setUpdatedAt(Instant.now());
        return settingRepository.save(setting);
    }

    private NotificationSetting getOrCreateSettings(String userId) {
        return settingRepository.findByUserId(userId).orElseGet(() -> settingRepository.save(NotificationSetting.builder()
                .userId(userId)
                .enableEmail(true)
                .enableInApp(true)
                .channelType(ChannelType.SYSTEM)
                .updatedAt(Instant.now())
                .build()));
    }

    private void pushRealtime(Notification notification) {
        SseEmitter emitter = emitters.get(notification.getRecipientId());
        if (emitter == null) return;
        try {
            emitter.send(SseEmitter.event().name("notification").data(notification));
        } catch (IOException e) {
            emitters.remove(notification.getRecipientId());
        }
    }
}
