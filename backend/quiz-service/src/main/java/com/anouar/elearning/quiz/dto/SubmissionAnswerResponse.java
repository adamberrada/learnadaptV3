package com.anouar.elearning.quiz.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SubmissionAnswerResponse {
    private String questionId;
    private String questionText;
    private String selectedOptionId;
    private String selectedOptionText;
    private boolean correct;
    private Integer pointsAwarded;
    private Integer questionPoints;
}
