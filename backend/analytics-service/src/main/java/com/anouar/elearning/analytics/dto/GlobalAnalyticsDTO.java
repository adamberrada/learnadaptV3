package com.anouar.elearning.analytics.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public record GlobalAnalyticsDTO(
        long activeUsers,
        long globalEnrollments,
        BigDecimal totalRevenue,
        List<String> top5Categories,
        Map<String, Long> activityCurve,
        double generalEngagementRate) {}

