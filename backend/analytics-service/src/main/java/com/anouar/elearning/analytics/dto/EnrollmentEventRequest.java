package com.anouar.elearning.analytics.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record EnrollmentEventRequest(
        @NotBlank String courseId, @NotBlank String teacherId, @NotBlank String courseTitle, @NotNull BigDecimal amount) {}

