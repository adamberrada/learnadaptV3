package com.anouar.elearning.notification.dto;

import com.anouar.elearning.notification.entity.NotificationType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record NotificationRequest(
        @NotBlank String recipientId, @NotBlank String title, @NotBlank String message, @NotNull NotificationType type) {}
