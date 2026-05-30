package com.anouar.elearning.analytics.dto;

import java.util.List;

public record LearnerDashboardDTO(
        String learnerId,
        long totalTimeSpentInMinutes,
        int totalLessonsCompleted,
        List<String> recentQuizzes,
        double engagementScore,
        List<String> adaptiveSuggestions) {}

