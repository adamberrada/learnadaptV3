package com.anouar.elearning.platform.dto;

import com.anouar.elearning.platform.entity.ContactStatus;

import java.time.LocalDateTime;

public record ContactMessageDTO(
        Long id,
        String senderName,
        String senderEmail,
        String subject,
        String message,
        ContactStatus status,
        String adminNotes,
        LocalDateTime createdAt,
        LocalDateTime resolvedAt
) {
}
