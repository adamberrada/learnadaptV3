package com.anouar.elearning.analytics.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import lombok.*;

@Entity
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class LearnerDailyActivity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false) private String learnerId;
    @Column(nullable = false) private LocalDate activityDate;
    private int timeSpentInMinutes;
    private int lessonsCompletedCount;
    private int quizTakenCount;
}

