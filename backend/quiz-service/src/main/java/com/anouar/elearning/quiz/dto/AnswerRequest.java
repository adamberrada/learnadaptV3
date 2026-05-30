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
public class AnswerRequest {

    @NotBlank(message = "Question id is required")
    private String questionId;

    @NotBlank(message = "Option id is required")
    private String optionId;
}
