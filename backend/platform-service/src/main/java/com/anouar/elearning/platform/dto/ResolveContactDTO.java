package com.anouar.elearning.platform.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ResolveContactDTO(
        @NotBlank(message = "Admin notes are required")
        @Size(max = 5000, message = "Admin notes must not exceed 5000 characters")
        String adminNotes
) {
}
