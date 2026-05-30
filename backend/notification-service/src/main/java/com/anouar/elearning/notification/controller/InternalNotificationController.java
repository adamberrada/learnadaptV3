package com.anouar.elearning.notification.controller;

import com.anouar.elearning.notification.dto.ApiResponse;
import com.anouar.elearning.notification.dto.NotificationRequest;
import com.anouar.elearning.notification.entity.Notification;
import com.anouar.elearning.notification.service.NotificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/internal/notifications")
@RequiredArgsConstructor
public class InternalNotificationController {
    private final NotificationService notificationService;

    @PostMapping("/send")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<Notification> send(@Valid @RequestBody NotificationRequest request) {
        return ApiResponse.<Notification>builder()
                .success(true)
                .message("Notification routed successfully")
                .data(notificationService.routeNotification(request))
                .build();
    }
}
