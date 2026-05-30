package com.anouar.elearning.platform.dto;

import com.anouar.elearning.platform.entity.FaqCategory;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public record FaqDTO(
        Long id,

        @NotBlank(message = "Question is required")
        @Size(max = 500, message = "Question must not exceed 500 characters")
        String question,

        @NotBlank(message = "Answer is required")
        @Size(max = 5000, message = "Answer must not exceed 5000 characters")
        String answer,

        @NotNull(message = "Category is required")
        FaqCategory category,

        @Min(value = 0, message = "Display order must be positive or zero")
        Integer displayOrder,

        Boolean isPublished,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
