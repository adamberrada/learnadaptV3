package com.anouar.elearning.course.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProgressResponse {
    private String learnerId;
    private String lessonId;
    private String courseId;
    private boolean completed;
    private int completedLessons;
    private int totalLessons;
    private double percentage;
    private LocalDateTime completedAt;
}
