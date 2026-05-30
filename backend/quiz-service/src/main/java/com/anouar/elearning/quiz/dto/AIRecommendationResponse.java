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
public class AIRecommendationResponse {
    private String id;
    private String learnerId;
    private String quizId;
    private String detectedDifficulties;
    private List<String> recommendedLessons;
    private LocalDateTime createdAt;
}
