package com.anouar.elearning.quiz.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CourseAnalyticsResponse {
    private String courseId;
    private long quizCount;
    private long submissionCount;
    private double averageScore;
    private double passRate;
    private List<QuestionDifficultyResponse> difficultQuestions;
}
