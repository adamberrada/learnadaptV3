package com.anouar.elearning.analytics.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDate;

public record LessonCompleteEventRequest(
        @NotBlank String learnerId,
        @Min(1) int minutesSpent,
        @Min(0) int lessonsCompleted,
        LocalDate activityDate) {}

