package com.anouar.elearning.platform.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.List;

public record PlatformConfigDTO(
        @NotBlank(message = "Site name is required")
        @Size(max = 120, message = "Site name must not exceed 120 characters")
        String siteName,

        @NotBlank(message = "Support email is required")
        @Email(message = "Support email must be valid")
        @Size(max = 180, message = "Support email must not exceed 180 characters")
        String supportEmail,

        boolean maintenanceMode,

        @NotEmpty(message = "Allowed languages are required")
        List<@NotBlank(message = "Language code must not be blank") @Size(max = 12) String> allowedLanguages,

        @Min(value = 1, message = "Max upload size must be at least 1 MB")
        @Max(value = 1024, message = "Max upload size must not exceed 1024 MB")
        int maxUploadSizeInMb
) {
}
