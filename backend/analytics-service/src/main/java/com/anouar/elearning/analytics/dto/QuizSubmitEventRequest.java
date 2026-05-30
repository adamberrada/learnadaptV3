package com.anouar.elearning.analytics.dto;

import jakarta.validation.constraints.*;

public record QuizSubmitEventRequest(
        @NotBlank String quizId,
        @NotBlank String courseId,
        @NotBlank String learnerId,
        @DecimalMin("0.0") @DecimalMax("100.0") double scorePercent,
        @NotBlank String hardestQuestionId) {}

