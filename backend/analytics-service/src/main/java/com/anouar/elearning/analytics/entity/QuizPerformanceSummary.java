package com.anouar.elearning.analytics.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class QuizPerformanceSummary {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true) private String quizId;
    @Column(nullable = false) private String courseId;
    private long totalAttempts;
    private double globalSuccessRate;
    private String hardestQuestionId;
}

