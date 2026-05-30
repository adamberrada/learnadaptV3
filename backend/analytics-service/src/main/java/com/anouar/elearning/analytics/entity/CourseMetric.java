package com.anouar.elearning.analytics.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import lombok.*;

@Entity
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class CourseMetric {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true) private String courseId;
    @Column(nullable = false) private String teacherId;
    @Column(nullable = false) private String title;
    private long totalEnrollments;
    private double completionRate;
    private double averageRating;
    @Column(nullable = false) private BigDecimal totalRevenue;
    @Column(nullable = false) private Instant updatedAt;
}

