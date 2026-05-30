package com.anouar.elearning.quiz.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class QuizSubmissionResponse {
    private String id;
    private String quizId;
    private String learnerId;
    private Integer scoreObtained;
    private Integer maxScore;
    private Integer passingScore;
    private boolean passed;
    private List<SubmissionAnswerResponse> answers;
    private LocalDateTime submittedAt;
}
