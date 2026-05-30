package com.anouar.elearning.quiz.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class QuizSubmitRequest {

    @Valid
    @NotEmpty(message = "Answers are required")
    private List<AnswerRequest> answers;
}
