package com.anouar.elearning.quiz.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class QuestionDifficultyResponse {
    private String questionId;
    private String questionText;
    private long totalAttempts;
    private long failedAttempts;
    private double failureRate;
}
