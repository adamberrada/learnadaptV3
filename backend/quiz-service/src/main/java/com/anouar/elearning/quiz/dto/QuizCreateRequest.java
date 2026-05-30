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
public class QuizCreateRequest {

    @NotBlank(message = "Course id is required")
    private String courseId;

    @NotBlank(message = "Chapter id is required")
    private String chapterId;

    @NotBlank(message = "Title is required")
    private String title;

    private String description;

    @NotNull(message = "Time limit is required")
    @Min(value = 1, message = "Time limit must be positive")
    private Integer timeLimitInMinutes;

    @NotNull(message = "Passing score is required")
    @Min(value = 0, message = "Passing score must be greater than or equal to 0")
    private Integer passingScore;
}
