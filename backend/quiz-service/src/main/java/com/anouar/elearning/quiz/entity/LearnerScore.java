package com.anouar.elearning.quiz.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "learner_scores", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"learner_id", "quiz_id"})
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LearnerScore {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "learner_id", nullable = false)
    private String learnerId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_id", nullable = false)
    private Quiz quiz;

    @Column(nullable = false)
    private Integer bestScore;

    @Column(nullable = false)
    private Integer attempts;

    @Column(nullable = false)
    private boolean passed;

    private LocalDateTime lastSubmittedAt;
}
