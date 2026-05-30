package com.anouar.elearning.quiz.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "ai_recommendations")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AIRecommendation {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    private String learnerId;

    @Column(nullable = false)
    private String quizId;

    @Column(length = 5000)
    private String detectedDifficulties;

    @ElementCollection
    @CollectionTable(name = "ai_recommended_lessons", joinColumns = @JoinColumn(name = "recommendation_id"))
    @Column(name = "recommended_lesson")
    @Builder.Default
    private List<String> recommendedLessons = new ArrayList<>();

    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
