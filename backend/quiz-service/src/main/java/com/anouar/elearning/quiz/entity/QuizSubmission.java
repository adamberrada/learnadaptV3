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
@Table(name = "quiz_submissions")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuizSubmission {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_id", nullable = false)
    private Quiz quiz;

    @Column(nullable = false)
    private String learnerId;

    @Column(nullable = false)
    private Integer scoreObtained;

    @Column(nullable = false)
    private Integer maxScore;

    @Column(nullable = false)
    private boolean passed;

    @OneToMany(mappedBy = "submission", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<SubmissionAnswer> answers = new ArrayList<>();

    private LocalDateTime submittedAt;

    @PrePersist
    protected void onSubmit() {
        submittedAt = LocalDateTime.now();
    }
}
