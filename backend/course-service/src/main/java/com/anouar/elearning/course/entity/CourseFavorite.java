package com.anouar.elearning.course.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "course_favorites", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"learner_id", "course_id"})
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseFavorite {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "learner_id", nullable = false)
    private String learnerId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
