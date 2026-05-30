package com.anouar.elearning.quiz.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GlobalResultsResponse {
    private long quizCount;
    private long submissionCount;
    private double averageScore;
    private double passRate;
}
