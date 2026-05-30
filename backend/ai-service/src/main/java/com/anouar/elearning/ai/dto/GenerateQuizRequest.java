package com.anouar.elearning.ai.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record GenerateQuizRequest(
        @NotBlank(message = "Course content is required")
        @JsonAlias({"content", "courseContext"})
        String courseContent,

        @Min(value = 1, message = "Question count must be at least 1")
        @Max(value = 50, message = "Question count must not exceed 50")
        @JsonAlias({"numberOfQuestions"})
        int questionCount,

        @NotBlank(message = "Difficulty is required")
        @JsonAlias({"level"})
        String difficulty
) {
}
