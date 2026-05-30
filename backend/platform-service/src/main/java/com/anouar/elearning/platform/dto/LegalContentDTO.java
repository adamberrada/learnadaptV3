package com.anouar.elearning.platform.dto;

import com.anouar.elearning.platform.entity.LegalContentType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public record LegalContentDTO(
        Long id,

        @NotNull(message = "Content type is required")
        LegalContentType contentType,

        @NotBlank(message = "HTML content is required")
        @Size(max = 20000, message = "HTML content must not exceed 20000 characters")
        String htmlContent,

        Integer version,
        Boolean isActive,
        LocalDateTime updatedAt
) {
}
