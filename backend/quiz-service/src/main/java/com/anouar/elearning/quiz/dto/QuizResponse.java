package com.anouar.elearning.quiz.dto;

import com.anouar.elearning.quiz.entity.QuizStatus;
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
public class QuizResponse {
    private String id;
    private String courseId;
    private String chapterId;
    private String title;
    private String description;
    private Integer timeLimitInMinutes;
    private Integer passingScore;
    private Integer totalPoints;
    private QuizStatus status;
    private String createdBy;
    private List<QuestionResponse> questions;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
