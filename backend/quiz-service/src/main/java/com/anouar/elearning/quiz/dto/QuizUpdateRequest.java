package com.anouar.elearning.quiz.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class QuizUpdateRequest {

    @NotBlank(message = "Title is required")
    private String title;

    private String description;
}
