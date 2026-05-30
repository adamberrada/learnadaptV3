package com.anouar.elearning.analytics.controller;

import com.anouar.elearning.analytics.dto.ApiResponse;
import com.anouar.elearning.analytics.dto.LearnerDashboardDTO;
import com.anouar.elearning.analytics.service.AnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/analytics/learner")
@RequiredArgsConstructor
public class LearnerAnalyticsController {
    private final AnalyticsService service;

    @GetMapping("/dashboard")
    public ApiResponse<LearnerDashboardDTO> dashboard(Authentication auth) {
        return ApiResponse.<LearnerDashboardDTO>builder().success(true).message("Learner dashboard generated").data(service.learnerDashboard(auth.getName())).build();
    }
}

