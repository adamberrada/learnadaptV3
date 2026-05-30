package com.anouar.elearning.platform.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ContactRequestDTO(
        @NotBlank(message = "Sender name is required")
        @Size(max = 120, message = "Sender name must not exceed 120 characters")
        String senderName,

        @NotBlank(message = "Sender email is required")
        @Email(message = "Sender email must be valid")
        @Size(max = 180, message = "Sender email must not exceed 180 characters")
        String senderEmail,

        @NotBlank(message = "Subject is required")
        @Size(max = 180, message = "Subject must not exceed 180 characters")
        String subject,

        @NotBlank(message = "Message is required")
        @Size(max = 5000, message = "Message must not exceed 5000 characters")
        String message
) {
}
