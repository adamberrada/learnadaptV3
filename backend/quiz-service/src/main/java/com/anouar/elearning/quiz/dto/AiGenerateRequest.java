package com.anouar.elearning.quiz.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AiGenerateRequest {

    @NotBlank(message = "Course context is required")
    private String courseContext;

    @NotNull(message = "Question count is required")
    @Min(value = 1, message = "Question count must be positive")
    private Integer questionCount;

    @Min(value = 1, message = "Points per question must be positive")
    private Integer pointsPerQuestion;
}
