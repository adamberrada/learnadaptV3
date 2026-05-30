package com.anouar.elearning.analytics.controller;

import com.anouar.elearning.analytics.dto.ApiResponse;
import com.anouar.elearning.analytics.dto.GlobalAnalyticsDTO;
import com.anouar.elearning.analytics.service.AnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/analytics/admin")
@RequiredArgsConstructor
public class AdminAnalyticsController {
    private final AnalyticsService service;

    @GetMapping("/platform-summary")
    public ApiResponse<GlobalAnalyticsDTO> summary() {
        return ApiResponse.<GlobalAnalyticsDTO>builder().success(true).message("Platform summary generated").data(service.adminSummary()).build();
    }
}

