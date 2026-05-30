package com.anouar.elearning.analytics.controller;

import com.anouar.elearning.analytics.dto.ApiResponse;
import com.anouar.elearning.analytics.dto.TeacherDashboardDTO;
import com.anouar.elearning.analytics.service.AnalyticsService;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/analytics/teacher")
@RequiredArgsConstructor
public class TeacherAnalyticsController {
    private final AnalyticsService service;

    @GetMapping("/dashboard")
    public ApiResponse<TeacherDashboardDTO> dashboard(Authentication auth) {
        return ApiResponse.<TeacherDashboardDTO>builder().success(true).message("Teacher dashboard generated").data(service.teacherDashboard(auth.getName())).build();
    }

    @GetMapping("/courses/{courseId}/students")
    public ApiResponse<Map<String, Object>> students(@PathVariable String courseId) {
        return ApiResponse.<Map<String, Object>>builder().success(true).message("Students tracking generated").data(service.studentsByCourse(courseId)).build();
    }

    @GetMapping("/quizzes/{quizId}/difficulties")
    public ApiResponse<Map<String, Object>> quizDifficulties(@PathVariable String quizId) {
        return ApiResponse.<Map<String, Object>>builder().success(true).message("Quiz difficulties generated").data(service.quizDifficulties(quizId)).build();
    }
}

