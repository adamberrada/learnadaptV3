package com.anouar.elearning.analytics.controller;

import com.anouar.elearning.analytics.dto.*;
import com.anouar.elearning.analytics.service.AnalyticsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/internal/analytics")
@RequiredArgsConstructor
public class InternalIngestionController {
    private final AnalyticsService service;

    @PostMapping("/events/enrollment")
    public ApiResponse<?> enrollment(@Valid @RequestBody EnrollmentEventRequest req) {
        return ApiResponse.builder().success(true).message("Enrollment event ingested").data(service.ingestEnrollment(req)).build();
    }

    @PostMapping("/events/lesson-complete")
    public ApiResponse<?> lessonComplete(@Valid @RequestBody LessonCompleteEventRequest req) {
        return ApiResponse.builder().success(true).message("Lesson complete event ingested").data(service.ingestLessonComplete(req)).build();
    }

    @PostMapping("/events/quiz-submit")
    public ApiResponse<?> quizSubmit(@Valid @RequestBody QuizSubmitEventRequest req) {
        return ApiResponse.builder().success(true).message("Quiz submit event ingested").data(service.ingestQuizSubmit(req)).build();
    }
}
