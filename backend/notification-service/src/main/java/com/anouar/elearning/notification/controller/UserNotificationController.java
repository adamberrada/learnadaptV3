package com.anouar.elearning.notification.controller;

import com.anouar.elearning.notification.dto.ApiResponse;
import com.anouar.elearning.notification.dto.NotificationSettingRequest;
import com.anouar.elearning.notification.entity.Notification;
import com.anouar.elearning.notification.entity.NotificationSetting;
import com.anouar.elearning.notification.entity.NotificationStatus;
import com.anouar.elearning.notification.service.NotificationService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class UserNotificationController {
    private final NotificationService notificationService;

    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter stream(Authentication authentication) {
        return notificationService.subscribe(authentication.getName());
    }

    @GetMapping
    public ApiResponse<List<Notification>> getMyNotifications(Authentication authentication, @RequestParam(required = false) NotificationStatus status) {
        return ApiResponse.<List<Notification>>builder()
                .success(true)
                .message("Notifications fetched successfully")
                .data(notificationService.getUserNotifications(authentication.getName(), status))
                .build();
    }

    @PutMapping("/{notificationId}/read")
    public ApiResponse<Notification> markAsRead(Authentication authentication, @PathVariable Long notificationId) {
        return ApiResponse.<Notification>builder()
                .success(true)
                .message("Notification marked as read")
                .data(notificationService.markAsRead(authentication.getName(), notificationId))
                .build();
    }

    @PutMapping("/read-all")
    public ApiResponse<Integer> markAllAsRead(Authentication authentication) {
        return ApiResponse.<Integer>builder()
                .success(true)
                .message("All notifications marked as read")
                .data(notificationService.markAllAsRead(authentication.getName()))
                .build();
    }

    @GetMapping("/settings")
    public ApiResponse<NotificationSetting> getSettings(Authentication authentication) {
        return ApiResponse.<NotificationSetting>builder()
                .success(true)
                .message("Notification settings fetched successfully")
                .data(notificationService.getSettings(authentication.getName()))
                .build();
    }

    @PutMapping("/settings")
    public ApiResponse<NotificationSetting> updateSettings(Authentication authentication, @Valid @RequestBody NotificationSettingRequest request) {
        return ApiResponse.<NotificationSetting>builder()
                .success(true)
                .message("Notification settings updated successfully")
                .data(notificationService.updateSettings(authentication.getName(), request))
                .build();
    }
}
