package com.anouar.elearning.analytics.dto;

import java.math.BigDecimal;
import java.util.List;

public record TeacherDashboardDTO(
        String teacherId,
        BigDecimal totalRevenue,
        long totalEnrollments,
        String mostPopularCourse,
        double averageQuizSuccessRate,
        List<String> atRiskLearners) {}

